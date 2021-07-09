package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class NonEmptyString extends Validator{

    NonEmptyString() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        s.size() >= 1
    }
}
