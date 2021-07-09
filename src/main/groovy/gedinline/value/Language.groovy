package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Language extends Validator {

    static Set languageCodes

    Language() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        init()

        s.toLowerCase() in languageCodes
    }

    void init() {

        if (!languageCodes) {
            def inputStream = getClass().getClassLoader().getResourceAsStream('language-codes.txt')

            languageCodes = inputStream.readLines() as Set
        }
    }

}
