package gedinline.main

import spock.lang.*

import static gedinline.tagtree.Occurrence.*

class OccurrenceRulesSpec extends Specification {

    void test() {

        expect:

            GedInlineValidator.checkOccurrenceRules(occurrence, tagCount) == ok

        where:

            occurrence    | tagCount || ok

            MANDATORY     | 0        || false
            MANDATORY     | 1        || true
            MANDATORY     | 2        || false
            MANDATORY     | 3        || false
            MANDATORY     | 4        || false

            OPTIONAL      | 0        || true
            OPTIONAL      | 1        || true
            OPTIONAL      | 2        || false
            OPTIONAL      | 3        || false
            OPTIONAL      | 4        || false

            MULTIPLE      | 0        || true
            MULTIPLE      | 1        || true
            MULTIPLE      | 2        || true
            MULTIPLE      | 3        || true
            MULTIPLE      | 4        || true

            UP_TO_3_TIMES | 0        || true
            UP_TO_3_TIMES | 1        || true
            UP_TO_3_TIMES | 2        || true
            UP_TO_3_TIMES | 3        || true
            UP_TO_3_TIMES | 4        || false

            AT_LEAST_1    | 0        || false
            AT_LEAST_1    | 1        || true
            AT_LEAST_1    | 2        || true
            AT_LEAST_1    | 3        || true
            AT_LEAST_1    | 4        || true
    }
}
