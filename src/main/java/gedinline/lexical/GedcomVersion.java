package gedinline.lexical;

import org.apache.commons.lang.StringUtils;

public class GedcomVersion {

    public static final GedcomVersion V_55 = new GedcomVersion("5.5");
    public static final GedcomVersion V_551 = new GedcomVersion("5.5.1");
    public static final GedcomVersion V_555 = new GedcomVersion("5.5.5");
    public static final GedcomVersion V_70 = new GedcomVersion("7.0");

    private String gedcomVersion;
    private String tagTree;

    public GedcomVersion(String gedcomVersion) {
        this.gedcomVersion = gedcomVersion;
    }

    public boolean isSupported() {
        return gedcomVersion.equals("5.5") || gedcomVersion.equals("5.5.1") || gedcomVersion.equals("5.5.5") || gedcomVersion.equals("7.0");
    }

    public boolean is555() {
        return this.equals(V_555);
    }

    public boolean is70() {
        return this.equals(V_70);
    }

    public String getSuffix() {
        return is555() ? "" : "_" + StringUtils.remove(gedcomVersion, ".");
    }

    public String toString() {
        return gedcomVersion;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GedcomVersion that = (GedcomVersion) o;
        return !(gedcomVersion != null ? !gedcomVersion.equals(that.gedcomVersion) : that.gedcomVersion != null);
    }

    public int hashCode() {
        return gedcomVersion != null ? gedcomVersion.hashCode() : 0;
    }
}
