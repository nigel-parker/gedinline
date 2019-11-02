package gedinline.lexical;

public class InputLinePrecursor {

    private int lineNumber;
    private Level level;
    private String line;
    private String originalLine;

    public InputLinePrecursor(int lineNumber, Level level, String line, String originalLine) {
        this.lineNumber = lineNumber;
        this.level = level;
        this.line = line;
        this.originalLine = originalLine;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Level getLevel() {
        return level;
    }

    public String getLine() {
        return line;
    }

    public String getLevelAndLine() {
        return originalLine;
    }

    public String toString() {
        return lineNumber + ": " + getLevelAndLine();
    }
}
