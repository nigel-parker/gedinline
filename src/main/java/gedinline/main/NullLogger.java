package gedinline.main;

import gedinline.lexical.InputLine;

public class NullLogger implements WarningSink{

    public void warning(String message) {
    }

    public void warning(InputLine inputLine, String s) {
    }

    public void warning(int lineNumber, String s) {
    }
}
