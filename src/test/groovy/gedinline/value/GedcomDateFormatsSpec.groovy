package gedinline.value

import spock.lang.*

import static gedinline.value.GedcomDateFormats.*

class GedcomDateFormatsSpec extends Specification {

    void 'day month year'() {

        expect:

            parses(s, DAY_MONTH_YEAR) == result

        where:

            s                || result

            '3 JAN 1972'     || true
            '03 JAN 1972'    || true
            '4 MAR 692'      || true
            '1 JAN 95'       || true
            '3 jan 1972'     || true
            '29 FEB 2000'    || true

            '31 FEB 1972'    || false
            '29 FEB 1900'    || false
            '29 FEB 2019'    || false
            '31 JUN 1692'    || false
            '3 January 1972' || false
            'JAN 1972'       || false
    }

    void 'month year'() {

        expect:

            parses(s, MONTH_YEAR) == result

        where:

            s          || result

            'JAN 1972' || true
            'jan 1972' || true
            'MAR 692'  || true

            'XYZ 1972' || false
            '1972'     || false
    }

    void 'year'() {

        expect:

            parses(s, YEAR) == result

        where:

            s      || result

            '1972' || true
            '692'  || true
            '95'   || true

            'ABCD' || false
    }
}
