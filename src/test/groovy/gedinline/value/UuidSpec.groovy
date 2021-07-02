package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class UuidSpec extends Specification {

    void 'test UID for V_70'() {

        expect:

            new Uuid(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input                                  | expectedResult

            '123e4567-e89b-12d3-a456-426655440000' | true
            'c73bcdcc-2669-4bf6-81d3-e4ae73fb11fd' | true
            'C73BCDCC-2669-4Bf6-81d3-E4AE73FB11FD' | true

            'c73bcdcc-2669-4bf6-81d3-e4aX73fb11fd' | false
            'c73bcdcc26694bf681d3e4ae73fb11fd'     | false
            'anything-else'                        | false
    }
}
