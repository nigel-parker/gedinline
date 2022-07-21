package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class AgeAtEventSpec extends Specification {

    void 'test for GEDCOM 7.0'() {

        expect:

            def i1 = input
            def i2 = '< ' + input
            def i3 = '> ' + input
            def i4 = ''

            new AgeAtEvent().isValid(i1, GedcomVersion.V_70) == expectedResult
            new AgeAtEvent().isValid(i2, GedcomVersion.V_70) == expectedResult
            new AgeAtEvent().isValid(i3, GedcomVersion.V_70) == expectedResult
            new AgeAtEvent().isValid(i4, GedcomVersion.V_70) == true

        where:

            input           || expectedResult

            '37y 11m 0w 2d' || true
            '37y'           || true
            '11m 2w'        || true
            '2w 3d'         || true
            '2d'            || true
            '37y 2m'        || true
            '37y 2d'        || true
            '11y 2d'        || true
            '444m 1000d'    || true

            '37Y'           || false
            '37m 4y'        || false
            'CHILD'         || false
            'child'         || false
            'INFANT'        || false
            'STILLBORN'     || false
            '>111y'         || false
    }

    void 'test forms common to all GEDCOM versions'() {

        expect:

            new AgeAtEvent().isValid(input, GedcomVersion.V_555) == expectedResult
            new AgeAtEvent().isValid(input, GedcomVersion.V_551) == expectedResult

        where:

            input        || expectedResult

            'CHILD'      || true
            'child'      || true
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
            '>111y'      || false
    }

    void 'test new forms for GEDCOM 555'() {

        expect:

            new AgeAtEvent().isValid(input, GedcomVersion.V_555) == expectedResult
            new AgeAtEvent().isValid(input, GedcomVersion.V_551) == !expectedResult

        where:

            input         || expectedResult

            '999y 11m 2d' || true
            '999y'        || true
            '999y 2m'     || true
            '999y 2d'     || true
            '> 11y'       || true
            '> 111y'      || true
            '< child'     || true
            '> child'     || true

            '<child'      || false
            '>child'      || false
            '>11y'        || false

    }
}
