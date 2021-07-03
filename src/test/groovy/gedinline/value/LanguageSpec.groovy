package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class LanguageSpec extends Specification {

    void 'test Language for V_70'() {

        expect:

            new Language(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input           | expectedResult

            'no'            | true
            'NO'            | true

            'nc'            | false
            'anything-else' | false
    }
}
