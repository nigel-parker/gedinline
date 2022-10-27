package gedinline.tagtree

class ConcAndContSpec extends InlineSpecification {

    def VALID_SOURCE = '0 @S1@ SOUR\n1 TITL My source'

    void 'test SUBMITTER_RECORD rules'() {

        def result

        when: 'the input file contains a valid source record'

            result = getResult(VALID_SOURCE, 0, false)

        then: 'everything is OK'

            result.contains('0  Total number of warning messages')

        when: 'the input file contains a number of CONx after the source record'

            result = getResult(VALID_SOURCE + '\n2 CONC 12\n2 CONT 34', 0, false)

        then: 'everything is still OK'

            result.contains('0  Total number of warning messages')

        when: 'however the input file contains a CONx as a subrecord to another CONx'

            result = getResult(VALID_SOURCE + '\n2 CONC 12\n3 CONT 34', 0, false)

        then: 'everything is still OK'

            result.contains('0  Total number of warning messages')

    }
}