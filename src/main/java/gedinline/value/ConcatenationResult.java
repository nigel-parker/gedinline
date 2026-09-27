package gedinline.value;

import java.util.ArrayList;
import java.util.List;

public class ConcatenationResult extends ParseResultValue {

    private List<ParseResultValue> concatenation = new ArrayList<>();

    public ConcatenationResult() {
    }

    public void add(ParseResultValue value) {
        concatenation.add(value);
    }

    public ParseResultValue get(int i) {
        return concatenation.get(i);
    }

    public List<ParseResultValue> getConcatenation() {
        return concatenation;
    }

    public int size() {
        return concatenation.size();
    }
}
