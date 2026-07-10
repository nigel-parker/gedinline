package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

/**
 * Both g7:SOUR-EVEN and g7:DATA-EVEN draw their values from the enumeration set
 * g7:enumset-EVENATTR, which is the union of the event tags (spec 7.0.15 section 3.3.1)
 * and the attribute tags (section 3.3.2). The LDS ordinances of section 3.3.3 are not
 * members of that set.
 */
@Unroll
class EventAttributeEnumSpec extends Specification {

    void 'test #element accepts \'#input\''() {

        expect:

            isValid(element, input) == expectedResult

        where:

            element         | input              || expectedResult

            'g7:SOUR-EVEN'  | 'BIRT'             || true
            'g7:SOUR-EVEN'  | 'MARR'             || true
            'g7:SOUR-EVEN'  | 'ORDN'             || true
            'g7:SOUR-EVEN'  | 'ORD'              || false
            'g7:SOUR-EVEN'  | 'OCCU'             || true
            'g7:SOUR-EVEN'  | 'RESI'             || true
            'g7:SOUR-EVEN'  | 'TITL'             || true
            'g7:SOUR-EVEN'  | 'EVEN'             || true
            'g7:SOUR-EVEN'  | 'FACT'             || true
            'g7:SOUR-EVEN'  | 'BAPL'             || false
            'g7:SOUR-EVEN'  | 'BIRT, DEAT'       || false

            // An alternative that is a strict prefix of another must not shadow it

            'g7:SOUR-EVEN'  | 'CHR'              || true
            'g7:SOUR-EVEN'  | 'CHRA'             || true
            'g7:SOUR-EVEN'  | 'DIV'              || true
            'g7:SOUR-EVEN'  | 'DIVF'             || true
            'g7:SOUR-EVEN'  | 'CHRX'             || false

            'g7:DATA-EVEN'  | 'BIRT'             || true
            'g7:DATA-EVEN'  | 'BIRT, DEAT, MARR' || true
            'g7:DATA-EVEN'  | 'ORDN'             || true
            'g7:DATA-EVEN'  | 'ORD'              || false
            'g7:DATA-EVEN'  | 'EVEN'             || true
            'g7:DATA-EVEN'  | 'FACT'             || true
            'g7:DATA-EVEN'  | 'BAPL'             || false

            // A matched alternative must consume its element entirely; the list parser
            // must not silently discard whatever trails it.

            'g7:DATA-EVEN'  | 'BIRTXX'           || false
            'g7:DATA-EVEN'  | 'ORDNXX'           || false
            'g7:DATA-EVEN'  | 'BIRT, DEATXX'     || false
            'g7:DATA-EVEN'  | 'BIRT, ZZZZ'       || false

            'g7:DATA-EVEN'  | 'CHRA'             || true
            'g7:DATA-EVEN'  | 'DIVF'             || true
            'g7:DATA-EVEN'  | 'BIRT, CHRA'       || true

            // g7:NO draws from g7:enumset-EVEN, which holds the event tags only:
            // no attributes, and not the generic EVEN tag

            'g7:NO'         | 'BIRT'             || true
            'g7:NO'         | 'ORDN'             || true
            'g7:NO'         | 'CHRA'             || true
            'g7:NO'         | 'DIVF'             || true
            'g7:NO'         | 'ORD'              || false
            'g7:NO'         | 'OCCU'             || false
            'g7:NO'         | 'EVEN'             || false
            'g7:NO'         | 'FACT'             || false
    }

    private boolean isValid(String element, String input) {

        def grammar = new ValueGrammar(V_70)
        def expressionParser = new ExpressionParser(grammar.find(element), grammar, V_70)
        ParsingResult parsingResult = expressionParser.parse(input)

        parsingResult.parsedEverythingOk()
    }
}
