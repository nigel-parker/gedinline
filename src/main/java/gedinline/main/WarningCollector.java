package gedinline.main;

import com.google.common.collect.Maps;
import gedinline.lexical.InputLine;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Stack;

public class WarningCollector implements WarningSink {

    private Stack<WarningCount> stack = new Stack<WarningCount>();
    private StructureListener structureListener;
    private Map<String, Integer> counts = Maps.newHashMap();

    public WarningCollector(StructureListener structureListener) {
        this.structureListener = structureListener;
    }

    public int getCount() {
        return stack.pop().count;
    }

    public void setCountMode() {
        stack.push(new WarningCount());
    }

    public void warning(String message) {
        warning(0, message);
    }

    public void warning(InputLine inputLine, String s) {
        warning(inputLine.getLineNumber(), s);
    }

    public void warning(int lineNumber, String s) {
        String prefix = lineNumber == 0 ? "" : "*** Line " + lineNumber + ": ";
        String logMessage = StringUtils.rightPad(prefix, 18) + s;

        if (stack.isEmpty()) {
            if (!counts.containsKey(s)) {
                counts.put(s, 1);
            } else {
                counts.put(s, counts.get(s) + 1);
            }

            if (counts.get(s) <= 5) {
                structureListener.handleLogWarning(lineNumber, logMessage);
            }

        } else {
            stack.peek().incrementCount();
        }
    }

    static class WarningCount {
        int count = 0;

        void incrementCount() {
            count++;
        }
    }
}
