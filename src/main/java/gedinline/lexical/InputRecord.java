package gedinline.lexical;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class InputRecord {

    private InputLine inputLine;
    private List<InputRecord> inputRecords = Lists.newArrayList();

    public InputRecord(InputLine inputLine) {
        this.inputLine = inputLine;
    }

    public void add(InputRecord inputRecord) {
        inputRecords.add(inputRecord);
    }

    public InputLine getInputLine() {
        return inputLine;
    }

    public List<InputRecord> getInputRecords() {
        return inputRecords;
    }

    public int getTagCount(Tag tag) {
        int count = 0;

        for (InputRecord inputRecord : inputRecords) {
            Tag tag1 = inputRecord.getInputLine().getTag();

            if (tag1.equals(tag)) {
                count++;
            }
        }

        return count;
    }

    public Set<Tag> getSubTags() {
        Set<Tag> result = Sets.newHashSet();

        for (InputRecord inputRecord : inputRecords) {
            result.add(inputRecord.getInputLine().getTag());
        }

        return result;
    }

    public String toIndentedString() {
        String s = inputLine.toString() + "\n";

        for (InputRecord inputRecord : inputRecords) {
            s += inputRecord.toString();
        }

        return s;
    }

    public String toString() {
        return inputLine.toString();
    }
}
