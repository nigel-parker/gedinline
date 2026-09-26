package gedinline.main;

import gedinline.lexical.InputLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The outcome of validating one level-0 record, recorded as an ordered list of events. Replaying the events in
 * order against the real WarningCollector and listeners reproduces exactly the side effects that validating the
 * record on the main thread would have had, which is what allows records to be validated on worker threads.
 */
public class RecordResult {

    /**
     * Marker for the two kinds of event.
     */
    public interface Event {
    }

    /**
     * A warning as it would have been passed to the WarningCollector. Line number 0 means "no line".
     */
    public static final class Warning implements Event {

        private final int lineNumber;
        private final String message;

        public Warning(int lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Warning(" + lineNumber + ", " + message + ")";
        }
    }

    /**
     * One input line was validated. On replay the listeners are notified of the line first; if a listener
     * objects, its message replaces the line's own warnings, exactly as in the single-threaded validator.
     */
    public static final class LineValidated implements Event {

        private final InputLine inputLine;
        private final List<Warning> warnings;

        public LineValidated(InputLine inputLine, List<Warning> warnings) {
            this.inputLine = inputLine;
            this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
        }

        public InputLine getInputLine() {
            return inputLine;
        }

        public List<Warning> getWarnings() {
            return warnings;
        }

        @Override
        public String toString() {
            return "LineValidated(" + inputLine.getLineNumber() + ", " + warnings + ")";
        }
    }

    private final List<Event> events = new ArrayList<>();

    public void add(Event event) {
        events.add(event);
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public String toString() {
        return events.toString();
    }
}
