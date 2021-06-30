package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Email {

    String s
    GedcomVersion gedcomVersion

    Email(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {

        // email addresses have many subtleties. This is a very simple compromise that covers the majority of
        // normal cases.

        return s.matches(/[^@]+?@[^@]+?\.[^@]+/);
    }
}
