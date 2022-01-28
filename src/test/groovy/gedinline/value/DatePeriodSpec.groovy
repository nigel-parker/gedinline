package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class DatePeriodSpec extends Specification {

    void 'verify that DatePeriod handles \'#input\' correctly'() {

        expect:

            new DatePeriod().validate(input, V_70).isValid() == expectedResult

        where:

            input                                   || expectedResult

            'FROM 6 DEC 2002'                       || true
            'TO 6 DEC 2002'                         || true
            'FROM 6 DEC 2002 TO 6 DEC 2002'         || true
            'FROM JULIAN 6 DEC 2 BCE TO 6 DEC 2002' || true

            'FROM Sep 1953'                         || false
            '6 DEC 2002'                            || false
            'FROM 06 DEC 2002'                      || false
            'TO 06 DEC 2002'                        || false
            'FROM 06 DEC 2002 TO 06 DEC 2002'       || false
    }
}
