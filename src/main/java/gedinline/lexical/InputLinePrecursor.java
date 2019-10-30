package gedinline.lexical;

public class InputLinePrecursor {

    private int lineNumber;
    private Level level;
    private String line;

    public InputLinePrecursor(int lineNumber, Level level, String line) {
        this.lineNumber = lineNumber;
        this.level = level;
        this.line = line;
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
        return level + " " + line;
    }

    public String toString() {
        return lineNumber + ": " + getLevelAndLine();
    }
}
