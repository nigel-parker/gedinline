package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Uuid extends Validator{

    Uuid() {
    }

    Uuid(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {

        s.matches(/[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}/);
    }
}
