package gedinline.lexical

import gedinline.main.*
import spock.lang.*

class Tag555Spec extends Specification {

    @SuppressWarnings(["GroovyResultOfObjectAllocationIgnored", "GroovyUnusedCatchParameter"])
    void test() {

        expect:

            try {
                new Tag(input, GedcomVersion.V_555)

                assert result

            } catch (GedcomException ge) {

                assert !result

            }

        where:

            input            | result

            '2'              | true
            'CHAR'           | true
            '2char'          | true
            'x' * 31         | true
            '_' + ('x' * 30) | true

            null             | false
            ''               | false
            '????'           | false
            '_' + ('x' * 31) | false
            '_' * 4          | false
    }
}
