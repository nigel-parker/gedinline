package gedinline.lexical;

import gedinline.main.GedcomException;
import gedinline.main.GedcomUtil;
import org.apache.commons.lang.StringUtils;

public class Pointer {

    private String pointer;
    private String meat;

    public Pointer(String s) throws GedcomException {

        if (!looksValid(s) ||
                s.length() < 3 ||
                !s.endsWith("@")) {
            throw new GedcomException("Invalid pointer '" + s + "'");
        }

        String first = s.substring(1, 2);
        meat = s.substring(1, s.length() - 1);

        if (!GedcomUtil.isAlphanumeric(first)) {
            throw new GedcomException("Pointers must start with an alphanumeric '" + s + "'");
        }

        if (StringUtils.contains(meat, "@")) {
            throw new GedcomException("Pointers cannot contain @ characters '" + s + "'");
        }

        if (StringUtils.contains(meat, "!") || StringUtils.contains(meat, ":")) {
            throw new GedcomException("Pointers cannot currently contain ! or : characters '" + s + "'");
        }

        this.pointer = s;
    }

    public String getMeat() {
        return meat;
    }

    public String getPointer() {
        return pointer;
    }

    public static boolean looksValid(String s) {
        String s1 = StringUtils.trimToEmpty(s);
        return !StringUtils.isBlank(s1) && s1.startsWith("@") && !s1.startsWith("@#");
    }

    public String toString() {
        return pointer;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pointer pointer1 = (Pointer) o;
        return !(pointer != null ? !pointer.equals(pointer1.pointer) : pointer1.pointer != null);
    }

    public int hashCode() {
        return pointer != null ? pointer.hashCode() : 0;
    }
}
