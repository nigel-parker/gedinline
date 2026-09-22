package gedinline.main

import spock.lang.*

class StructureListenerSpec extends Specification {

    void 'lines per second does not overflow for very large files'() {

        expect:

            StructureListener.linesPerSecond(lines, millis) == expected

        where:

            lines     | millis  || expected
            16_547    | 1_858   || 8_905
            4_281_483 | 88_000  || 48_653
            3_968_971 | 90_000  || 44_099
    }

    void 'warnings per 10000 lines does not overflow for very many warnings'() {

        expect:

            StructureListener.warningsPer10kLines(warnings, lines) == expected

        where:

            warnings | lines     || expected
            5        | 16_754    || 2
            942_367  | 3_157_249 || 2_984
    }
}
