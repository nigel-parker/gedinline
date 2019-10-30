package gedinline.lexical

import spock.lang.*

class HeaderParserSpec extends Specification {

    HeaderParser parser

    void test() {

        when:

            parser = new HeaderParser()

        then:

            parser.lineCount == 6
            !parser.validGedcom555Header

        when:

            addHeaderPrefix()

        then:

            !parser.validGedcom555Header

        when:

            parser.addLine('1 CHAR UTF-8')

        then:

            parser.validGedcom555Header

        when:

            parser = new HeaderParser()
            addHeaderPrefix()
            parser.addLine('1 CHAR UNICODE')

        then:

            parser.validGedcom555Header
    }

    private addHeaderPrefix() {
        parser.addLine('0 HEAD')
        parser.addLine('1 GEDC')
        parser.addLine('2 VERS 5.5.5')
        parser.addLine('2 FORM LINEAGE-LINKED')
        parser.addLine('3 VERS 5.5.5')
    }
}
