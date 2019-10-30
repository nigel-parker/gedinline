package gedinline.tagtree;

import gedinline.main.GedcomException;
import org.apache.commons.lang.StringUtils;

public class SyntaxPointer {

    private String pointer;
    private static final String AT = "@";

    public SyntaxPointer(String s) throws GedcomException {

        if (!looksValid(s) ||
                s.length() < 5 ||
                !s.endsWith(AT)) {
            throw new GedcomException("Invalid syntax pointer '" + s + "'");
        }

        this.pointer = s;
    }

    public String getPointer() {
        return pointer;
    }

    public static boolean looksValid(String s) {
        String s1 = StringUtils.trimToEmpty(s);
        return !StringUtils.isBlank(s1) && s1.startsWith(AT);
    }

    public String toString() {
        return pointer;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyntaxPointer pointer1 = (SyntaxPointer) o;
        return !(pointer != null ? !pointer.equals(pointer1.pointer) : pointer1.pointer != null);
    }

    public int hashCode() {
        return pointer != null ? pointer.hashCode() : 0;
    }
}
