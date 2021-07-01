package gedinline.value

import spock.lang.*

class SyntaxExpressionSpec extends Specification {

    void test() {

        expect:

            new SyntaxExpression('String').string
            new SyntaxExpression('<syntax-element>').syntaxElement
            new SyntaxExpression('[aa|b]').disjunction
            new SyntaxExpression('[a b|c]').disjunction
            new SyntaxExpression('[a|b c]').disjunction
            new SyntaxExpression('a b').conjunction
            new SyntaxExpression('[a] [b]').conjunction
            new SyntaxExpression('[<|>|<NULL>] [YYy MMm DDDd|YYy|MMm|DDDd|YYy MMm|YYy DDDd|MMm DDDd|CHILD|INFANT|STILLBORN]').conjunction
            new SyntaxExpression('regex:A').regex
            new SyntaxExpression('regex:\\d{1,3}\\.\\d{1,3}(\\.\\d{1,3}(\\.\\d{1,3})?)?').regex
            new SyntaxExpression('regex:_[A-Z0-9_]*').regex

            def syntaxExpression1 = new SyntaxExpression('list:String')
            syntaxExpression1.list
            syntaxExpression1.term.terms.first().isAtom()

            def syntaxExpression2 = new SyntaxExpression('list:[a!b]')
            syntaxExpression2.list
            syntaxExpression2.term.terms.first().isDisjunction()
    }
}
