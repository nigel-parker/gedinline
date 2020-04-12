package gedinline.tagtree

class MiscellaneousSpec extends InlineSpecification {

    final VALID_DATE = '0 @I00001@ INDI\n1 BIRT\n2 DATE 6 APR'

    void 'test SUBMITTER_RECORD rules'() {

        when: 'the input file contains a valid source record'

            getResult(VALID_DATE, 0, false)

        then: 'everything is OK'

            true

    }

    void 'test duplicate gedcom versions in file'() {

        when: 'the file header specifies 2 different gedcom versions'

            def innlegg = '''1 GEDC
2 VERS 5.5
2 FORM LINEAGE-LINKED
1 CHAR UTF-8'''
            def result = getResult(innlegg, 2, false)

        then: 'it ignores the second one'

            result.contains('GEDCOM version assumed         5.5.5')
    }

    void 'test for 3 PHON records bug'() {

        when: 'the file contains 3 PHON records'

            def innlegg = '''2 CORP gedcom.org
3 ADDR
4 ADR1 gedcom.org
3 PHON +1-555-555-5555
3 PHON +1-555-555-5555
3 PHON +1-555-555-5555
0 @U@ SUBM
1 NAME gedcom.org'''
            getResultShort(innlegg, 0, false)

        then: 'everything is OK'

            true
    }
}
