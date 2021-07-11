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

        def regex = /(?<languageCode>[a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*/
        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        matcher.group('languageCode') in languageCodes
    }

    void init() {

        if (!languageCodes) {
            def inputStream = getClass().getClassLoader().getResourceAsStream('language-codes.txt')

            languageCodes = inputStream.readLines() as Set
        }
    }

}
