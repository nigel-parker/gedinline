package gedinline.value

import spock.lang.*

class CardinalitySpec extends Specification {

    void test() {

        expect:

            def cardinality = new Cardinality(input)

            cardinality.minimum == minimum
            cardinality.maximum == maximum

        where:

            input          || minimum | maximum

            '{Size=1:120}' || 1       | 120
            '{Size=3}'     || 3       | 3
            ''             || 1       | 9999
    }
}
