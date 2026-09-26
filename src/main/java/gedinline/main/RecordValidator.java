package gedinline.main;

import gedinline.lexical.InputLine;
import gedinline.lexical.InputRecord;
import gedinline.lexical.GedcomVersion;
import gedinline.lexical.Tag;
import gedinline.main.RecordResult.LineValidated;
import gedinline.main.RecordResult.Warning;
import gedinline.tagtree.Occurrence;
import gedinline.tagtree.SyntaxTreeNode;
import gedinline.tagtree.TagTree;
import gedinline.value.ExpressionParser;
import gedinline.value.ParsingResult;
import gedinline.value.SyntaxElement;
import gedinline.value.ValueGrammar;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static gedinline.value.ExpressionParser.REPLACE_WITH;

/**
 * Validates one level-0 record against its expanded tag tree and returns the outcome as a RecordResult. It only
 * reads the grammar objects it is given, so one instance can be used from any number of threads at once.
 */
public class RecordValidator {

    private final ValueGrammar valueGrammar;
    private final GedcomVersion gedcomVersion;

    public RecordValidator(ValueGrammar valueGrammar, GedcomVersion gedcomVersion) {
        this.valueGrammar = valueGrammar;
        this.gedcomVersion = gedcomVersion;
    }

    public RecordResult validate(InputRecord inputRecord, TagTree tagTree) {
        return new Run().validate(inputRecord, tagTree);
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

    /**
     * The state of one validate() call: the result being built and the sink that feeds it.
     */
    private class Run {

        private final RecordResult result = new RecordResult();
        private final RecordingWarningCollector warningCollector = new RecordingWarningCollector();

        RecordResult validate(InputRecord inputRecord, TagTree tagTree) {
            warningCollector.redirect(result::add);
            validateRecord(inputRecord, tagTree);
            return result;
        }

        private void validateRecord(InputRecord inputRecord1, TagTree tagTree1) {

            // Naming convention: this is a recursive method which handles a node and its immediate subnodes. Name1 is used
            // for any attributes of the node, name2 for subnodes.

            InputLine inputLine1 = inputRecord1.getInputLine();
            Tag tag1 = inputLine1.getTag();

            if (Debug.active(inputLine1)) {
                System.out.println();
                System.out.println("--- validating inputLine " + inputLine1 + " and " + inputRecord1.getInputRecords().size() + " subsidiaries");
                System.out.println("--- vha tagTree \n" + StringUtils.abbreviate(tagTree1.toString(), 100));
                System.out.println();
            }

            if (warningCollector.isCountMode()) {
                // Trial run: warnings are only counted, and the listeners are not involved
                validateLineSafely(inputLine1, tagTree1);
            } else {
                // Real run: the line's own warnings are kept apart so that the replay can drop them if a listener
                // objects to the line, which is what happens in the single-threaded validator
                List<Warning> lineWarnings = new ArrayList<>();
                Consumer<Warning> previous = warningCollector.redirect(lineWarnings::add);
                validateLineSafely(inputLine1, tagTree1);
                warningCollector.redirect(previous);
                result.add(new LineValidated(inputLine1, lineWarnings));
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

                    debug(inputLine1, () -> "Getting subtrees with tag " + tag2);

                    for (TagTree tagTree2 : tagTree1.getSubtrees(tag2)) {
                        debug(inputLine1, () -> "tagTree2 = \n" + tagTree2);

                        warningCollector.setCountMode();
                        validateRecord(inputrecord2, tagTree2);
                        int warningCount = warningCollector.getCount();

                        debug(inputLine1, () -> "warningCount = " + warningCount);

                        if (warningCount < lowestWarningCount) {
                            tagTree3 = tagTree2;
                            lowestWarningCount = warningCount;

                            debug(inputLine1, () -> "lowestWarningCount = " + warningCount);
                        }
                    }

                    if (tagTree3 != null) {
                        validateRecord(inputrecord2, tagTree3); // recurse
                    }
                }
            }
        }

        private void validateLineSafely(InputLine inputLine, TagTree tagTree) {
            try {
                validateLine(inputLine, tagTree.getSyntaxTreeNode());
            } catch (Exception e) {
                warningCollector.warning(inputLine, e.getMessage());
            }
        }

        // The message is a Supplier so that nothing (in particular no tag tree) is rendered unless debugging is on
        private void debug(InputLine inputLine, Supplier<String> message) {
            if (Debug.active(inputLine)) {
                System.out.println("%%% " + message.get());
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
                    System.out.println();
                    System.out.println("--- looking at inputLine " + inputLine + " OK: " + parsingResult.parsedEverythingOk());
                    System.out.println();
                }

                if (!parsingResult.parsedEverythingOk()) {
                    String detail;

                    if (parsingResult.hasErrorMessage()) {
                        detail = parsingResult.getErrorMessage();
                    } else {
                        detail = "is not a valid";
                    }

                    final String message;

                    if (detail.startsWith(REPLACE_WITH)) {
                        message = StringUtils.substringAfter(detail, REPLACE_WITH);
                    } else if (parsingResult.isSuppressValue()) {
                        message = "the value " + detail + " <" + syntaxTreeNode.getSyntaxElementIdStem() + ">";
                    } else {
                        message = "'" + value + "' " + detail + " <" + syntaxTreeNode.getSyntaxElementIdStem() + ">";
                    }

                    String prefix = "Invalid content for " + inputLineTag + " tag: ";
                    warningCollector.warning(inputLine, prefix + message);
                }
            }
        }
    }
}
