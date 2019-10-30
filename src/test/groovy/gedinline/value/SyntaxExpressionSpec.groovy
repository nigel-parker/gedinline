package gedinline.value

import spock.lang.*

class SyntaxExpressionSpec extends Specification {

    void test() {

        expect:

            new SyntaxExpression('aa').literal
            new SyntaxExpression('String').string
            new SyntaxExpression('<syntax-element>').syntaxElement
            new SyntaxExpression('[aa|b]').disjunction
            new SyntaxExpression('[a b|c]').disjunction
            new SyntaxExpression('[a|b c]').disjunction
            new SyntaxExpression('a b').conjunction
            new SyntaxExpression('[a] [b]').conjunction
            new SyntaxExpression('[<|>|<NULL>] [YYy MMm DDDd|YYy|MMm|DDDd|YYy MMm|YYy DDDd|MMm DDDd|CHILD|INFANT|STILLBORN]').conjunction

    }
}
