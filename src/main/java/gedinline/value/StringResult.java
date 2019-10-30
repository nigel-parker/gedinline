package gedinline.value;

public class StringResult extends ParseResultValue {

    private String literal;

    public StringResult(String literal) {
        this.literal = literal;
    }

    public String getString() {
        return literal;
    }
}
