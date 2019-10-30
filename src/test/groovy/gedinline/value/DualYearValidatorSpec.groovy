package gedinline.value

import gedinline.lexical.*
import spock.lang.*

class DualYearValidatorSpec extends Specification {

    void test() {

        expect:

            new DualYearValidator(s1, dualYear).isValid() == result

        where:

            s1            | dualYear || result

            '24 MAR 1692' | '/93'    || true
            '4 MAR 692'   | '/93'    || true
            'MAR 1692'    | '/93'    || true

            '1692'        | '/93'    || false
            'MAR 1922'    | '/24'    || false // not consecutive years
            '25 MAR 1922' | '/23'    || false // after old new year
            '31 DEC 1922' | '/23'    || false // after old new year
            '24 MAR 1924' | '/25'    || false // after 1923

    }
}
