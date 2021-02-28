package gedinline.lexical;

import gedinline.main.GedcomException;

public class Tag70 extends Tag {

    public Tag70(String s) {
        super(s);

        if (!s.matches("[A-Z][A-Z0-9_]*|_[A-Z0-9_]+")) {
            throw new GedcomException("Tags must consist of alphanumerics: '" + s + "'");
        }
    }
}
