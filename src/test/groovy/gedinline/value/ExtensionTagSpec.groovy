package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class ExtensionTagSpec extends Specification {

    void 'test URL validation for \'#input\''() {

        expect:

            new ExtensionTag().isValid(input, V_70) == expectedResult

        where:

            input                                        | expectedResult

            '_SKYPEID http://xmlns.com/foaf/0.1/skypeID' | true

            'SKYPEID http://xmlns.com/foaf/0.1/skypeID'  | false
            '_SKYPEID www.sottovoce.no'                  | false
    }
}
