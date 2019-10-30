package gedinline.main;

import gedinline.lexical.InputLine;

public interface WarningSink {

    void warning(String message);

    void warning(InputLine inputLine, String s);

    void warning(int lineNumber, String s);
}
