package gedinline.main

import gedinline.util.*
import spock.lang.*

@Unroll
class GedInlineValidatorSpec extends FileReaderSpecification {

    @Unroll
    void 'test variant #variant'() {

        given:

            def testFileGenerator = new TestFileGenerator()
            def testFileContent = testFileGenerator.getVariant(variant)
            def stringWriter = new StringWriter()
            def inputStream = new ByteArrayInputStream(testFileContent.getBytes())
            def gedcomValidator = new GedInlineValidator(inputStream, 'gedcom-v7.ged', new PrintWriter(stringWriter))
            gedcomValidator.validate()

            def actualNumberOfWarnings = gedcomValidator.numberOfWarnings

            if (actualNumberOfWarnings) {
                println testFileContent
                println stringWriter.toString()
            }

        expect:

            actualNumberOfWarnings == expectedNumberOfWarnings

        where:

            variant || expectedNumberOfWarnings | comment

            1       || 0                        | ''
            2       || 0                        | ''
            3       || 27                       | 'Valid 5.5 file treated as 7.0'
            4       || 1                        | ''
            5       || 0                        | ''
            6       || 0                        | ''
            7       || 2                        | ''
            8       || 0                        | ''
            9       || 0                        | ''
            10      || 0                        | ''
            11      || 0                        | ''
            12      || 0                        | ''
            13      || 0                        | ''
            14      || 0                        | ''
            15      || 0                        | ''
            16      || 2                        | ''
            17      || 1                        | ''
            18      || 0                        | ''
            19      || 0                        | ''
            20      || 0                        | ''
            21      || 0                        | ''
            22      || 0                        | ''
            23      || 1                        | 'ADDR payload is required, see spec p. 37'
            24      || 1                        | ''
            25      || 1                        | ''
            26      || 1                        | ''
            27      || 0                        | ''
            28      || 1                        | ''

            101     || 1                        | ''
            102     || 1                        | ''
            103     || 1                        | ''
            104     || 0                        | 'TBD: cant detect surrogates, they are converted to ? under input'
            105     || 0                        | 'TBD: cant detect surrogates, they are converted to ? under input'
            106     || 1                        | ''
            107     || 1                        | 'No blanks before level number'
            108     || 1                        | 'Blank lines not allowed'
            109     || 1                        | 'Tabs are allowed, except in personal names'

            201     || 1                        | 'TBD: Handling of stack overflow problem, see spec p. 35'

    }

    @Unroll
    void 'V7.0 continuation \'#input\''() {

        expect:

            def testLine = '0 @1@ SNOTE '

            def testFileGenerator = new TestFileGenerator()
            def gedcomUnderTest = testFileGenerator.withBody(testLine + input + '\n')
            def stringWriter = new StringWriter()
            def inputStream = new ByteArrayInputStream(gedcomUnderTest.getBytes())
            def gedcomValidator = new GedInlineValidator(inputStream, 'gedcom-v7.ged', new PrintWriter(stringWriter))
            gedcomValidator.validate()

            println stringWriter.toString()

            expectedResult == (gedcomValidator.numberOfWarnings == 0)

        where:

            input             || expectedResult | comment

            'abc'             || true           | ''
            '  abc   '        || true           | 'Leading and trailing spaces allowed'
            'abc\n1 CONT xyz' || true           | ''
            'abc\n1 CONT'     || true           | 'Empty continuation 1'
            'abc\n1 CONT '    || true           | 'Empty continuation 2'
            ''                || true           | 'Empty SNOTES are allowed'

            'abc\n1 CONC xyz' || false          | 'CONC invalid for 7.0'

            '@@me'            || false          | 'TBD: Should be true'
    }

    void 'verify handling of empty file'() {

        expect:

            verify('empty.ged', 0, 'File not recognised as a valid GEDCOM file', false)
    }

    void 'verify test file #filename'() {

        expect:

            verify(filename, expectedWarningCount, expectedContent)

        where:

            filename                 || expectedWarningCount | expectedContent

            'fs-long-url.ged'        || 0                    | ''
            'fs-minimal70.ged'       || 0                    | ''
            'fs-spaces.ged'          || 0                    | ''
            'fs-voidptr.ged'         || 0                    | ''
            'harvey.ged'             || 1                    | 'Report generated on'
            'multimedia-test.ged'    || 4                    | ''
            'olson-555.ged'          || 8                    | ''
            'phon-x-3.ged'           || 0                    | ''
            'simple-555.ged'         || 1                    | ''
            'simple.ged'             || 2                    | ''
            'smith.ged'              || 26                   | ''
            'torture-test-5-5-1.ged' || 0                    | ''
            'w01.ged'                || 0                    | ''
            'w02.ged'                || 1                    | 'Level numbers should not have leading zeroes'
            'w03.ged'                || 3                    | 'Invalid GEDCOM record'
            'w04.ged'                || 2                    | 'Level numbers must increase by 1'
            'w05.ged'                || 4                    | 'Invalid GEDCOM line \'1 \''
            'w06.ged'                || 2                    | 'Tags must consist of alphanumerics'
            'w07.ged'                || 2                    | 'Tag sour is not allowed under HEAD'
            'w08.ged'                || 2                    | 'Invalid pointer'
            'w09.ged'                || 0                    | ''
            'w10.ged'                || 1                    | 'Mandatory tag DATE not found under STAT'
            'w10.ged'                || 1                    | 'Other                       1'
            'w11.ged'                || 57                   | 'Unknown XREF type'
            'w14.ged'                || 2                    | "*** Line 13:      Invalid GEDCOM line '10'"
            'w15.ged'                || 6                    | "*** Line 12:      Invalid GEDCOM line '10'"
            'w16.ged'                || 0                    | ''
            'w17.ged'                || 1                    | 'Month values must be upper case'
            'w18.ged'                || 1                    | 'Cross-reference identifier @I2@ is not valid here'
            'w19.ged'                || 0                    | ''
            'w550.ged'               || 0                    | ''
            'w551.ged'               || 0                    | ''
    }

    void 'Issue #1 fixed'() {

        given:

            def gedcomValidator = new GedInlineValidator(getInputStream(filename), '', new PrintWriter(new StringWriter()))
            gedcomValidator.validate()

        expect:

            gedcomValidator.analysisStatistics.generatedBy == generatedBy

        where:

            filename                     || generatedBy

            'encoding-utf-16-le-555.ged' || 'GS'
            'w01.ged'                    || 'G'
            'w12.ged'                    || 'GED-inline'
            'w13.ged'                    || 'Reunion'


    }
}
