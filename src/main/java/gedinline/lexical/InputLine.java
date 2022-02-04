package gedinline.lexical;

import com.google.common.base.Splitter;
import gedinline.main.GedcomException;
import gedinline.main.WarningSink;
import gedinline.value.Pointer;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

public class InputLine implements Line {

    private int lineNumber;
    private Level level;
    private Pointer label;
    private Tag tag;
    private Pointer pointer;
    private String value = "";
    private GedcomVersion gedcomVersion;

    public InputLine(InputLinePrecursor precursor, GedcomVersion gedcomVersion, WarningSink warningSink) {
        this.lineNumber = precursor.getLineNumber();
        this.level = precursor.getLevel();
        this.gedcomVersion = gedcomVersion;
        String line = precursor.getLine();

        Iterator<String> it = Splitter.on(" ").omitEmptyStrings().split(line).iterator();

        if (!it.hasNext()) {
            throw new GedcomException("Invalid GEDCOM line '" + precursor.getLevelAndLine() + "'");
        }

        String token = it.next();

        if (Pointer.looksValid(token)) {
            label = new Pointer(token, gedcomVersion);

            if (!label.isValid()) {
                warningSink.warning(lineNumber, "Invalid pointer '" + token + "'");
            }

            if (!it.hasNext()) {
                throw new GedcomException("Invalid GEDCOM line '" + precursor.getLevelAndLine() + "'");
            }

            if (level.getLevel() != 0) {
                throw new GedcomException("Cross-reference identifier " + token + " is not valid here");
            }

            token = it.next();
        }

        try {
            tag = Tag.getInstance(token, gedcomVersion);
        } catch (GedcomException e) {
            warningSink.warning(lineNumber, e.getMessage());
            tag = Tag.XXXX;
        }

        if (it.hasNext()) {
            token = it.next();

            if (Pointer.looksValid(token)) {
                pointer = new Pointer(token, gedcomVersion);

                if (!pointer.isValid()) {
                    warningSink.warning(lineNumber, "Invalid pointer '" + token + "'");
                }
            } else {
                value = StringUtils.substringAfter(line, tag + " ");
            }
        }
    }

    public GedcomVersion getGedcomVersion() {
        return gedcomVersion;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Level getLevel() {
        return level;
    }

    public Pointer getLabel() {
        return label;
    }

    public boolean hasLabel() {
        return label != null;
    }

    public Tag getTag() {
        return tag;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public String getValue() {
        return pointer == null ? value : pointer.toString();
    }

    public boolean isLevelZero() {
        return level.getLevel() == 0;
    }

    public String toString() {
        return lineNumber + ": " +
                level + " " +
                (label == null ? "" : label) + " " +
                tag + " " +
                (pointer == null ? "" : pointer) +
                value;
    }
}
