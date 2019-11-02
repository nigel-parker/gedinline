package gedinline.main

import spock.lang.*

@Unroll
class GedInlineValidatorSpec extends FileReaderSpecification {

    void 'verify test file #filename'() {

        expect:

            verify(filename, expectedWarningCount, expectedContent)

        where:

            filename                     || expectedWarningCount | expectedContent

            'corner1.ged'                || 3                    | 'Invalid content for SURN tag'
            'encoding-utf-16-le-555.ged' || 0                    | ''
            'harvey.ged'                 || 2                    | ''
            'jiapu.ged'                  || 10                   | ''
            'multimedia-test.ged'        || 4                    | ''
            'olson-555.ged'              || 8                    | ''
            'simple-555.ged'             || 1                    | ''
            'simple.ged'                 || 3                    | ''
            'smith.ged'                  || 27                   | ''
            'torture-test-5-5-1.ged'     || 0                    | ''
            'w01.ged'                    || 0                    | ''
            'w02.ged'                    || 1                    | 'Level numbers should not have leading zeroes'
            'w03.ged'                    || 3                    | 'Invalid GEDCOM record'
            'w04.ged'                    || 2                    | 'Level numbers must increase by 1'
            'w05.ged'                    || 4                    | 'Invalid GEDCOM line \'1 \''
            'w06.ged'                    || 2                    | 'Tags must consist of alphanumerics'
            'w07.ged'                    || 2                    | 'Tag sour is not allowed under HEAD'
            'w08.ged'                    || 2                    | 'Invalid pointer'
            'w09.ged'                    || 0                    | ''
            'w10.ged'                    || 1                    | 'Mandatory tag DATE not found under STAT'
            'w10.ged'                    || 1                    | 'Other                       1'
            'w11.ged'                    || 56                   | ''
            'w14.ged'                    || 2                    | "*** Line 13:      Invalid GEDCOM line '10'"
            'w15.ged'                    || 7                    | "*** Line 12:      Invalid GEDCOM line '10'"
            'w550.ged'                   || 1                    | ''
            'w551.ged'                   || 0                    | ''
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
