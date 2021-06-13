package gedinline.lexical

import gedinline.main.*

import static gedinline.lexical.GedcomVersion.*

//@Unroll
class BasicParserSpec extends FileReaderSpecification {

    void 'test BasicParser on a simple gedcom file fragment'() {

        when:

            def lines = getInputLinePrecursors('head.ged')

        then:

            lines.size() == 11

            def line1 = lines[0]
            line1.lineNumber == 1
            line1.level.level == 0
            line1.line == 'HEAD'

            def line2 = lines[1]
            line2.lineNumber == 2
            line2.level.level == 1
            line2.line == 'SOUR BROSKEEP'
    }

    void 'test that the parser reads the correct number of lines'() {

        expect:

            getInputLinePrecursors(filename).size() == numberOfLines

        where:

            filename               || numberOfLines

            'torture-test-5-5.ged' || 2198
            'harvey.ged'           || 475
            'simple.ged'           || 34
            'error-1.ged'          || 11
    }

    void 'test file encoding for #filename'() {

        expect:

            def result = getInputLinePrecursors(filename)
            def line16 = result[16].line
            def line17 = result[17].line

            if (filename.contains('ascii')) {
                assert line16.contains('Mahlum')
                assert line17.contains('Indstoy')
            } else {
                assert line16 == 'NAME Peder Ellingssen /Mæhlum/'
                assert line17 == 'NAME Helga Agnethe Rogne /Indstøy/'
            }

        where:

            filename                     || ok

            'encoding-ansel.ged'         || true
            'encoding-ascii.ged'         || true
            'encoding-utf-16-le-bom.ged' || true
            'encoding-utf-16.ged'        || true
            'encoding-utf-8-bom.ged'     || true
            'encoding-utf-8.ged'         || true
    }

    void 'test that invalid code points are translated to replacement character'() {

        when:

            def line3 = getInputLinePrecursors('error-3.ged')[2].line

        then:

            Character.codePointAt(line3, 16) == 0xFFFD
    }

    int actualWarningCount

    @SuppressWarnings("GroovyAccessibility")
    void 'test warning count for \'#inputLine\' version #gedcomVersion '() {

        given:

            actualWarningCount = 0
            def basicParser = new BasicParser(null, new WarningCounter())
            basicParser.gedcomVersion = gedcomVersion
            basicParser.previousLevel = new Level(2)

            if (gedcomVersion.is555()) {
                basicParser.handleLine555(inputLine)
            } else {
                basicParser.handleLine(inputLine)
            }

        expect:

            expectedWarningCount == actualWarningCount

        where:

            inputLine                            | gedcomVersion || expectedWarningCount

            '3 @A1@ DATE @#DJULIAN@ 31 DEC 1599' | V_551         || 0
            '3 @A1@ DATE @#DJULIAN@ 31 DEC 1599' | V_555         || 0

            ' 3 BIRT'                            | V_551         || 0
            ' 3 BIRT'                            | V_555         || 1 // can't have leading whitespace

            '4 BIRT'                             | V_551         || 1
            '4 BIRT'                             | V_555         || 1

            '3  BIRT'                            | V_551         || 0
//            '3  BIRT'                            | V_555         || 1 // only 1 space allowed

            '03 BIRT'                            | V_551         || 1
            '03 BIRT'                            | V_555         || 1

            '103 BIRT'                           | V_551         || 1
            '103 BIRT'                           | V_555         || 1

            '3 BIRT @'                           | V_551         || 0
//            '3 BIRT @'                           | V_555         || 1 // @ must be escaped
            '3 BIRT @@'                          | V_555         || 0

            '3 BIRT @#@'                         | V_551         || 0
            '3 BIRT @#@'                         | V_551         || 0

            '3 BIRT a\fb'                        | V_551         || 1
            '3 BIRT a\fb'                        | V_555         || 1

            '3 BIRT a\tb'                        | V_551         || 1
//            '3 BIRT a\tb'                        | V_555         || 0
    }

    class WarningCounter implements WarningSink {

        void warning(String s) {
            incrementWarningCount(s)
        }

        void warning(InputLine inputLine, String s) {
            incrementWarningCount(s)
        }

        void warning(int lineNumber, String s) {
            incrementWarningCount(s)
        }

        void incrementWarningCount(String warning) {
            actualWarningCount++
        }
    }
}
