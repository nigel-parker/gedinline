package gedinline.lexical;

import gedinline.main.GedcomException;

public class Tag55 extends Tag {

    public Tag55(String s) {
        super(s);

        if (!s.matches("[a-zA-Z0-9_]+")) {
            throw new GedcomException("Tags must consist of alphanumerics: '" + s + "'");
        }
    }
}
