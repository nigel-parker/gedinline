package gedinline.value


import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class MediaTypeSpec extends Specification {

    void 'test MediaType for V_70'() {

        expect:

            new MediaType().isValid(input, V_70) == expectedResult

        where:

            input                      || expectedResult

            'text/plain'               || true
            'text/plain;charset=UTF-8' || true
            'application/octet-stream' || true
            'image/vnd.microsoft.icon' || true
            'image/svg+xml'            || true

            'image'                    || false
    }
}
