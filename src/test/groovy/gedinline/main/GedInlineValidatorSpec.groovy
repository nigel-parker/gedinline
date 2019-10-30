package gedinline.main

import spock.lang.*

@Unroll
class GedInlineValidatorSpec extends FileReaderSpecification {

    void 'verify test file #filename'() {

        expect:

            verify(filename, expectedWarningCount, expectedContent)

        where:

            filename                 || expectedWarningCount | expectedContent

            'corner1.ged'            || 3                    | 'Invalid content for SURN tag'
            'harvey.ged'            || 2                    | ''
            'jiapu.ged'              || 10                   | ''
            'multimedia-test.ged'    || 4                    | ''
            'olson-555.ged'          || 8                    | ''
            'simple.ged'             || 3                    | ''
            'simple-555.ged'         || 1                    | ''
            'smith.ged'              || 27                   | ''
            'torture-test-5-5-1.ged' || 0                    | ''
            'w1.ged'                 || 0                    | ''
            'w10.ged'                || 1                    | 'Mandatory tag DATE not found under STAT'
            'w10.ged'                || 1                    | 'Other                       1'
            'w11.ged'                || 56                   | ''
            'w2.ged'                 || 1                    | 'Level numbers should not have leading zeroes'
            'w3.ged'                 || 3                    | 'Invalid GEDCOM record'
            'w4.ged'                 || 2                    | 'Level numbers must increase by 1'
            'w5.ged'                 || 4                    | 'Invalid GEDCOM line \'1 \''
            'w550.ged'               || 1                    | ''
            'w551.ged'               || 0                    | ''
            'w6.ged'                 || 2                    | 'Tags must consist of alphanumerics'
            'w7.ged'                 || 2                    | 'Tag sour is not allowed under HEAD'
            'w8.ged'                 || 2                    | 'Invalid pointer'
            'w9.ged'                 || 0                    | ''

    }
}
