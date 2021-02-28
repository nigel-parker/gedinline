package gedinline.lexical

import gedinline.main.*
import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

//@Unroll
class TagSpec extends Specification {

    @SuppressWarnings(["GroovyResultOfObjectAllocationIgnored", "GroovyUnusedCatchParameter"])
    void 'test tag #input for #gedcomVersion'() {

        expect:

            try {
                Tag.getInstance(input, gedcomVersion)

                assert result

            } catch (GedcomException ge) {

                assert !result

            }

        where:

            gedcomVersion | input            | result

            V_551         | 'name'           | true
            V_551         | '____'           | true
            V_551         | '999'            | true
            V_551         | '_' + ('x' * 31) | true

            V_551         | ''               | false
            V_551         | '@'              | false
            V_551         | 'my-name'        | false

            V_555         | '2'              | true
            V_555         | 'CHAR'           | true
            V_555         | '2char'          | true
            V_555         | 'x' * 31         | true
            V_555         | '_' + ('x' * 30) | true

            V_555         | null             | false
            V_555         | ''               | false
            V_555         | '????'           | false
            V_555         | '_' + ('x' * 31) | false
            V_555         | '_' * 4          | false

            V_70          | 'C'              | true
            V_70          | 'CHAR_2'         | true
            V_70          | '_CHAR_2'        | true
            V_70          | '_C'             | true
            V_70          | '_2'             | true

            V_70          | 'name'           | false
            V_70          | '999'            | false
            V_70          | ''               | false
            V_70          | '_'              | false
            V_70          | '@'              | false
            V_70          | 'my-name'        | false

    }

    void 'test equals'() {

        expect:

            def tag555 = Tag.getInstance('SUBM', V_555)

            tag555 == Tag.SUBM

            def map = [:]

        when:

            map.put(tag555, '99')

        then:

            map.size() == 1
            map.get(Tag.SUBM)
        when:

            map.put(Tag.SUBM, '99')

        then:

            map.size() == 1
            map.get(Tag.SUBM)
    }
}
