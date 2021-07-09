package gedinline.value


import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class LatitudeSpec extends Specification {

    void 'test Latitude for V_70'() {

        expect:

            new Latitude().isValid(input, V_70) == expectedResult

        where:

            input        | expectedResult

            'N18.150944' | true
            'N18'        | true
            'N0'         | true
            'N018'       | true
            'S90'        | true

            'N.98'       | false
            'N89.'       | false
            'N91'        | false
            'c73'        | false
    }
}
