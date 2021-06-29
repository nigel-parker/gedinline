package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class PersonalNameSpec extends Specification {

    void 'test PersonalName for V_70'() {

        expect:

            new PersonalName(input, GedcomVersion.V_70).isValid() == expectedResult

        where:

            input                     || expectedResult

            'Napoléon'                || true
            'Napoléon Bonaparte'      || true
            'Napoléon /Bonaparte/'    || true
            'Napoléon /Bonaparte/ Jr' || true
            '//'                      || true
            '??!\\&%==  #"89GH??'     || true

            ''                        || false
            '/'                       || false
            '///'                     || false
            '\t'                      || false
    }
}
