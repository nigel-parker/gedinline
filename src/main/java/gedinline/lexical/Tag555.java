package gedinline.lexical;

import gedinline.main.GedcomException;

public class Tag555 extends Tag {

    public Tag555(String s) {
        super(s);

        if (s.length() > 31) {
            throw new GedcomException("Tag '" + s + "' is too long");
        }

        if (!s.matches("_?[a-zA-Z0-9]+")) {
            throw new GedcomException("Tags must consist of alphanumerics: '" + s + "'");
        }
    }

}
