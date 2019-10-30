package gedinline.value;

import com.google.common.collect.Lists;

import java.util.List;

public class ConcatenationResult extends ParseResultValue {

    private List<ParseResultValue> concatenation = Lists.newArrayList();

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
