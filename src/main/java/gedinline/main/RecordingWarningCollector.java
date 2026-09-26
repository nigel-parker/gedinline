package gedinline.main;

import gedinline.lexical.InputLine;
import gedinline.main.RecordResult.Warning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * A WarningSink that records warnings instead of reporting them, so that they can be replayed later in the right
 * order. It has the same count mode as WarningCollector: while a count frame is active, warnings are only counted,
 * which the record validator uses to choose between alternative grammar subtrees. Not thread-safe: each thread
 * uses its own instance.
 */
public class RecordingWarningCollector implements WarningSink {

    private final Deque<int[]> countFrames = new ArrayDeque<>();
    private List<Warning> warnings = new ArrayList<>();
    private Consumer<Warning> target = warnings::add;

    public void setCountMode() {
        countFrames.push(new int[1]);
    }

    public int getCount() {
        return countFrames.pop()[0];
    }

    public boolean isCountMode() {
        return !countFrames.isEmpty();
    }

    @Override
    public void warning(String message) {
        warning(0, message);
    }

    @Override
    public void warning(InputLine inputLine, String s) {
        warning(inputLine.getLineNumber(), s);
    }

    @Override
    public void warning(int lineNumber, String s) {
        if (countFrames.isEmpty()) {
            target.accept(new Warning(lineNumber, s));
        } else {
            countFrames.peek()[0]++;
        }
    }

    /**
     * Sends subsequent warnings to the given consumer instead of the internal list, returning the previous target
     * so that the caller can restore it.
     */
    public Consumer<Warning> redirect(Consumer<Warning> newTarget) {
        Consumer<Warning> previous = target;
        target = newTarget;
        return previous;
    }

    /**
     * Returns the warnings recorded so far and starts a new, empty list.
     */
    public List<Warning> drain() {
        List<Warning> result = warnings;
        warnings = new ArrayList<>();
        target = warnings::add;
        return result;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }
}
