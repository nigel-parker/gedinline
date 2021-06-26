package gedinline.main;

import com.google.common.collect.Maps;
import gedinline.lexical.*;
import gedinline.tagtree.Occurrence;
import gedinline.tagtree.SyntaxTreeNode;
import gedinline.tagtree.TagTree;
import gedinline.tagtree.TagTreeGrammar;
import gedinline.value.ExpressionParser;
import gedinline.value.ParsingResult;
import gedinline.value.SyntaxElement;
import gedinline.value.ValueGrammar;

import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GedInlineValidator {

    private Map<Tag, TagTree> expandedGrammarMap = Maps.newHashMap();
    private String filename = "Unknown";
    private InputStream inputStream;
    private Map<String, String> recordMap = new HashMap<String, String>();
    private StructureListener structureListener;
    private ValueGrammar valueGrammar;
    private WarningCollector warningCollector;
    private GedcomVersion gedcomVersion;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
     * Validates the GEDCOM data found in the input stream and sends the summary report to the given PrintWriter. It
     * returns a boolean indicating whether the input was recognised as a GEDCOM file or not. Note that this value
     * is true even though the GEDCOM contains warnings. It only returns false if the file was not recognised as
     * GEDCOM at all.
     *
     * @return whether the input was recognised as GEDCOM
     */
    public boolean validate() {

        try {
            warningCollector = new WarningCollector(structureListener);
            structureListener.setFilename(filename);
            RecordCollector recordCollector = new RecordCollector(inputStream, warningCollector, structureListener);
            gedcomVersion = recordCollector.getGedcomVersion();
            TagTreeGrammar tagTreeGrammar = new TagTreeGrammar(gedcomVersion);
            valueGrammar = new ValueGrammar(gedcomVersion);
            initialiseDefaultRecordMap(gedcomVersion);
            propertyChangeSupport.addPropertyChangeListener(new GedcomListener());

            for (Map.Entry<String, String> entry : recordMap.entrySet()) {
                expandedGrammarMap.put(Tag.getInstance(entry.getKey()), tagTreeGrammar.expand(entry.getValue()));
            }

            while (recordCollector.hasNext()) {
                InputRecord inputRecord = recordCollector.next();
                InputLine inputLine = inputRecord.getInputLine();
                Tag tag = inputLine.getTag();

                try {
                    if (!recordMap.keySet().contains(tag.toString())) {
                        throw new GedcomException("Ignoring unknown record type at level 0: " + tag);
                    }

                    validateRecord(inputRecord, expandedGrammarMap.get(tag));
                } catch (GedcomException e) {
                    warningCollector.warning(inputLine, e.getMessage());
                }
            }

            structureListener.closeNormal();
            return true;

        } catch (InvalidFormatException e) {
            structureListener.closeError(e.getMessage());
            return false;

        } catch (RuntimeException e) {
            structureListener.closeError(e.getMessage());
            return false;
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

    private void validateRecord(InputRecord inputRecord1, TagTree tagTree1) {

        // Naming convention: this is a recursive method which handles a node and its immediate subnodes. Name1 is used
        // for any attributes of the node, name2 for subnodes.

        InputLine inputLine1 = inputRecord1.getInputLine();
        Tag tag1 = inputLine1.getTag();

        if (Debug.active(inputLine1)) {
            System.out.println("");
            System.out.println("--- validating inputLine " + inputLine1 + " and " + inputRecord1.getInputRecords().size() + " subsidiaries");
            System.out.println("--- vha tagTree \n" + tagTree1);
            System.out.println("");
        }

        try {
            propertyChangeSupport.firePropertyChange("", null, inputLine1);
            validateLine(inputLine1, tagTree1.getSyntaxTreeNode());

        } catch (Exception e) {
            warningCollector.warning(inputLine1, e.getMessage());
        }

        for (TagTree tagTree2 : tagTree1.getSubtrees()) {

            SyntaxTreeNode syntaxTreeNode2 = tagTree2.getSyntaxTreeNode();
            Tag tag2 = syntaxTreeNode2.getTag();
            Occurrence occurrence2 = syntaxTreeNode2.getOccurrence();
            int tagCount2 = inputRecord1.getTagCount(tag2);
            boolean occurrenceOk = checkOccurrenceRules(occurrence2, tagCount2);

            if (!occurrenceOk) {
                switch (occurrence2) {
                    case MANDATORY:
                        warningCollector.warning(inputLine1, "Mandatory tag " + tag2 + " not found under " + tag1);
                        break;

                    case OPTIONAL:
                        warningCollector.warning(inputLine1, "Tag " + tag2 + " occurs more than once");
                        break;

                    case UP_TO_3_TIMES:
                        warningCollector.warning(inputLine1, "Tag " + tag2 + " occurs too many times, maximum is 3 times");
                        break;
                }
            }
        }

        for (InputRecord inputrecord2 : inputRecord1.getInputRecords()) {
            InputLine inputLine2 = inputrecord2.getInputLine();
            Tag tag2 = inputLine2.getTag();

            if (!tag2.isUserDefined()) {
                Set<Tag> tags2 = tagTree1.getSubTags();

                if (!tags2.contains(tag2)) {
                    warningCollector.warning(inputLine2, "Tag " + tag2 + " is not allowed under " + tag1);
                }

                int lowestWarningCount = Integer.MAX_VALUE;
                TagTree tagTree3 = null;

                debug(inputLine1, "Getting subtrees with tag " + tag2);

                for (TagTree tagTree2 : tagTree1.getSubtrees(tag2)) {
                    debug(inputLine1, "tagTree2 = \n" + tagTree2);

                    warningCollector.setCountMode();
                    validateRecord(inputrecord2, tagTree2);
                    int warningCount = warningCollector.getCount();

                    debug(inputLine1, "warningCount = " + warningCount);

                    if (warningCount < lowestWarningCount) {
                        tagTree3 = tagTree2;
                        lowestWarningCount = warningCount;

                        debug(inputLine1, "lowestWarningCount = " + lowestWarningCount);
                    }
                }

                if (tagTree3 != null) {
                    validateRecord(inputrecord2, tagTree3); // recurse
                }
            }
        }
    }

    static boolean checkOccurrenceRules(Occurrence occurrence, int tagCount) {
        switch (occurrence) {

            case MANDATORY:
                return tagCount == 1;

            case OPTIONAL:
                return tagCount <= 1;

            case MULTIPLE:
                return true;

            case UP_TO_3_TIMES:
                return tagCount <= 3;

            case AT_LEAST_1:
                return tagCount >= 1;
        }

        return true;
    }

    private void debug(InputLine inputLine, String message) {
        if (Debug.active(inputLine)) {
            System.out.println("%%% " + message);
        }
    }

    private void validateLine(InputLine inputLine, SyntaxTreeNode syntaxTreeNode) {
        Tag inputLineTag = inputLine.getTag();

        if (inputLineTag.isUserDefined()) {
            return;
        }

        String syntaxElementId = syntaxTreeNode.getSyntaxElementId();
        String value = inputLine.getValue();

        if (syntaxTreeNode.hasLabel() && !inputLine.hasLabel()) {
            warningCollector.warning(inputLine, "Missing xref_id");
        }

        if (syntaxElementId.equals("")) {
            if (!value.equals("")) {
                warningCollector.warning(inputLine, "Tag " + inputLineTag + " has non empty content: " + value);
            }
        } else {
            SyntaxElement syntaxElement = valueGrammar.find(syntaxElementId);
            ExpressionParser expressionParser = new ExpressionParser(syntaxElement, valueGrammar, gedcomVersion);
            ParsingResult parsingResult = expressionParser.parse(value);

            if (Debug.active(inputLine)) {
                System.out.println("");
                System.out.println("--- looking at inputLine " + inputLine + " OK: " + parsingResult.parsedEverythingOk());
                System.out.println("");
            }


            if (!parsingResult.parsedEverythingOk()) {
                String detail;

                if (parsingResult.hasErrorMessage()) {
                    detail = parsingResult.getErrorMessage();
                } else {
                    detail = "is not a valid";
                }

                warningCollector.warning(inputLine, "Invalid content for " + inputLineTag + " tag: '" + value + "' " + detail + " <" + syntaxTreeNode.getSyntaxElementIdStem() + ">");
            }
        }
    }
}
