package gedinline.value

import gedinline.lexical.*
import groovy.transform.*
import org.apache.commons.validator.routines.*

@CompileStatic
class Url extends Validator {

    Url() {
    }

    Url(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;
    }

    boolean isValid() {

        def s2 = s.toLowerCase().startsWith('http') ? s : 'http://' + s

        new UrlValidator().isValid(s2)
    }
}
