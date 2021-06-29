package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class MediaTypeSpec extends Specification {

    void 'test MediaType for V_70'() {

        expect:

            new MediaType(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input                      || expectedResult

            'text/plain'               || true
            'text/plain;charset=UTF-8' || true
            'application/octet-stream' || true
            'image/vnd.microsoft.icon' || true
            'image/svg+xml'            || true
    }
}
