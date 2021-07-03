package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class ExtensionTag extends Validator{

    ExtensionTag() {
    }

    ExtensionTag(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;

        assert gedcomVersion.is70();
    }

    boolean isValid() {

        def regex = /_[A-Z0-9_]* (?<URI>.*)/

        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        def uriInput = matcher.group('URI')
        def uri = new Uri(uriInput, gedcomVersion)

        uri.isValid()
    }
}
