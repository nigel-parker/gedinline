package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class SemanticVersionNumberSpec extends Specification {

    void 'test SemanticVersionNumber for V_70'() {

        expect:

            new SemanticVersionNumber(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input    || expectedResult

            '7.0'    || true
            '7.0.1'  || true

            '07.0.1' || false
            '-7.0.1' || false
            '.0.1'   || false
            '7x0.1'  || false
    }
}
