package gedinline.lexical

import gedinline.main.*
import spock.lang.*

@SuppressWarnings("GroovyPointlessBoolean")
class InputLineSpec extends Specification {

    void 'test input line parsing for 5.5.1'() {

        expect:

            InputLinePrecursor precursor = new InputLinePrecursor(1, new Level(0), input, "")
            InputLine inputLine = new InputLine(precursor, GedcomVersion.V_551, new NullLogger())

            if (label) {
                assert inputLine.label.pointer == label
            } else {
                assert inputLine.label == null
            }

            inputLine.tag.tag == tag

            if (pointer) {
                assert inputLine.pointer.pointer == pointer
            } else {
                assert inputLine.pointer == null
            }

            inputLine.value == value

        where:

            input                        | label | tag    | pointer | value

            'HEAD'                       | null  | 'HEAD' | null    | ''
            '@a@ HEAD'                   | '@a@' | 'HEAD' | null    | ''
            'HEAD @a@'                   | null  | 'HEAD' | '@a@'   | '@a@'
            'HEAD a b c'                 | null  | 'HEAD' | null    | 'a b c'
            'DATE @#DJULIAN@ 5 FEB 1667' | null  | 'DATE' | null    | '@#DJULIAN@ 5 FEB 1667'
    }

    void 'test input line parsing for 7.0'() {

        expect:

            InputLinePrecursor precursor = new InputLinePrecursor(1, new Level(0), input, "")
            InputLine inputLine = new InputLine(precursor, GedcomVersion.V_70, new NullLogger())

            if (label) {
                assert inputLine.label.pointer == label
            } else {
                assert inputLine.label == null
            }

            inputLine.tag.tag == tag

            if (pointer) {
                assert inputLine.pointer.pointer == pointer
            } else {
                assert inputLine.pointer == null
            }

            inputLine.value == value

        where:

            input        | label | tag    | pointer | value

            'HEAD'       | null  | 'HEAD' | null    | ''
            '@A@ HEAD'   | '@A@' | 'HEAD' | null    | ''
            'HEAD @A@'   | null  | 'HEAD' | '@A@'   | '@A@'
            'HEAD a b c' | null  | 'HEAD' | null    | 'a b c'
            'CONT @me'   | null  | 'CONT' | '@me'   | '@me'
            'CONT @@me'  | null  | 'CONT' | null    | '@me'
            'CONT @@@@@' | null  | 'CONT' | null    | '@@@@'
            'CONT @#'    | null  | 'CONT' | '@#'    | '@#'
    }
}