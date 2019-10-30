package gedinline.value

import spock.lang.*

class TimeValueSpec extends Specification {

    void test() {

        expect:

            new TimeValue(input).valid == result

        where:

            input           || result

            '0:0'           || true
            '00:00'         || true
            '00:59'         || true
            '13:13'         || true
            '23:59'         || true

            '00:00:00'      || true
            '23:59:59'      || true

            '00:00:00.00'   || true
            '00:00:00.6'    || true
            '00:00:00.999'  || true

            '00:60'         || false
            '24:00'         || false
            '24:13'         || false
            '23:59.59'      || false
            '23:59:59.1234' || false
    }
}
