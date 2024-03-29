package gedinline.lexical;

import org.apache.commons.lang.StringUtils;

public enum GedcomVersion {

    V_55("5.5", "tag-tree.txt", "value-grammar.txt", "55"),
    V_551("5.5.1", "tag-tree.txt", "value-grammar.txt", "551"),
    V_555("5.5.5", "tag-tree-555.txt", "value-grammar-555.txt", ""),
    V_70("7.0", "tag-tree-70.txt", "value-grammar-70.txt", ""),
    OTHER("other", null, null);

    private String text;
    private String tagTree;
    private String valueGrammar;
    private String suffix;

    GedcomVersion(String text, String tagTree, String valueGrammar, String suffix) {
        this.text = text;
        this.tagTree = tagTree;
        this.valueGrammar = valueGrammar;
        this.suffix = suffix;
    }

    GedcomVersion(String text, String tagTree, String suffix) {
        this.text = text;
        this.tagTree = tagTree;
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix.equals("") ? suffix : "_" + suffix;
    }

    public String getTagTree() {
        return tagTree;
    }

    public String getValueGrammar() {
        return valueGrammar;
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

    public static GedcomVersion fromString(String s) {
        try {
            String s1 = "V_" + StringUtils.remove(s, '.');

            if (s1.startsWith("V_70")) {
                return V_70;
            } else {
                return GedcomVersion.valueOf(s1);
            }
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
