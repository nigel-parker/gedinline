package gedinline.lexical;

import gedinline.main.GedcomException;
import org.apache.commons.lang.StringUtils;

public class Tag {

    public static Tag CHAR = getInstance("CHAR");
    public static Tag CONC = getInstance("CONC");
    public static Tag CONT = getInstance("CONT");
    public static Tag DATE = getInstance("DATE");
    public static Tag FAM = getInstance("FAM");
    public static Tag GEDC = getInstance("GEDC");
    public static Tag HEAD = getInstance("HEAD");
    public static Tag INDI = getInstance("INDI");
    public static Tag MARR = getInstance("MARR");
    public static Tag NAME = getInstance("NAME");
    public static Tag PLAC = getInstance("PLAC");
    public static Tag SEX = getInstance("SEX");
    public static Tag SOUR = getInstance("SOUR");
    public static Tag SUBM = getInstance("SUBM");
    public static Tag TRLR = getInstance("TRLR");
    public static Tag VERS = getInstance("VERS");
    public static Tag XXXX = getInstance("xxxx");

    protected String tag;

    public static Tag getInstance(String s) {
        return getInstance(s, GedcomVersion.V_551);
    }

    public static Tag getInstance(String s, GedcomVersion gedcomVersion) {
        if (gedcomVersion.is555()) {
            return new Tag555(s);

        } else if (gedcomVersion.is70()) {
            return new Tag70(s);

        } else {
            return new Tag55(s);
        }
    }

    protected Tag(String s) {

        String s1 = StringUtils.trimToEmpty(s);

        if (StringUtils.isBlank(s1)) {
            throw new GedcomException("Invalid tag '" + s + "'");
        }

        this.tag = s;
    }

    public String getTag() {
        return tag;
    }

    public boolean isConcatenationTag() {
        return this.equals(CONC);
    }

    public boolean isContinuationTag() {
        return this.equals(CONT);
    }

    public boolean isConxTag() {
        return isConcatenationTag() || isContinuationTag();
    }

    public boolean isFinalTag() {
        return this.equals(TRLR);
    }

    public boolean isUserDefined() {
        return tag.startsWith("_");
    }

    public String toString() {
        return tag;
    }

    public boolean equals(Object that) {
        return this.tag.equals(((Tag) that).tag);
    }

    public int hashCode() {
        return tag.hashCode();
    }
}
