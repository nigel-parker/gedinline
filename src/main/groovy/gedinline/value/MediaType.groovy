package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class MediaType {

    String s
    GedcomVersion gedcomVersion

    MediaType(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {
        // somewhat simplified but should cover the majority of normal cases

        def mtChar = /[A-Za-z0-9+\-\.]/
        def mtToken = "$mtChar+"

        String regex70 = "$mtToken/$mtToken(;$mtToken=$mtToken)*"

        return s.matches(regex70);
    }
}
