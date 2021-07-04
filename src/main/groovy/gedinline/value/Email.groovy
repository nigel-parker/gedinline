package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Email extends Validator{

    Email() {
    }

    Email(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {

        // email addresses have many subtleties. This is a very simple compromise that covers the majority of
        // normal cases.

        s.matches(/[^@]+?@[^@]+?\.[^@]+/);
    }
}