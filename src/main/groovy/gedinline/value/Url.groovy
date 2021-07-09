package gedinline.value

import gedinline.lexical.*
import groovy.transform.*
import org.apache.commons.validator.routines.*

@CompileStatic
class Url extends Validator {

    Url() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def s2 = s.toLowerCase().startsWith('http') ? s : 'http://' + s

        new UrlValidator().isValid(s2)
    }
}
