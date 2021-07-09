package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class ExtensionTag extends Validator{

    ExtensionTag() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def regex = /_[A-Z0-9_]* (?<URI>.*)/

        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        def uriInput = matcher.group('URI')
        new Uri().isValid(uriInput, gedcomVersion)
    }
}
