package gedinline.value

import gedinline.lexical.*
import spock.lang.*

class AgeAtEventSpec extends Specification {

    void 'test forms common to all GEDCOM versions'() {

        expect:

            new AgeAtEvent(input, GedcomVersion.V_555).isValid() == expectedResult
            new AgeAtEvent(input, GedcomVersion.V_551).isValid() == expectedResult

        where:

            input        || expectedResult

            'CHILD'      || true
            'child'      || true
            '<child'     || true
            '>child'     || true
            'INFANT'     || true
            'STILLBORN'  || true
            '37y 11m 2d' || true
            '37y'        || true
            '11m'        || true
            '2d'         || true
            '37y 2m'     || true
            '37y 2d'     || true
            '11y 2d'     || true

            '444m 1000d' || false
    }

    void 'test new forms for GEDCOM 555'() {

        expect:

            new AgeAtEvent(input, GedcomVersion.V_555).isValid() == expectedResult
            new AgeAtEvent(input, GedcomVersion.V_551).isValid() == !expectedResult

        where:

            input        || expectedResult

            '999y 11m 2d' || true
            '999y'        || true
            '999y 2m'     || true
            '999y 2d'     || true
    }
}
