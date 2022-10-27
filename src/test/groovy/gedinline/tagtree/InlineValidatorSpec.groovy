package gedinline.tagtree

class InlineValidatorSpec extends InlineSpecification {

    def VALID_SUBMITTER1 = '0 @U1@ SUBM\n1 NAME Nigel'
    def VALID_SUBMITTER2 = '0 @U2@ SUBM\n1 NAME Nigel'
    def VALID_SOURCE = '0 @S1@ SOUR'

    void 'test SUBMITTER_RECORD rules'() {

        def result

        when: 'the input file contains a single valid submitter record immediately after the header'

            result = getResultShort(VALID_SUBMITTER1, 0, false)

        then: 'everything is OK'

            result.contains('0  Total number of warning messages')

        when: 'there is no submitter record'

            result = getResultShort(VALID_SOURCE, 1, false)

        then:

            result.contains('Mandatory <SUBMITTER_RECORD> is missing, should be placed directly after the header')

        when: 'there is submitter record but not in the right place'

            result = getResultShort("$VALID_SOURCE\n$VALID_SUBMITTER1", 1, false)

        then:

            result.contains('Mandatory <SUBMITTER_RECORD> is missing, should be placed directly after the header')

        when: 'there are multiple submitter records'

            result = getResultShort(VALID_SUBMITTER1 + '\n' + VALID_SUBMITTER2, 1, false)

        then:

            result.contains('Multiple <SUBMITTER_RECORD>s are not allowed')

        when: 'we have both defects'

            result = getResultShort(VALID_SOURCE + '\n' + VALID_SUBMITTER1 + '\n' + VALID_SUBMITTER2, 1, false)

        then:

            result.contains('Mandatory <SUBMITTER_RECORD> is missing, should be placed directly after the header')

    }
}
