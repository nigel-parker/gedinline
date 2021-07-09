package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Longitude extends Validator {

    Longitude() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def matcher = s =~ /(E|W)(\d+(\.\d+)?)/

        assert matcher != null

        if (!matcher.matches()) {
            return false
        }

        def longitude = new BigDecimal(matcher.group(2))

        longitude >= 0 && longitude <= 180
    }
}
