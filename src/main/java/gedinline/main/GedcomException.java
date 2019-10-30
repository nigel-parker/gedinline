package gedinline.main;

import gedinline.lexical.InputLine;

public class GedcomException extends RuntimeException {

    public GedcomException(InputLine inputLine, String s) {
        super("Line " + inputLine.getLineNumber() + ": " + s);
    }

    public GedcomException(String s) {
        super(s);
    }
}
