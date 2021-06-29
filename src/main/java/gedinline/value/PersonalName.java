package gedinline.value;

import gedinline.lexical.GedcomVersion;

public class PersonalName {

    private String s;
    private GedcomVersion gedcomVersion;

    public PersonalName(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    public boolean isValid() {
        String nameStr = "[^/\\t]+";
        String regex70 = "(" + nameStr + "|(" + nameStr + ")*/(" + nameStr + ")*/(" + nameStr + ")*)";

        return s.matches(regex70);
    }
}
