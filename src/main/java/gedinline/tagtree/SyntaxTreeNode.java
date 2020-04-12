package gedinline.tagtree;

import com.google.common.base.Splitter;
import gedinline.lexical.Tag;
import gedinline.main.ValidatorBugException;
import org.apache.commons.lang.StringUtils;

import static gedinline.tagtree.Occurrence.*;

public class SyntaxTreeNode {

    private enum State {
        START, WAITING_FOR_LEVEL, WAITING_FOR_TAG, WAITING_FOR_SYNTAX_ELEMENT, WAITING_FOR_OCCURRENCE, FINISHED
    }

    private SyntaxPointer label;
    private int level;
    private Tag tag;
    private SubtreeReference subtreeReference;
    private SyntaxElementId syntaxElementId;
    private Occurrence occurrence;
    private State state = State.START;
    private String origin;

    private SyntaxTreeNode() {
    }

    public SyntaxTreeNode(String s) {

        origin = s;

        for (String token : Splitter.on(" ").omitEmptyStrings().split(s)) {

            switch (state) {
                case START:
                    if (token.startsWith("@")) {
                        label = new SyntaxPointer(token);
                        state = State.WAITING_FOR_LEVEL;
                    } else if (token.startsWith("+")) {
                        level = Integer.parseInt(token.substring(1));
                        state = State.WAITING_FOR_TAG;
                    } else {
                        level = 0;
                        readTagOrSubtreeReference(token);
                    }

                    break;

                case WAITING_FOR_LEVEL:
                    if (token.startsWith("+")) {
                        level = Integer.parseInt(token.substring(1));
                        state = State.WAITING_FOR_TAG;
                    } else {
                        level = 0;
                        readTagOrSubtreeReference(token);
                    }

                    break;

                case WAITING_FOR_TAG:
                    readTagOrSubtreeReference(token);
                    break;

                case WAITING_FOR_SYNTAX_ELEMENT:
                    if (token.startsWith("{")) {
                        syntaxElementId = null;
                        setOccurrence(token);
                        state = State.FINISHED;
                    } else {
                        syntaxElementId = new SyntaxElementId(token);
                        state = State.WAITING_FOR_OCCURRENCE;
                    }

                    break;

                case WAITING_FOR_OCCURRENCE:
                    setOccurrence(token);
                    state = State.FINISHED;
                    break;
            }
        }

        if (state != State.FINISHED) {
            throw new ValidatorBugException("Invalid tag instance '" + s + "'");
        }
    }

    private void readTagOrSubtreeReference(String token) {
        if (token.startsWith("<<")) {
            subtreeReference = new SubtreeReference(token);
            state = State.WAITING_FOR_OCCURRENCE;
        } else {
            tag = new Tag(token);
            state = State.WAITING_FOR_SYNTAX_ELEMENT;
        }
    }

    public SyntaxPointer getLabel() {
        return label;
    }

    public boolean hasLabel() {
        return label != null;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return tag.getTag();
    }

    public Occurrence getOccurrence() {
        return occurrence;
    }

    public String getOrigin() {
        return origin;
    }

    public SubtreeReference getSubtreeReference() {
        return subtreeReference;
    }

    public String getSyntaxElementId() {
        return syntaxElementId == null ? "" : syntaxElementId.toString();
    }

    public String getSyntaxElementIdStem() {
        String id = getSyntaxElementId();
        return id.endsWith("_551") ? id.substring(0, id.length() - 4) : id;
    }

    public Tag getTag() {
        return tag;
    }

    public boolean isSubtreeReference() {
        return !isTag();
    }

    public boolean isTag() {
        return tag != null;
    }

    private void setOccurrence(String token) {
        if (token.equals("{1:1}")) {
            occurrence = MANDATORY;
        } else if (token.equals("{0:1}")) {
            occurrence = OPTIONAL;
        } else if (token.equals("{0:3}")) {
            occurrence = UP_TO_3_TIMES;
        } else if (token.equals("{0:M}")) {
            occurrence = MULTIPLE;
        } else {
            throw new ValidatorBugException("Invalid occurrence " + token);
        }
    }

    public SyntaxTreeNode with(Occurrence occurrence) {
        SyntaxTreeNode syntaxTreeNode = new SyntaxTreeNode();

        syntaxTreeNode.label = label;
        syntaxTreeNode.level = level;
        syntaxTreeNode.subtreeReference = subtreeReference;
        syntaxTreeNode.syntaxElementId = syntaxElementId;
        syntaxTreeNode.tag = tag;
        syntaxTreeNode.occurrence = chooseOccurrence(occurrence, this.occurrence);

        return syntaxTreeNode;
    }

    private Occurrence chooseOccurrence(Occurrence o1, Occurrence o2) {
        if (o1.isMultiple() || o2.isMultiple()) {
            return MULTIPLE;
        }

        if (o1.is3Times() || o2.is3Times()) {
            return UP_TO_3_TIMES;
        }

        if (o1.isOptional() || o2.isOptional()) {
            return OPTIONAL;
        }

        return MANDATORY;
    }

    public String toString(int level) {
        String indent = StringUtils.repeat(" ", level * 5);

        String name = isTag() ? getName() : subtreeReference.toString();
        return StringUtils.rightPad(indent + name, 39) +
                StringUtils.rightPad(getSyntaxElementId(), 30) +
                " " +
                getOccurrence();
    }
}
