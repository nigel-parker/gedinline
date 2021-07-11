package gedinline.value


import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class TimeSpec extends Specification {

    void 'verify that Time handles \'#input\' correctly'() {

        expect:

            new Time().isValid(input, gedcomVersion) == expectedResult

        where:

            gedcomVersion | input            || expectedResult

            V_551         | '0:0'            || true
            V_551         | '00:00'          || true
            V_551         | '00:59'          || true
            V_551         | '13:13'          || true
            V_551         | '23:59'          || true

            V_551         | '00:00:00'       || true
            V_551         | '23:59:59'       || true

            V_551         | '00:00:00.00'    || true
            V_551         | '00:00:00.6'     || true
            V_551         | '00:00:00.999'   || true

            V_551         | '00:60'          || false
            V_551         | '24:00'          || false
            V_551         | '24:13'          || false
            V_551         | '23:59.59'       || false
            V_551         | '23:59:59.1234'  || false

            V_70          | '0:0'            || true
            V_70          | '00:00'          || true
            V_70          | '00:59'          || true
            V_70          | '13:13'          || true
            V_70          | '23:59'          || true
            V_70          | '23:59Z'         || true

            V_70          | '00:00:00'       || true
            V_70          | '23:59:59'       || true
            V_70          | '23:59:59Z'      || true

            V_70          | '00:00:00.00'    || true
            V_70          | '00:00:00.6'     || true
            V_70          | '00:00:00.999'   || true
            V_70          | '00:00:00.999Z'  || true

            V_70          | '00:60'          || false
            V_70          | '00:60 Z'        || false
            V_70          | '24:00'          || false
            V_70          | '24:13'          || false
            V_70          | '23:59.59'       || false
            V_70          | '00:00:00.999ZZ' || false
            V_70          | '23:59:59.1234'  || false
    }
}
