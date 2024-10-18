package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class FileReferenceSpec extends Specification {

    void 'test URL validation for \'#input\''() {

        expect:

            new FileReference().isValid(input, V_70) == expectedResult

        where:

            input                           | expectedResult

            'http://sottovoce.no'           | true
            'https://sottovoce.no'          | true
            'https://sottovoce.no?all=true' | true
            'https://sottovoce.no#top'      | true
            'ftp://sottovoce.no'            | true
            'file://sottovoce.no'           | true
            'file://sottovoce.no/a'         | true
            'file://sottovoce.no/%2fa'      | true
            'file://sottovoce.no/%5c'       | true

            'file:data/sottovoce.pdf'       | true
            'sottovoce.pdf'                 | true
            'sotto%20voce.pdf'              | true
            'sottovoce.pdf/%2fa'            | true

            'sotto voce.pdf'                | false
            '/sottovoce.pdf'                | false
            'sottovoce..pdf'                | false
            '\\sottovoce.pdf'               | false
            'sottovoce.pdf/%5c'             | false
            'sottovoce.pdf#top'             | false
            'sottovoce.pdf?binary=true'     | false

    }
}
