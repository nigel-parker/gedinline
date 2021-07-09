package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class EmailSpec extends Specification {

    void 'test Email for V_70'() {

        expect:

            new Email().isValid(input, GedcomVersion.V_70) == expectedResult

        where:

            input                        | expectedResult

            'nigel.parker@sottovoce.no'  | true
            'goofy_sloofy@hotmail.com'   | true
            'post@althea.xyz'            | true
            'terje@torkelsen.tech'       | true
            'jonas.d.v.@hotmail.com'     | true // technically invalid, but not thrown out by the regex

            'sottovoce'                  | false
            'sottovoce.no'               | false
            '@sottovoce.no'              | false
            'nigel.parker@sottovoce.no@' | false
            'nnn@nnn.'                   | false
            'nnn@nnn'                    | false
    }
}
