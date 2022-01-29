package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Text extends Validator {

    Text() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        s.size() >= 0
    }
}
