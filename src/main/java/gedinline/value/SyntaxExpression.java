package gedinline.value;

import gedinline.main.ValidatorBugException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SyntaxExpression {

    private String expression;
    private Term parseTree;

    public SyntaxExpression(String expression) {
        this.expression = expression;
        parse(expression);
    }

    public String getExpression() {
        return expression;
    }

    public List<SyntaxExpression> getTerms() {
        List<SyntaxExpression> result = new ArrayList<SyntaxExpression>();

        for (Term term : parseTree.getTerms()) {
            result.add(new SyntaxExpression(term.toString()));
        }

        return result;
    }

    public boolean isConjunction() {
        return parseTree.isConjunction();
    }

    public boolean isDisjunction() {
        return parseTree.isDisjunction();
    }

    public boolean isRegex() {
        String atom = parseTree.getAtom();
        return parseTree.isAtom() && atom.startsWith("regex:");
    }

    public boolean isSyntaxElement() {
        String atom = parseTree.getAtom();
        return parseTree.isAtom() && atom.startsWith("<") && atom.endsWith(">");
    }

    public boolean isLiteral() {
        return parseTree.isAtom() &&
                !isSyntaxElement() &&
                !isString() &&
                !isWhitespace() &&
                !isNull() &&
                !isPointer() &&
                !isRegex() &&
                !isDecimal();
    }

    public boolean isDecimal() {
        return parseTree.getAtom().equals("Decimal");
    }

    public boolean isString() {
        return parseTree.getAtom().equals("String");
    }

    public boolean isStringInBrackets() {
        return parseTree.getAtom().equals("StringInBrackets");
    }

    public boolean isNonSpaceString() {
        return parseTree.getAtom().equals("NonSpaceString");
    }

    public boolean isTimeValue() {
        return parseTree.getAtom().equals("Time");
    }

    public boolean isAgeAtEvent() {
        return parseTree.getAtom().equals("AgeAtEvent");
    }

    public boolean isWhitespace() {
        return parseTree.getAtom().equals("Whitespace");
    }

    public boolean isNull() {
        return parseTree.getAtom().equals("Null");
    }

    public boolean isPointer() {
        return parseTree.getAtom().equals("Pointer");
    }

    public boolean isDateValue() {
        return parseTree.getAtom().equals("DateValue");
    }

    public String getSyntaxElementName() {
        return isSyntaxElement() ? expression.substring(1, expression.length() - 1) : null;
    }

    private void parse(String e) {

        if (e.startsWith("regex:")) {
            Term term = new Term(Term.Type.ATOM);
            term.addString(e);
            parseTree = term;
            return;
        }

        Stack<Term> stack = new Stack<Term>();

        for (char c : e.toCharArray()) {
            switch (c) {
                case '[':
                    stack.push(new Term(Term.Type.DISJUNCTION));
                    break;

                case '|':
                case ']':
                    Term term = stack.pop();
                    Term term5 = stack.peek();
                    term5.addTerm(term);

                    if (term5.isConjunction()) {
                        stack.pop();
                        stack.peek().addTerm(term5);
                    }

                    break;

                case ' ':
                    Term term2 = stack.pop();

                    if (!stack.isEmpty() && stack.peek().isConjunction()) {
                        stack.peek().addTerm(term2);
                    } else {
                        Term term4 = new Term(Term.Type.CONJUNCTION);
                        term4.addTerm(term2);
                        stack.push(term4);
                    }

                    break;

                default:
                    Term term6;

                    if (stack.isEmpty() || !stack.peek().isAtom()) {
                        term6 = new Term(Term.Type.ATOM);
                        stack.push(term6);
                    } else {
                        term6 = stack.peek();
                    }

                    term6.addString(Character.toString(c));
            }
        }

        if (stack.size() >= 2) {
            Term term = stack.pop();
            stack.peek().addTerm(term);
        }

        if (stack.size() > 1) {
            throw new ValidatorBugException("Error in parsing " + expression);
        }

        if (stack.isEmpty()) {
            Term term = new Term(Term.Type.ATOM);
            term.addString("");
            parseTree = term;
        } else {
            parseTree = stack.pop();
        }
    }

    public String toString() {
        return parseTree.toString();
    }
}

