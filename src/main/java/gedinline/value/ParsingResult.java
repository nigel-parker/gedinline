package gedinline.value;

public class ParsingResult {

    private SyntaxElement syntaxElement;
    private String input;
    private boolean ok;
    private ParseResultValue value;
    private String remainder;
    private String errorMessage;

    public ParsingResult() {
    }

    public SyntaxElement getSyntaxElement() {
        return syntaxElement;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasErrorMessage() {
        return errorMessage != null;
    }

    public String getInput() {
        return input;
    }

    public boolean isOk() {
        return ok;
    }

    public ParseResultValue getValue() {
        return value;
    }

    public String getRemainder() {
        return remainder;
    }

    public boolean parsedEverythingOk() {
        return isOk() && parsingIsFinished();
    }

    public boolean parsingIsFinished() {
        return remainder.equals("");
    }

    public void setSyntaxElement(SyntaxElement syntaxElement) {
        this.syntaxElement = syntaxElement;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setValue(ParseResultValue value) {
        this.value = value;
    }

    public void setRemainder(String remainder) {
        this.remainder = remainder;
    }
}
