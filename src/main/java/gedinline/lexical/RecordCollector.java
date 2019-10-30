package gedinline.lexical;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gedinline.main.GedcomException;
import gedinline.main.NullLogger;
import gedinline.main.StructureListener;
import gedinline.main.WarningSink;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * The RecordCollector reads the result from the BasicParser and assembles lines into records, simultaneously
 * converting InputLinePrecursors into InputLines
 */
public class RecordCollector {

    private InputRecord next = null;
    private GedcomVersion gedcomVersion;
    private WarningSink log;
    private StructureListener structureListener;
    private boolean trailerFound = false;
    private BasicParser basicParser;
    private Level previousLevel = new Level();
    private Stack<InputRecord> stack = new Stack<InputRecord>();
    private Set<Pointer> labels = Sets.newHashSet();
    private Set<Pointer> pointers = Sets.newHashSet();
    private boolean finished = false;

    public RecordCollector(InputStream inputStream) {
        this(inputStream, new NullLogger(), new StructureListener(new PrintWriter(new StringWriter())));
    }

    public RecordCollector(InputStream inputStream, WarningSink log, StructureListener structureListener) {
        this.log = log;
        this.structureListener = structureListener;

        try {
            basicParser = new BasicParser(inputStream, log);
        } catch (IOException e) {
            throw new RuntimeException("Problems reading GEDCOM file ", e);
        }

        gedcomVersion = basicParser.getGedcomVersion();
        structureListener.handleGedcomVersion(gedcomVersion);
    }

    public GedcomVersion getGedcomVersion() {
        return gedcomVersion;
    }

    public List<InputRecord> getInputRecords() {
        List<InputRecord> result = Lists.newArrayList();

        while (hasNext()) {
            result.add(next());
        }

        return result;
    }

    public boolean hasNext() {

        if (finished) {
            return false;
        } else if (next != null) {
            return true;
        }

        while (basicParser.hasNext()) {
            InputLinePrecursor inputLinePrecursor = basicParser.next();
            Level level = inputLinePrecursor.getLevel();

            try {
                InputLine inputLine = new InputLine(inputLinePrecursor, gedcomVersion, log);
                Pointer label = inputLine.getLabel();
                Pointer pointer = inputLine.getPointer();

                if (label != null) {
                    if (labels.contains(label)) {
                        throw new GedcomException("Duplicate occurrence of cross reference @" + label + "@");
                    }

                    labels.add(label);
                }

                if (pointer != null) {
                    pointers.add(pointer);
                }

                adjustStack(level);
                InputRecord inputRecord = new InputRecord(inputLine);

                if (stack.isEmpty()) {
                    structureListener.handleInputLine(inputLine);
                } else {
                    InputRecord parentRecord = stack.peek();
                    parentRecord.add(inputRecord);
                    structureListener.handleInputLine(parentRecord, inputLine, log);
                }

                stack.push(inputRecord);
            } catch (GedcomException e) {
                log.warning(inputLinePrecursor.getLineNumber(), e.getMessage());
            }

            previousLevel = level;

            if (next != null) {
                return true;
            }
        }

        adjustStack(new Level(0));
        Sets.SetView<Pointer> unsatisfiedPointers = Sets.difference(pointers, labels);

        if (!unsatisfiedPointers.isEmpty()) {
            Pointer s = unsatisfiedPointers.iterator().next();
            log.warning("Can't find pointer reference " + s + " in file");
        }

        if (!trailerFound) {
            log.warning("File does not end with a TRLR record");
        }

        finished = true;
        return next != null;
    }

    public InputRecord next() {
        InputRecord result = next;
        next = null;
        return result;
    }

    private void adjustStack(Level level) {
        int difference = Level.getDifference(previousLevel, level);

        for (int j = 0; j <= difference; j++) {
            InputRecord inputRecord = stack.pop();

            if (stack.isEmpty()) {
                addRecord(inputRecord);
            }
        }
    }

    private void addRecord(InputRecord inputRecord) {
        InputLine inputLine = inputRecord.getInputLine();
        Tag tag = inputLine.getTag();
        int lineNumber = inputLine.getLineNumber();

        if (trailerFound) {
            log.warning(lineNumber, "Found " + tag + " record after TRLR");
        }

        if (tag.isFinalTag()) {
            trailerFound = true;
        }

        next = inputRecord;
    }
}

