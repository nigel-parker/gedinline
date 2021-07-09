package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class MediaType extends Validator {

    MediaType() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        // somewhat simplified but should cover the majority of normal cases

        def mtChar = /[A-Za-z0-9+\-\.]/
        def mtToken = "$mtChar+"

        String regex70 = "$mtToken/$mtToken(;$mtToken=$mtToken)*"

        s.matches(regex70);
    }
}
