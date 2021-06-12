package gedinline.util

import spock.lang.*

class VsTemplateSpec extends Specification {

    void test() {

        when:

            def template1 = new VsTemplate('{b}a{b}c{b}')

        then:

            template1.toString() == '{b}a{b}c{b}'
            template1.numberOfFreeVariables == 1
            template1.freeVariables == ['b']

            template1.bindAll('').toString() == 'ac'

        when:

            def template2 = template1.bind([a: 'A', b: 'B'])

        then:

            template2.toString() == 'BaBcB'
            template2.numberOfFreeVariables == 0
            template2.freeVariables == []

    }
}
