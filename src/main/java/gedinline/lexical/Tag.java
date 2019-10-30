package gedinline.lexical;

import gedinline.main.GedcomException;
import org.apache.commons.lang.StringUtils;

public class Tag {

    private String tag;
    private GedcomVersion gedcomVersion;

    public Tag(String s) throws GedcomException {
        this(s, GedcomVersion.V_551);
    }

    public Tag(String s, GedcomVersion gedcomVersion) {
        this.gedcomVersion = gedcomVersion;

        String s1 = StringUtils.trimToEmpty(s);

        if (StringUtils.isBlank(s1)) {
            throw new GedcomException("Invalid tag '" + s + "'");
        }

        if (gedcomVersion.is555()) {
            if (s1.length() > 31) {
                throw new GedcomException("Tag '" + s + "' is too long");
            }

            if (!s1.matches("_?[a-zA-Z0-9]+")) {
                throw new GedcomException("Tags must consist of alphanumerics: '" + s + "'");
            }
        } else {
            if (!s1.matches("[a-zA-Z0-9_]+")) {
                throw new GedcomException("Tags must consist of alphanumerics: '" + s + "'");
            }
        }

        this.tag = s;
    }

    public String getTag() {
        return tag;
    }

    public boolean isConcatenationTag() {
        return tag.equals("CONC");
    }

    public boolean isContinuationTag() {
        return tag.equals("CONT");
    }

    public boolean isConxTag() {
        return isConcatenationTag() || isContinuationTag();
    }

    public boolean isUserDefined() {
        return tag.startsWith("_");
    }

    public boolean isEncodingTag() {
        return tag.equals("CHAR");
    }

    public boolean isFinalTag() {
        return tag.equals("TRLR");
    }

    public String toString() {
        return tag;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag pointer1 = (Tag) o;
        return !(tag != null ? !tag.equals(pointer1.tag) : pointer1.tag != null);
    }

    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }
}
