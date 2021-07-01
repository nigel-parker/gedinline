package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class SemanticVersionNumber extends Validator {

    SemanticVersionNumber() {
    }

    SemanticVersionNumber(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {

        def numericIdentifier = /(0|[1-9][0-9]*)/
        String regex70 = "${numericIdentifier}\\.${numericIdentifier}(\\.${numericIdentifier})?"

        s.matches(regex70);
    }
}
