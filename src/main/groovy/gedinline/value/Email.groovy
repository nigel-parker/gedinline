package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Email extends Validator {

    Email() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        // email addresses have many subtleties. This is a very simple compromise that covers the majority of
        // normal cases.

        s.matches(/[^@]+?@[^@]+?\.[^@]+/);
    }
}
