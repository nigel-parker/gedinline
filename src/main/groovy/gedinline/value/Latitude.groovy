package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Latitude extends Validator {

    Latitude() {
    }

    Latitude(String s, GedcomVersion gedcomVersion) {
        this.s = s
        this.gedcomVersion = gedcomVersion

        assert gedcomVersion.is70()
    }

    boolean isValid() {

        def matcher = s =~ /(N|S)(\d+(\.\d+)?)/

        assert matcher != null

        if (!matcher.matches()) {
            return false
        }

        def latitude = new BigDecimal(matcher.group(2))

        latitude >= 0 && latitude <= 90
    }


}
