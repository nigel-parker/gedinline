package gedinline.lexical

import gedinline.main.*
import spock.lang.*

class TagSpec extends Specification {

    @SuppressWarnings("GroovyResultOfObjectAllocationIgnored")
    void test() {

        given:

            def actualResult

            try {
                new Tag(input)
                actualResult = true
            } catch (GedcomException ignored) {
                actualResult = false
            }

        expect:

            actualResult == expectedResult

        where:

            input     || expectedResult

            'name'    || true
            '____'    || true
            '999'     || true

            ''        || false
            '@'       || false
            'my-name' || false
    }
}
