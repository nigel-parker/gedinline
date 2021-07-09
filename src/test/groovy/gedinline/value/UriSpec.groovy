package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class UriSpec extends Specification {

    void 'test URL validation for \'#input\''() {

        expect:

            new Uri().isValid(input, V_70) == expectedResult

        where:

            input                               | expectedResult

            'file:disk'                         | true
            'http://xmlns.com'                  | true
            'http://xmlns.com/foaf/0.1/skypeID' | true

            'http://sottovoce.no'               | true
            'HTTPS://SOTTOVOCE.NO'              | true

            'www.sottovoce.no'                  | false
    }
}
