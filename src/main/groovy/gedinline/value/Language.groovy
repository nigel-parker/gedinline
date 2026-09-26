package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Language extends Validator {

    // Loaded once, eagerly, so that concurrent validation threads share one immutable set
    static final Set<String> languageCodes = loadLanguageCodes()

    Language() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def regex = /(?<languageCode>[a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*/
        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        matcher.group('languageCode') in languageCodes
    }

    private static Set<String> loadLanguageCodes() {
        Language.classLoader.getResourceAsStream('language-codes.txt').withStream { InputStream inputStream ->
            Collections.unmodifiableSet(inputStream.readLines() as Set<String>)
        }
    }

}
