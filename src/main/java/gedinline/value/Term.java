package gedinline.value;

import gedinline.main.ValidatorBugException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Term {

    enum Type {
        LIST, CONJUNCTION, DISJUNCTION, REGEX, ATOM
    }

    private String atom = "";
    private List<Term> terms = new ArrayList<>();
    private Type type;

    public Term(Type type) {
        this.type = type;
    }

    public void addTerm(Term term) {
        if (isAtom()) {
            throw new ValidatorBugException("Can't add terms to atom " + this);
        }

        terms.add(term);
    }

    public void addString(String s) {
        if (!isAtom()) {
            throw new ValidatorBugException("Cant add strings to non-atoms: " + s);
        }

        atom += s;
    }

    public String getAtom() {
        return atom;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public boolean isAtom() {
        return type == Type.ATOM;
    }

    public boolean isConjunction() {
        return type == Type.CONJUNCTION;
    }

    public boolean isDisjunction() {
        return type == Type.DISJUNCTION;
    }

    public boolean isList() {
        return type == Type.LIST;
    }

    public boolean isRegex() {
        return type == Type.REGEX;
    }

    public String toString() {
        if (isAtom()) {
            return atom;
        } else if (isConjunction()) {
            return join(" ");
        } else {
            return "[" + join("|") + "]";
        }
    }

    private String join(String separator) {
        return terms.stream().map(String::valueOf).collect(Collectors.joining(separator));
    }
}
