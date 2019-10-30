package gedinline.value

import spock.lang.*

class ExpressionParserSpec extends Specification {

    void 'test simple expression'() {

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

            expression | input               || ok    | expectedValue | remainder

            'String'   | 'abc'               || true  | 'abc'         | ''
            'CAST'     | 'CAST'              || true  | 'CAST'        | ''
            'CAST'     | 'Cast'              || true  | 'CAST'        | ''
            'CAST'     | 'CAST of thousands' || true  | 'CAST'        | ' of thousands'
            'CAST'     | 'CART'              || false | null          | 'CART'
    }

    void 'test conjunction'() {

        when:

            def parsingResult = getParsingResult('CAST String', 'CAST of thousands')

        then:

            parsingResult.input == 'CAST of thousands'
            parsingResult.ok
            parsingResult.remainder == ''

            def result = parsingResult.value.concatenation as List<ParseResultValue>
            result.size() == 2
            result[0].string == 'CAST'
            result[1].string == ' of thousands'
    }

    void 'test disjunction'() {

        when:

            def parsingResult = getParsingResult('[CAST|String]', 'CASE closed')

        then:

            parsingResult.input == 'CASE closed'
            parsingResult.ok
            parsingResult.remainder == ''

            def result = parsingResult.value as StringResult
            result.string == 'CASE closed'
    }

    void 'test a little more complicated'() {

        when:

            def parser = new ExpressionParser(new SyntaxExpression('<DATE_LDS_ORD>'),
                    new SyntaxElementLocator() {
                        SyntaxElement find(String name) {
                            return new SyntaxElement(
                                    '<DATE_LDS_ORD>',
                                    new Cardinality('{Size=1:8}'),
                                    new SyntaxExpression('[a|b]'),
                                    '');
                        }
                    }, null)

            def parsingResult = parser.parse('b');
            def result = parsingResult.value as StringResult

        then:

            parsingResult.ok
            result.string == 'b'
            parsingResult.remainder == ''
    }

    void 'test pointer separation'() {

        when:

            def parsingResult = getParsingResult('String', '@F101@')

        then:

            !parsingResult.ok
            parsingResult.remainder == '@F101@'
    }

    private ParsingResult getParsingResult(String expression, String input) {
        new ExpressionParser(new SyntaxExpression(expression), null, null).parse(input)
    }
}
