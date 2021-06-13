package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersionNew.*

@Unroll
class ValueGrammarSpec extends Specification {

    void 'test value grammar for #element \'#input\' version #version '() {

        expect:

            def grammar = new ValueGrammar(version)
            def syntaxElement = grammar.find(element)
            def expressionParser = new ExpressionParser(syntaxElement, grammar, version)
            ParsingResult result1 = expressionParser.parse(input)
            String remainder = result1.getRemainder()

            result1.isOk() == result
            remainder == '' || remainder == input

        where:

            element                    | input                                   | version || result

            'ADDRESS_EMAIL'            | 'nigel.parker@mazeppa.no'               | V_551   || true
            'ADDRESS_EMAIL'            | 'nigel.parker@sottovoce.no'             | V_555   || true
            'ADDRESS_FAX'              | '+47 98245799'                          | V_551   || true
            'ADDRESS_WEB_PAGE'         | 'https://www.aftenposten.no'            | V_551   || true
            'ADOPTED_BY_WHICH_PARENT'  | 'BOTH'                                  | V_55    || true
            'ADOPTED_BY_WHICH_PARENT'  | 'BOTH'                                  | V_555   || true
            'AGE_AT_EVENT'             | '108y 1d'                               | V_555   || true
            'AGE_AT_EVENT'             | 'CHILD'                                 | V_55    || true
            'ATTRIBUTE_DESCRIPTOR'     | 'Woodworking'                           | V_551   || true
            'CHILD_LINKAGE_STATUS'     | 'disproven'                             | V_551   || true
            'DATE_LDS_ORD'             | 'BET 05 APR 1712/13 AND 28 SEP 1714/15' | V_55    || true
            'DATE_PERIOD'              | 'FROM Jan 1820 TO DEC 1825'             | V_555   || true
            'DATE_RANGE'               | 'BET 1971 AND 1972'                     | V_55    || true
            'DATE_RANGE'               | 'BET 1971 AND 1972'                     | V_555   || true
            'DATE_VALUE'               | '(anything here)'                       | V_55    || true
            'DATE_VALUE'               | '(anything here)'                       | V_555   || true
            'DATE_VALUE'               | '24 FEB 1736/37'                        | V_55    || true
            'DATE_VALUE'               | '24 FEB 1736/37'                        | V_555   || true
            'DATE_VALUE'               | '665'                                   | V_55    || true
            'DATE_VALUE'               | '665'                                   | V_555   || true
            'DATE_VALUE'               | 'ABT 4 DEC 1971'                        | V_55    || true
            'DATE_VALUE'               | 'ABT 4 DEC 1971'                        | V_555   || true
            'DATE_VALUE'               | 'ABT 665'                               | V_55    || true
            'DATE_VALUE'               | 'ABT 665'                               | V_555   || true
            'DATE_VALUE'               | 'BEF 775'                               | V_55    || true
            'DATE_VALUE'               | 'BEF 775'                               | V_555   || true
            'DATE_VALUE'               | 'FROM APR 2000 TO 5 MAR 2001'           | V_55    || true
            'DATE_VALUE'               | 'FROM APR 2000 TO 5 MAR 2001'           | V_555   || true
            'DATE_VALUE'               | 'INT 1792 (French Revolution)'          | V_55    || true
            'DATE_VALUE'               | 'INT 1792 (French Revolution)'          | V_555   || true
            'DATE'                     | '1972'                                  | V_555   || true
            'DATE'                     | '4 DEC 1972'                            | V_55    || true
            'DATE'                     | '4 DEC 1972'                            | V_555   || true
            'DATE'                     | 'MAR 1972'                              | V_555   || true
            'EVENTS_RECORDED'          | ''                                      | V_55    || false
            'EVENTS_RECORDED'          | 'ADOP,CAST'                             | V_55    || true
            'EVENTS_RECORDED'          | 'BIRT, CHR'                             | V_55    || true
            'EVENTS_RECORDED'          | 'BIRT, DEAT, MARR'                      | V_555   || true
            'EVENTS_RECORDED'          | 'BIRT,CHR'                              | V_55    || true
            'GEDCOM_FORM'              | 'LINEAGE-LINKED'                        | V_55    || true
            'LANGUAGE_ID'              | 'Belorusian'                            | V_555   || true
            'LANGUAGE_ID'              | 'Groovy'                                | V_555   || false
            'NAME_PERSONAL'            | '/Parker/ Esq'                          | V_55    || true
            'NAME_PERSONAL'            | '/Parker/ Esq'                          | V_555   || true
            'NAME_PERSONAL'            | '/Parker/'                              | V_55    || true
            'NAME_PERSONAL'            | '/Parker/'                              | V_555   || true
            'NAME_PERSONAL'            | '/Parker/Esq'                           | V_55    || true
            'NAME_PERSONAL'            | '/Parker/Esq'                           | V_555   || true
            'NAME_PERSONAL'            | 'John /Parker/ Esq'                     | V_55    || true
            'NAME_PERSONAL'            | 'John /Parker/ Esq'                     | V_555   || true
            'NAME_PERSONAL'            | 'John /Parker/'                         | V_55    || true
            'NAME_PERSONAL'            | 'John /Parker/'                         | V_555   || true
            'NAME_PERSONAL'            | 'John'                                  | V_55    || true
            'NAME_PERSONAL'            | 'John'                                  | V_555   || true
            'NAME_PHONETIC_VARIATION'  | 'harakiri'                              | V_551   || true
            'NAME_PIECE_GIVEN'         | 'Nigel, Munro'                          | V_55    || true
            'NAME_PIECE_SURNAME'       | ''                                      | V_55    || false
            'NAME_ROMANIZED_VARIATION' | 'kana'                                  | V_551   || true
            'NAME_TYPE'                | 'aka'                                   | V_551   || true
            'PHONETIC_TYPE'            | 'kana2'                                 | V_551   || true
            'PHONETISATION_METHOD'     | 'hangul'                                | V_555   || true
            'PHONETISATION_METHOD'     | 'kana'                                  | V_555   || true
            'PHONETISATION_METHOD'     | 'abcde'                                 | V_555   || true
            'PLACE_LATITUDE'           | 'N18.150944'                            | V_551   || true
            'PLACE_LONGITUDE'          | 'E168.150944'                           | V_551   || true
            'PLACE_VALUE'              | ''                                      | V_551   || false
            'PRODUCT_VERSION_NUMBER'   | '0.0'                                   | V_555   || true
            'PRODUCT_VERSION_NUMBER'   | '5.'                                    | V_555   || false
            'PRODUCT_VERSION_NUMBER'   | '999.999.999.999'                       | V_555   || true
            'RECEIVING_SYSTEM_NAME'    | 'S2345678901234567890'                  | V_55    || true
            'RECEIVING_SYSTEM_NAME'    | 'S234567890123456789012345'             | V_55    || false
            'RECEIVING_SYSTEM_NAME'    | 'Slekt'                                 | V_55    || true
            'ROLE_IN_EVENT'            | '(Revolutionary)'                       | V_55    || true
            'ROLE_IN_EVENT'            | 'SPOU'                                  | V_55    || true
            'ROMANIZED_TYPE'           | 'pinyin'                                | V_551   || true
            'SEX_VALUE_551'            | 'U'                                     | V_551   || true
            'TIME_VALUE'               | '23:59:59.999'                          | V_55    || true
            'TIME_VALUE'               | '23:59:59.999'                          | V_551   || true
            'TIME_VALUE'               | '23:59:59.999'                          | V_555   || true
            'XREF:INDI'                | '@F4097@'                               | V_55    || true

//            'EVENTS_RECORDED'          | 'ADOP,CAST2'                            | V_55    || false
//            'RECEIVING_SYSTEM_NAME'    | 'Slekt V1.0'                            | V_55    || false
//            'RECEIVING_SYSTEM_NAME'    | 'ANY'                                   | V_555   || false
//            'APPROVED_SYSTEM_ID'       | 'Slekt V1.0'                            | V_55    || false

    }
}
