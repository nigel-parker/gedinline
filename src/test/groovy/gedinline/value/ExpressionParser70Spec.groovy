package gedinline.value

import gedinline.lexical.*
import spock.lang.*

@Unroll
class ExpressionParser70Spec extends Specification {

    void 'test simple expression \'#expression\' / \'#input\''() {

        expect:

            def parsingResult = getParsingResult(expression, input)

            parsingResult.input == input
            parsingResult.ok == ok
            def actualValue = parsingResult.value

            if (expectedValue == null) {
                assert actualValue == null
            } else {
                assert (actualValue as StringResult).string == expectedValue
            }

            parsingResult.remainder == remainder

        where:

            expression             | input            || ok    | expectedValue | remainder

            'Pointer'              | '@F1@'           || true  | '@F1@'        | ''
            'Pointer'              | '@f1@'           || false | null          | '@f1@'
            'Pointer'              | '@F1-1@'         || false | null          | '@F1-1@'
            'Pointer'              | '@F1'            || false | null          | '@F1'

            'String'               | ''               || true  | ''            | ''
            'String'               | 'Abc'            || true  | 'Abc'         | ''

            '[Y|Null]'             | 'Y'              || true  | 'Y'           | ''
            '[Y|Null]'             | 'YX'             || true  | 'Y'           | 'X'
            '[Y|Null]'             | ''               || true  | ''            | ''
            '[Y|Null]'             | 'X'              || true  | ''            | 'X'
            '[Y|Null]'             | '['              || true  | ''            | '['

            'Decimal'              | '0'              || true  | '0'           | ''
            'Decimal'              | 'Abc'            || false | null          | 'Abc'
            'Decimal'              | '-1'             || false | null          | '-1'

            '[HUSB|WIFE]'          | 'husb'           || true  | 'HUSB'        | ''
            '[HUSB|WIFE]'          | 'hus'            || false | null          | 'hus'

            '[0|1|2|3]'            | '3'              || true  | '3'           | ''

            'regex:_[A-Z0-9_]* .*' | '_A URI'         || true  | ''            | ''

            'list:[A|B|C]'         | 'B, A'           || true  | ''            | ''
            'list:[A|B|C]'         | ''               || false | null          | ''
            'list:[A|B|C]'         | ' '              || false | null          | ' '

            'list:String'          | ' '              || true  | ''            | ''
            'list:String'          | ', , one, more,' || true  | ''            | ''
    }

    private ParsingResult getParsingResult(String expression, String input) {
        new ExpressionParser(new SyntaxExpression(expression), null, GedcomVersion.V_70).parse(input)
    }
}
