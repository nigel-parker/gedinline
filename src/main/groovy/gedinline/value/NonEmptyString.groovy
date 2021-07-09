package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class NonEmptyString extends Validator{

    NonEmptyString() {
    }

    NonEmptyString(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {
        s.size() >= 1
    }
}
