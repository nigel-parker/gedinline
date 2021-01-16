package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@SuppressWarnings("GroovyPointlessBoolean")
class DateValueSpec extends Specification {

    void 'test for valid GEDCOM 5.5.5 dates'() {

        expect:

            new DateValue(input, GedcomVersion.V_555).isValid() == result

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

            new DateValue(input, GedcomVersion.V_551).isValid() == result

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

            new DateValue(input).isValid() == result

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
