package gedinline.lexical;

import org.apache.commons.lang.StringUtils;

public enum GedcomVersionNew {

    V_55("5.5", "tag-tree.txt", "_55"),
    V_551("5.5.1", "tag-tree.txt", "_551"),
    V_555("5.5.5", "tag-tree-555.txt", ""),
    V_70("7.0", "tag-tree-70.txt", ""),
    OTHER("other", null, null);

    private String text;
    private String tagTree;
    private String suffix;

    GedcomVersionNew(String text, String tagTree, String suffix) {
        this.text = text;
        this.tagTree = tagTree;
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isSupported() {
        return !this.equals(OTHER);
    }

    public boolean is555() {
        return this.equals(V_555);
    }

    public boolean is70() {
        return this.equals(V_70);
    }

    public String toString() {
        return text;
    }

    public static GedcomVersionNew fromString(String s) {
        try {
            String s1 = "V_" + StringUtils.remove(s, '.');
            return GedcomVersionNew.valueOf(s1);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
