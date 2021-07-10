package gedinline.value


import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class DateExactSpec extends Specification {

    void 'verify that DateExact handles \'#input\' correctly'() {

        expect:

            new DateExact().isValid(input, V_70) == expectedResult

        where:

            input             || expectedResult

            '6 DEC 2002'      || true
            '6 _XYZ 2002'     || true       // Extension months allowed

            '6  DEC 2002'     || false      // Only one space allowed
            '06 DEC 2002'     || false      // No leading zeroes
            '0 DEC 2002'      || false      // Zero not allowed
            '6 DECEMBER 2002' || false      // Only 3-letter months
            '6 Dec 2002'      || false      // Only upper case months

            '33 FEB 90066'    || true       // TBD: check for nonsensical dates
            '29 FEB 2002'     || true       // TBD: check for valid dates


    }
}
