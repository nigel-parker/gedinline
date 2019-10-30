package gedinline.value;

import gedinline.lexical.GedcomVersion;

public class AgeAtEvent {

    private String s;
    private GedcomVersion gedcomVersion;

    public AgeAtEvent(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;
    }

    public boolean isValid() {
        String regex551 = "(<|>|)" +
                "(\\d{1,2}Y \\d{1,2}M \\d{1,3}D|" +
                "\\d{1,2}Y|" +
                "\\d{1,2}M|" +
                "\\d{1,3}D|" +
                "\\d{1,2}Y \\d{1,2}M|" +
                "\\d{1,2}Y \\d{1,3}D|" +
                "\\d{1,2}M \\d{1,3}D|" +
                "CHILD|" +
                "INFANT|" +
                "STILLBORN)";

        String regex555 = "(<|>|)" +
                "(\\d{1,3}Y \\d{1,2}M \\d{1,3}D|" +
                "\\d{1,3}Y|" +
                "\\d{1,2}M|" +
                "\\d{1,3}D|" +
                "\\d{1,3}Y \\d{1,2}M|" +
                "\\d{1,3}Y \\d{1,3}D|" +
                "\\d{1,3}M \\d{1,3}D|" +
                "CHILD|" +
                "INFANT|" +
                "STILLBORN)";

        String regex = gedcomVersion.is555() ? regex555 : regex551;
        return s.toUpperCase().matches(regex);
    }
}
