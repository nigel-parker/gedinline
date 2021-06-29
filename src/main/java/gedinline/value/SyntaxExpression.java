package gedinline.value;

import gedinline.main.ValidatorBugException;
import org.apache.commons.lang.StringUtils;

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

    public Term getTerm() {
        return parseTree;
    }

    public List<SyntaxExpression> getTerms() {
        List<SyntaxExpression> result = new ArrayList<SyntaxExpression>();

        for (Term term : parseTree.getTerms()) {
            result.add(new SyntaxExpression(term.toString()));
        }

        return result;
    }

    public boolean isAgeAtEvent() {
        return parseTree.getAtom().equals("AgeAtEvent");
    }

    public boolean isConjunction() {
        return parseTree.isConjunction();
    }

    public boolean isDateValue() {
        return parseTree.getAtom().equals("DateValue");
    }

    public boolean isDecimal() {
        return parseTree.getAtom().equals("Decimal");
    }

    public boolean isDisjunction() {
        return parseTree.isDisjunction();
    }

    public boolean isList() {
        return parseTree.isList();
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

    public boolean isMediaType() {
        return parseTree.getAtom().equals("MediaType");
    }

    public boolean isNonSpaceString() {
        return parseTree.getAtom().equals("NonSpaceString");
    }

    public boolean isNull() {
        return parseTree.getAtom().equals("Null");
    }

    public boolean isPersonalName() { return parseTree.getAtom().equals("PersonalName"); }

    public boolean isPointer() {
        return parseTree.getAtom().equals("Pointer");
    }

    public boolean isRegex() {
        return parseTree.isRegex();
    }

    public boolean isString() {
        return parseTree.getAtom().equals("String");
    }

    public boolean isStringInBrackets() {
        return parseTree.getAtom().equals("StringInBrackets");
    }

    public boolean isSyntaxElement() {
        String atom = parseTree.getAtom();
        return parseTree.isAtom() && atom.startsWith("<") && atom.endsWith(">");
    }

    public boolean isTimeValue() {
        return parseTree.getAtom().equals("Time");
    }

    public boolean isWhitespace() {
        return parseTree.getAtom().equals("Whitespace");
    }

    public String getSyntaxElementName() {
        return isSyntaxElement() ? expression.substring(1, expression.length() - 1) : null;
    }

    private void parse(String e) {

        Stack<Term> stack = new Stack<Term>();

        String regexMarker = "regex:";
        String listMarker = "list:";

        if (e.startsWith(regexMarker)) {
            // have to do this a special case because regexes can contain chars which clash with disjunction syntax
            Term atom = new Term(Term.Type.ATOM);
            atom.addString(StringUtils.substringAfter(e, regexMarker));
            Term regex = new Term(Term.Type.REGEX);
            regex.addTerm(atom);
            parseTree = regex;
            return;
        } else if (e.startsWith(listMarker)) {
            stack.push(new Term(Term.Type.LIST));
            e = StringUtils.substringAfter(e, listMarker);
        }

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

