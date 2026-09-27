package gedinline.main;

import gedinline.lexical.*;
import gedinline.main.RecordResult.Event;
import gedinline.main.RecordResult.LineValidated;
import gedinline.main.RecordResult.Warning;
import gedinline.tagtree.TagTree;
import gedinline.tagtree.TagTreeGrammar;
import gedinline.value.ValueGrammar;

import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class GedInlineValidator {

    private Map<Tag, TagTree> expandedGrammarMap = new HashMap<>();
    private String filename;
    private InputStream inputStream;
    private Map<String, String> recordMap = new HashMap<>();
    private StructureListener structureListener;
    private WarningCollector warningCollector;
    private GedcomVersion gedcomVersion;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private int parallelism = Runtime.getRuntime().availableProcessors();

    /**
     * Creates a GedInlineValidator.
     *
     * @param inputFile   file containing the GEDCOM data to be analysed
     * @param printWriter where to send the report of the validation result
     */
    public GedInlineValidator(File inputFile, PrintWriter printWriter) throws FileNotFoundException {
        this(new FileInputStream(inputFile), inputFile.getName(), printWriter);
    }

    /**
     * Creates a GedInlineValidator.
     *
     * @param inputStream stream containing the GEDCOM data
     * @param filename    name of the source file. Will be mentioned in the output report.
     * @param printWriter where to send the report of the validation result
     */
    public GedInlineValidator(InputStream inputStream, String filename, PrintWriter printWriter) {
        this.inputStream = inputStream;
        this.filename = filename;
        this.structureListener = new StructureListener(printWriter);
    }

    /**
     * Sets the number of threads used to validate records. Reading and parsing the file is always done on the
     * calling thread; with a parallelism greater than 1 the records are validated on a pool of that many worker
     * threads. The report is identical whatever the value. Must be called before validate(). The default is the
     * number of available processors.
     *
     * @param parallelism number of validation threads, at least 1
     */
    public void setParallelism(int parallelism) {
        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism must be at least 1, was " + parallelism);
        }

        this.parallelism = parallelism;
    }

    public int getParallelism() {
        return parallelism;
    }

    /**
     * Validates the GEDCOM data found in the input stream and sends the summary report to the given PrintWriter. It
     * returns a boolean indicating whether the input was recognised as a GEDCOM file or not. Note that this value
     * is true even though the GEDCOM contains warnings. It only returns false if the file was not recognised as
     * GEDCOM at all.
     *
     * @return whether the input was recognised as GEDCOM
     */
    public boolean validate() {

        // Warnings raised while parsing are buffered here and replayed just before the validation result of the
        // record they belong to, which keeps the order of the report identical to that of a single-threaded run.
        RecordingWarningCollector parseBuffer = new RecordingWarningCollector();
        ExecutorService executor = null;

        try {
            warningCollector = new WarningCollector(structureListener);
            structureListener.setFilename(filename);
            RecordCollector recordCollector = new RecordCollector(inputStream, parseBuffer, structureListener);
            gedcomVersion = recordCollector.getGedcomVersion();
            TagTreeGrammar tagTreeGrammar = new TagTreeGrammar(gedcomVersion);
            ValueGrammar valueGrammar = new ValueGrammar(gedcomVersion);
            initialiseDefaultRecordMap(gedcomVersion);
            LinkListener linkListener = new LinkListener(warningCollector);
            propertyChangeSupport.addPropertyChangeListener(new GedcomListener());
            propertyChangeSupport.addPropertyChangeListener(linkListener);

            for (Map.Entry<String, String> entry : recordMap.entrySet()) {
                expandedGrammarMap.put(Tag.getInstance(entry.getKey()), tagTreeGrammar.expand(entry.getValue()));
            }

            RecordValidator recordValidator = new RecordValidator(valueGrammar, gedcomVersion);

            if (parallelism > 1) {
                executor = Executors.newFixedThreadPool(parallelism, new ValidationThreadFactory());
            }

            // Results are replayed strictly in record order. The deque bounds the number of records in flight,
            // so the reader waits for the workers rather than filling the heap.
            Deque<Slot> inFlight = new ArrayDeque<>();
            int maxInFlight = 2 * parallelism;

            while (recordCollector.hasNext()) {
                InputRecord inputRecord = recordCollector.next();
                InputLine inputLine = inputRecord.getInputLine();
                Tag tag = inputLine.getTag();
                List<Warning> parseWarnings = parseBuffer.drain();
                Future<RecordResult> future = null;

                if (!tag.isUserDefined()) {
                    if (!recordMap.containsKey(tag.toString())) {
                        parseWarnings.add(new Warning(inputLine.getLineNumber(), "Ignoring unknown record type at level 0: " + tag));
                    } else {
                        TagTree tagTree = expandedGrammarMap.get(tag);

                        if (executor == null) {
                            future = CompletableFuture.completedFuture(recordValidator.validate(inputRecord, tagTree));
                        } else {
                            future = executor.submit(() -> recordValidator.validate(inputRecord, tagTree));
                        }
                    }
                }

                inFlight.addLast(new Slot(parseWarnings, future));

                if (inFlight.size() >= maxInFlight) {
                    replay(inFlight.removeFirst());
                }
            }

            while (!inFlight.isEmpty()) {
                replay(inFlight.removeFirst());
            }

            replay(parseBuffer.drain());
            linkListener.reportMissingLinks();
            structureListener.closeNormal();
            return true;

        } catch (RuntimeException e) {
            replay(parseBuffer.drain());
            structureListener.closeError(e.getMessage());
            return false;

        } finally {
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    private void replay(Slot slot) {
        replay(slot.parseWarnings);

        if (slot.future != null) {
            replay(await(slot.future));
        }
    }

    private void replay(List<Warning> warnings) {
        for (Warning warning : warnings) {
            warningCollector.warning(warning.getLineNumber(), warning.getMessage());
        }
    }

    private void replay(RecordResult recordResult) {
        for (Event event : recordResult.getEvents()) {
            if (event instanceof Warning) {
                Warning warning = (Warning) event;
                warningCollector.warning(warning.getLineNumber(), warning.getMessage());
            } else {
                LineValidated lineValidated = (LineValidated) event;
                InputLine inputLine = lineValidated.getInputLine();

                try {
                    propertyChangeSupport.firePropertyChange("", null, inputLine);
                    replay(lineValidated.getWarnings());
                } catch (Exception e) {
                    warningCollector.warning(inputLine, e.getMessage());
                }
            }
        }
    }

    private static RecordResult await(Future<RecordResult> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Validation was interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause.getMessage(), cause);
            }
        }
    }

    /**
     * Returns an AnalysisStatistics object which contains a summary of the key validation results.
     */
    public AnalysisStatistics getAnalysisStatistics() {
        return structureListener.getAnalysisStatistics();
    }

    /**
     * Returns the GedcomVersion declared in the inputStream.
     */
    public GedcomVersion getGedcomVersion() {
        return gedcomVersion;
    }

    /**
     * Returns the number of warnings found. To be called after validation.
     */
    public int getNumberOfWarnings() {
        if (structureListener != null) {
            return structureListener.getNumberOfWarnings();
        } else {
            return 0;
        }
    }

    private void initialiseDefaultRecordMap(GedcomVersion gedcomVersion) {

        if (gedcomVersion.is70()) {
            recordMap.put("HEAD", "HEADER");
            recordMap.put("FAM", "FAMILY_RECORD");
            recordMap.put("INDI", "INDIVIDUAL_RECORD");
            recordMap.put("OBJE", "MULTIMEDIA_RECORD");
            recordMap.put("REPO", "REPOSITORY_RECORD");
            recordMap.put("SNOTE", "SHARED_NOTE_RECORD");
            recordMap.put("SOUR", "SOURCE_RECORD");
            recordMap.put("SUBM", "SUBMITTER_RECORD");
            recordMap.put("TRLR", "TRAILER");
        } else if (gedcomVersion.is555()) {
            recordMap.put("HEAD", "HEADER");
            recordMap.put("SUBM", "SUBMITTER_RECORD");
            recordMap.put("FAM", "FAM_GROUP_RECORD");
            recordMap.put("INDI", "INDIVIDUAL_RECORD");
            recordMap.put("OBJE", "MULTIMEDIA_RECORD");
            recordMap.put("NOTE", "NOTE_RECORD");
            recordMap.put("REPO", "REPOSITORY_RECORD");
            recordMap.put("SOUR", "SOURCE_RECORD");
            recordMap.put("TRLR", "TRAILER");
        } else {
            recordMap.put("HEAD", "HEADER");
            recordMap.put("SUBN", "SUBMISSION_RECORD");
            recordMap.put("FAM", "FAM_RECORD");
            recordMap.put("INDI", "INDIVIDUAL_RECORD");
            recordMap.put("OBJE", "MULTIMEDIA_RECORD");
            recordMap.put("NOTE", "NOTE_RECORD");
            recordMap.put("REPO", "REPOSITORY_RECORD");
            recordMap.put("SOUR", "SOURCE_RECORD");
            recordMap.put("SUBM", "SUBMITTER_RECORD");
            recordMap.put("TRLR", "TRAILER_RECORD");
        }
    }

    public String toString() {
        return "Validator for " + filename;
    }

    /**
     * The parse warnings that precede a record's validation result, and the result itself (null for records that
     * are not validated, i.e. user-defined or unknown record types).
     */
    private static final class Slot {

        private final List<Warning> parseWarnings;
        private final Future<RecordResult> future;

        Slot(List<Warning> parseWarnings, Future<RecordResult> future) {
            this.parseWarnings = parseWarnings;
            this.future = future;
        }
    }

    /**
     * Daemon threads, so that a library user's JVM is never kept alive by an idle pool.
     */
    private static final class ValidationThreadFactory implements ThreadFactory {

        private int counter = 0;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "gedinline-validator-" + (++counter));
            thread.setDaemon(true);
            return thread;
        }
    }
}
