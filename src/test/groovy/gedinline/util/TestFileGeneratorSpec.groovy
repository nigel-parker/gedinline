package gedinline.util

import spock.lang.*

class TestFileGeneratorSpec extends Specification {

    void test() {

        given:

            def testFileGenerator = new TestFileGenerator()
            def result1 = testFileGenerator.generate([patchNumber: ''])

        expect:

            result1 == normalise('''

0 HEAD
1 GEDC
2 VERS 7.0
0 TRLR

''')

        when:

            def result2 = testFileGenerator.generate([body: ['0 @S1@ SOUR']])

        then:

            result2 == normalise('''

0 HEAD
1 GEDC
2 VERS 7.0
0 @S1@ SOUR
0 TRLR

''')


    }

    private normalise(String s) {
        s.trim() + '\n'
    }
}
