package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class LongitudeSpec extends Specification {

    void 'test Longitude for V_70'() {

        expect:

            new Longitude(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input        | expectedResult

            'E18.150944' | true
            'E18'        | true
            'E0'         | true
            'E018'       | true
            'W91'        | true

            'E.98'       | false
            'E89.'       | false
            'E180.1'     | false
            'c73'        | false
            'w91'        | false
    }
}
