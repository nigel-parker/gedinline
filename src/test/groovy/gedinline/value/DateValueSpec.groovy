package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*
import static gedinline.value.ExpressionParser.*

@SuppressWarnings("GroovyPointlessBoolean")
class DateValueSpec extends Specification {

    static def UPPER_CASE_MONTHS = REPLACE_WITH + 'Month values must be upper case'

    void 'test for valid GEDCOM 7.0 dates'() {

        expect:

            new DateValue(input, V_70).validate().isValid() == result

        where:

            input                                             || result

            '3 JAN 1895'                                      || true
            'JAN 1895'                                        || true
            '1895'                                            || true
            '1 JAN 895'                                       || true
            '1 JAN 95'                                        || true
            '5'                                               || true
            '1'                                               || true
            '0'                                               || false
            '0 BCE'                                           || false
            '1 BCE'                                           || true
            '95 BCE'                                          || true

            'ABT JAN 1972'                                    || true
            'FROM JAN 1972'                                   || true
            'TO JAN 1972'                                     || true
            'BEF JAN 1972'                                    || true
            'AFT JAN 1972'                                    || true
            'CAL JAN 1972'                                    || true
            'EST JAN 1972'                                    || true
            'FROM JAN 1972 TO 1973'                           || true
            'BET JAN 1972 AND 1973'                           || true

//            '3 JAN 1'                                         || true
            '3 JAN 9999'                                      || true

            'GREGORIAN 3 JAN 1972'                            || true
            'FRENCH_R 3 VEND 1972'                            || true
            'FRENCH_R VEND 03'                                || true
            'FRENCH_R 1972'                                   || true
            'HEBREW 3 SVN 1972'                               || true
            'HEBREW 3 SVN 72'                                 || true
            'JULIAN 3 DEC 1918'                               || true
            'JULIAN 1100 BCE'                                 || true

            'BET 5 JAN 1712/13 AND 28 FEB 1714/15'            || false
            'BET HEBREW 3 SVN 1972 AND JULIAN 3 DEC 1972'     || true

            ' 3 JAN 1972 '                                    || true
            'BET 9 AND 12 JAN 1972'                           || true // Strange but 9 is now taken as year 9 AD

            '24 MAR 1692/93'                                  || false
            'MAR 1692/93'                                     || false
            'JULIAN MAR 1692/93'                              || false
            ' (03 jan 1972) '                                 || false
            '3 January 1972'                                  || false

            '24 MAR 1924/25'                                  || false
            'MAR 1922/24'                                     || false
            '25 DEC 1922/23'                                  || false

            ''                                                || false
            '03 JAN 1972'                                     || false
            '3 JAN  1972'                                     || false
            '03 JAN'                                          || false
            '31 FEB 1972'                                     || false
            '29 FEB 2019'                                     || false
            '31 JUN 1692/3'                                   || false
            '31 JUN 1692/xx'                                  || false
            '1692/93'                                         || false
            '03 JAN 1972 B.C.'                                || false
            'JAN 1972 B.C.'                                   || false
            '3 JAN'                                           || false
            '13 DEC'                                          || false

            'GREGORIAN3 JAN 1972'                             || false
            '@#DGREGORIAN@ 3 JAN 1972'                        || false
            '@#DFRENCH R@ 3 VEND 1972'                        || false
            '@#DHEBREW@ 3 SVN 1972'                           || false
            '@#DJULIAN@ 3 DEC 1918'                           || false
            '@#DJULIAN@ 1100 BCE'                             || false
            'FRENCH_R 3 VEND 1972 BCE'                        || false
            'HEBREW 3 SVN 1972 BCE'                           || false

            '@#DGREGORIAN@3 JAN 1972'                         || false
            '@#DJULIAN@ 5 AUG 1100 B.C.'                      || false
            '@#DJULIAN@ 03 DEC 1918'                          || false
            '@#DJULIAN@ 3 DEC'                                || false
            '@#DROMAN@ 03 JAN 1972'                           || false
            '@#DFRENCH R@ 03 VEND 1972'                       || false
            '@#DUNKNOWN@ 03 JAN 1972'                         || false
            '@#DHEBREW@ 03 SVN 1972'                          || false
            '@#DHEBREW@ 03 SVN 1972 B.C.'                     || false

            'ABT1973'                                         || false
            'ABT  1973'                                       || false
            'ABT 1973 TO 1975'                                || false
            '(ABT 1973 TO 1975'                               || false
            'INT JAN 1972()'                                  || false
            '044 B.C.'                                        || false
            '044 BC'                                          || false
            'JULIAN 1100 B.C.'                                || false
            'JULIAN 1100 BC'                                  || false
            'from JAN 1972'                                   || false
            'INT HEBREW 2 TVT 5758 (interpreted Hebrew date)' || false
            'INT JAN 1972 (says so in the census)'            || false
            'INT JAN 1972 (says so in )(the census)'          || false
            '(date phrase)'                                   || false
            '()'                                              || false
    }

    void 'test for error message overriding'() {

        expect:

            def validationResult = new DateValue(input, V_70).validate()

            validationResult.isValid() == isValid
            validationResult.getMessage() == message

        where:

            input                       || isValid | message

            '3 Jan 1972'                || false   | UPPER_CASE_MONTHS
            'FROM jan 1972 TO jan 1972' || false   | UPPER_CASE_MONTHS
            'FROM jan 1972 TO JAN 1972' || false   | UPPER_CASE_MONTHS
            'FROM JAN 1972 TO jan 1972' || false   | UPPER_CASE_MONTHS
            'GREGORIAN 3 Jan 1972'      || false   | UPPER_CASE_MONTHS

    }

    void 'test for valid GEDCOM 5.5.5 dates'() {

        expect:

            new DateValue(input, V_555).validate().isValid() == result

        where:

            input                                                 || result

            '3 JAN 1972'                                          || true
            '3 JAN'                                               || true
            '13 DEC'                                              || true
            '15 MAY 099'                                          || true
            'JAN 1972'                                            || true
            '1895'                                                || true
            '099'                                                 || true
            '0012'                                                || true
            '1895'                                                || true
            '972'                                                 || true
            '044 B.C.'                                            || true
            '044 BC'                                              || true
            '044 BCE'                                             || true

            '@#DGREGORIAN@ 3 JAN 1972'                            || true
            '@#DFRENCH R@ 3 VEND 1972'                            || true
            '@#DFRENCH R@ VEND 1803'                              || true
            '@#DFRENCH R@ 1972'                                   || true
            '@#DHEBREW@ 3 SVN 1972'                               || true
            '@#DJULIAN@ 3 DEC 1918'                               || true
            '@#DJULIAN@ 1100 B.C.'                                || true
            '@#DJULIAN@ 1100 BC'                                  || true
            '@#DJULIAN@ 1100 BCE'                                 || true

            'ABT JAN 1972'                                        || true
            'FROM JAN 1972'                                       || true
            'TO JAN 1972'                                         || true
            'BEF JAN 1972'                                        || true
            'AFT JAN 1972'                                        || true
            'CAL JAN 1972'                                        || true
            'EST JAN 1972'                                        || true
            'FROM JAN 1972 TO 1973'                               || true
            'BET JAN 1972 AND 1973'                               || true
            'BET 5 JAN 1712/13 AND 28 FEB 1714/15'                || true
            'BET @#DHEBREW@ 3 SVN 1972 AND @#DJULIAN@ 3 DEC 1972' || true
            'INT @#DHEBREW@ 2 TVT 5758 (interpreted Hebrew date)' || true
            'INT JAN 1972 (says so in the census)'                || true
            'INT JAN 1972 (says so in )(the census)'              || true
            '(date phrase)'                                       || true
            '()'                                                  || true

            ' 3 JAN 1972 '                                        || true
            '3 jan 1972 '                                         || true
            ' (03 jan 1972) '                                     || true

            '24 MAR 1692/93'                                      || true
            'MAR 1692/93'                                         || true
            '@#DJULIAN@ MAR 1692/93'                              || true

            '3 January 1972'                                      || false

            '24 MAR 1924/25'                                      || false
            'MAR 1922/24'                                         || false
            '25 DEC 1922/23'                                      || false

            ''                                                    || false
            '12'                                                  || false
            '99'                                                  || false
            '03 JAN 1972'                                         || false
            '03 JAN'                                              || false
            '31 FEB 1972'                                         || false
            '29 FEB 2019'                                         || false
            '31 JUN 1692/3'                                       || false
            '31 JUN 1692/xx'                                      || false
            '1692/93'                                             || false
            '03 JAN 1972 B.C.'                                    || false
            'JAN 1972 B.C.'                                       || false

            '@#DGREGORIAN@3 JAN 1972'                             || false
            '@#DJULIAN@ 5 AUG 1100 B.C.'                          || false
            '@#DJULIAN@ 03 DEC 1918'                              || false
            '@#DJULIAN@ 3 DEC'                                    || false
            '@#DROMAN@ 03 JAN 1972'                               || false
            '@#DFRENCH R@ 03 VEND 1972'                           || false
            '@#DUNKNOWN@ 03 JAN 1972'                             || false
            '@#DHEBREW@ 03 SVN 1972'                              || false
            '@#DHEBREW@ 03 SVN 1972 B.C.'                         || false

            'ABT1973'                                             || false
            'ABT 1973 TO 1975'                                    || false
            '(ABT 1973 TO 1975'                                   || false
            'INT JAN 1972()'                                      || false
            'BET 9 AND 12 JAN 1972'                               || false
    }

    void 'test for valid GEDCOM 5.5.1 dates'() {

        expect:

            new DateValue(input, V_551).validate().isValid() == result

        where:

            input                                                   || result

            '3 JAN 1972'                                            || true
            '03 JAN 1972'                                           || true
            '05 MAY 099'                                            || true
            'JAN 1972'                                              || true
            '1895'                                                  || true
            '099'                                                   || true
            '0012'                                                  || true
            '1895'                                                  || true
            '972'                                                   || true
            '3 JAN 1692/93'                                         || true
            '1692/93'                                               || true
            '044 B.C.'                                              || true

            '@#DGREGORIAN@ 03 JAN 1972'                             || true
            '@#DFRENCH R@ 03 VEND 1972'                             || true
            '@#DFRENCH R@ VEND 1803'                                || true
            '@#DFRENCH R@ 1972'                                     || true
            '@#DHEBREW@ 03 SVN 1972'                                || true
            '@#DJULIAN@ 03 DEC 1918'                                || true
            '@#DJULIAN@ 1100 B.C.'                                  || true

            'ABT JAN 1972'                                          || true
            'FROM JAN 1972'                                         || true
            'TO JAN 1972'                                           || true
            'BEF JAN 1972'                                          || true
            'AFT JAN 1972'                                          || true
            'AFT 20 Nov 1345'                                       || true
            'CAL JAN 1972'                                          || true
            'EST JAN 1972'                                          || true
            'FROM JAN 1972 TO 1973'                                 || true
            'BET JAN 1972 AND 1973'                                 || true
            'BET 5 APR 1712/13 AND 28 SEP 1714/15'                  || true
            'BET @#DHEBREW@ 03 SVN 1972 AND @#DJULIAN@ 03 DEC 1972' || true
            'INT @#DHEBREW@ 2 TVT 5758 (interpreted Hebrew date)'   || true
            'INT JAN 1972 (says so in the census)'                  || true
            'INT JAN 1972 (says so in )(the census)'                || true
            '(date phrase)'                                         || true
            '()'                                                    || true

            ' 03 JAN 1972 '                                         || true
            '03 jan 1972 '                                          || true
            ' (03 jan 1972) '                                       || true

            ''                                                      || false
            '12'                                                    || false
            '99'                                                    || false
            '31 JUN 1972'                                           || false
            '31 JUN 1692/3'                                         || false
            '31 JUN 1692/xx'                                        || false
            '3 JAN'                                                 || false
            '03 JAN 1972 B.C.'                                      || false
            'JAN 1972 B.C.'                                         || false
            '31 FEB 2019'                                           || false

            'January 1972'                                          || false
            'AFT 20 November 1345'                                  || false
            '20 November 1345'                                      || false

            '@#DGREGORIAN@03 JAN 1972'                              || false
            '@#DJULIAN@ 5 AUG 1100 B.C.'                            || false
            '@#DJULIAN@ 5 AUG 1501/02'                              || false
            '@#DROMAN@ 03 JAN 1972'                                 || false
            '@#DUNKNOWN@ 03 JAN 1972'                               || false
            '@#DHEBREW@ 03 SVN 1972 B.C.'                           || false

            'ABT1973'                                               || false
            'ABT 1973 TO 1975'                                      || false
            '(ABT 1973 TO 1975'                                     || false
            'INT JAN 1972()'                                        || false
            'BET 9 AND 12 JAN 1972'                                 || false
    }

    void 'test for valid GEDCOM 5.5 dates'() {

        expect:

            new DateValue(input).validate().isValid() == result

        where:

            input                                                   || result

            '3 JAN 1972'                                            || true
            '03 JAN 1972'                                           || true
            '03 JAN 1972 B.C.'                                      || true
            '05 MAY 099'                                            || true
            'JAN 1972'                                              || true
            '1895'                                                  || true
            '099'                                                   || true
            '0012'                                                  || true
            '1895'                                                  || true
            '972'                                                   || true
            '3 JAN 1692/93'                                         || true
            'JAN 1692/93'                                           || true
            '1692/93'                                               || true
            '044 B.C.'                                              || true
            ' 3 JAN 1972 '                                          || true

            '@#DGREGORIAN@ 03 JAN 1972'                             || true
            '@#DFRENCH R@ 03 VEND 1972'                             || true
            '@#DFRENCH R@ VEND 1803'                                || true
            '@#DFRENCH R@ 1972'                                     || true
            '@#DHEBREW@ 03 SVN 1972'                                || true
            '@#DJULIAN@ 03 DEC 1918'                                || true
            '@#DJULIAN@ 5 AUG 1100 B.C.'                            || true
            '@#DJULIAN@ 1100 B.C.'                                  || true
            '@#DJULIAN@ 24 MAR 1729'                                || true

            'ABT JAN 1972'                                          || true
            'FROM JAN 1972'                                         || true
            'TO JAN 1972'                                           || true
            'BEF JAN 1972'                                          || true
            'AFT JAN 1972'                                          || true
            'CAL JAN 1972'                                          || true
            'EST JAN 1972'                                          || true
            'FROM JAN 1972 TO 1973'                                 || true
            'BET JAN 1972 AND 1973'                                 || true
            'BET 5 APR 1712/13 AND 28 SEP 1714/15'                  || true
            'BET @#DHEBREW@ 03 SVN 1972 AND @#DJULIAN@ 03 DEC 1972' || true
            'INT @#DHEBREW@ 2 TVT 5758 (interpreted Hebrew date)'   || true
            'INT JAN 1972 (says so in the census)'                  || true
            'INT JAN 1972 (says so in )(the census)'                || true
            '(date phrase)'                                         || true
            '()'                                                    || true

            ' 03 JAN 1972 '                                         || true
            '03 jan 1972 '                                          || true
            ' (03 jan 1972) '                                       || true

            ''                                                      || false
            '12'                                                    || false
            '99'                                                    || false
            '31 JUN 1972'                                           || false
            '31 JUN 1692/3'                                         || false
            '31 JUN 1692/xx'                                        || false
            '3 JAN'                                                 || false
            '44 B.C.'                                               || false
            '044 BC'                                                || false

            '@#DGREGORIAN@03 JAN 1972'                              || false
            '@#DROMAN@ 03 JAN 1972'                                 || false
            '@#DUNKNOWN@ 03 JAN 1972'                               || false

            'ABT1973'                                               || false
            'ABT 1973 TO 1975'                                      || false
            '(ABT 1973 TO 1975'                                     || false
            'INT JAN 1972()'                                        || false
            'BET 9 AND 12 JAN 1972'                                 || false

            '3 JAN 1692 B.C./93'                                    || true // todo: should of course fail (but who would think of trying it)

    }

    void 'test for NPE'() {

        when:

            new DateValue(null)

        then:

            thrown NullPointerException
    }
}
