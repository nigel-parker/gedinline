package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Language extends Validator {

    static Set languageCodes

    Language() {
    }

    Language(String s, GedcomVersion gedcomVersion) {
        this.s = s
        this.gedcomVersion = gedcomVersion

        assert gedcomVersion.is70()
    }

    boolean isValid() {

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
