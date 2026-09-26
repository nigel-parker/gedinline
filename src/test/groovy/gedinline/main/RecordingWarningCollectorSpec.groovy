package gedinline.main

import gedinline.lexical.*
import spock.lang.*

import static gedinline.main.RecordResult.Warning

class RecordingWarningCollectorSpec extends Specification {

    def collector = new RecordingWarningCollector()

    void 'warnings are recorded in the order they arrive'() {

        when:

            collector.warning(3, 'three')
            collector.warning('no line')
            collector.warning(inputLine(7), 'seven')

        then:

            collector.warnings*.lineNumber == [3, 0, 7]
            collector.warnings*.message == ['three', 'no line', 'seven']
    }

    void 'count mode counts instead of recording'() {

        when:

            collector.warning(1, 'before')
            collector.setCountMode()
            collector.warning(2, 'counted')
            collector.warning(3, 'counted too')
            def count = collector.count
            collector.warning(4, 'after')

        then:

            count == 2
            collector.warnings*.lineNumber == [1, 4]
    }

    void 'count frames nest'() {

        when:

            collector.setCountMode()
            collector.warning(1, 'outer')
            collector.setCountMode()
            collector.warning(2, 'inner')
            def inner = collector.count
            collector.warning(3, 'outer again')
            def outer = collector.count

        then:

            inner == 1
            outer == 2
            collector.warnings.isEmpty()
    }

    void 'drain hands over the recorded warnings and starts afresh'() {

        when:

            collector.warning(1, 'a')
            def first = collector.drain()
            collector.warning(2, 'b')
            def second = collector.drain()

        then:

            first*.message == ['a']
            second*.message == ['b']
            collector.warnings.isEmpty()
    }

    void 'warnings can be redirected to another list and back'() {

        given:

            List<Warning> lineWarnings = []

        when:

            collector.warning(1, 'record')
            def previous = collector.redirect { lineWarnings << it }
            collector.warning(2, 'line')
            collector.redirect(previous)
            collector.warning(3, 'record again')

        then:

            lineWarnings*.message == ['line']
            collector.warnings*.message == ['record', 'record again']
    }

    private InputLine inputLine(int lineNumber) {
        new InputLine(new InputLinePrecursor(lineNumber, new Level(0), 'HEAD', '0 HEAD'), GedcomVersion.V_551, new NullLogger())
    }
}
