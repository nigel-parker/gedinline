package gedinline.main;

import gedinline.lexical.InputLine;

public class Debug {

    private static final int DEBUG_LINE_NUMBER = -1;
//    private static final int DEBUG_LINE_NUMBER = 11;

    public static boolean active(InputLine inputLine) {
        return active(inputLine.getLineNumber());
//        return true;
    }

    public static boolean active(int lineNumber) {
        return lineNumber == DEBUG_LINE_NUMBER;
    }
}
