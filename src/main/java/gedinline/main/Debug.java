package gedinline.main;

import gedinline.lexical.InputLine;

public class Debug {

    private static final int DEBUG_LINE_NUMBER = -1;
//    private static final int DEBUG_LINE_NUMBER = 5;

    public static boolean active(InputLine inputLine) {
        if (active()) {
            return true;
        } else {
            return active(inputLine.getLineNumber());
        }
    }

    public static boolean active() {
        return false;
    }

    public static boolean active(int lineNumber) {
        return lineNumber == DEBUG_LINE_NUMBER;
    }
}
