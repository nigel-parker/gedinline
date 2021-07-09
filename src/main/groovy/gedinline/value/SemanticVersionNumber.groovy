package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class SemanticVersionNumber extends Validator {

    SemanticVersionNumber() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def numericIdentifier = /(0|[1-9][0-9]*)/
        String regex70 = "${numericIdentifier}\\.${numericIdentifier}(\\.${numericIdentifier})?"

        s.matches(regex70);
    }
}
