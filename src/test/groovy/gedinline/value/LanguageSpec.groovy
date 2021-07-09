package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class LanguageSpec extends Specification {

    void 'test Language for V_70'() {

        expect:

            new Language().isValid(input, V_70) == expectedResult

        where:

            input           | expectedResult

            'no'            | true
            'NO'            | true

            'nc'            | false
            'anything-else' | false
    }
}
