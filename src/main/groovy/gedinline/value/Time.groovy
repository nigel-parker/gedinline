package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

import java.time.*
import java.time.format.*

@CompileStatic
class Time extends Validator {

    static DateTimeFormatter DTF1 = DateTimeFormatter.ofPattern('H:m')
    static DateTimeFormatter DTF2 = DateTimeFormatter.ofPattern('H:m:s')
    static DateTimeFormatter DTF3 = DateTimeFormatter.ofPattern('H:m:s.S')
    static DateTimeFormatter DTF4 = DateTimeFormatter.ofPattern('H:m:s.SS')
    static DateTimeFormatter DTF5 = DateTimeFormatter.ofPattern('H:m:s.SSS')

    Time() {
    }

    boolean isValid(String s1, GedcomVersion gedcomVersion) {

        def s2 = gedcomVersion.is70() && s1.endsWith('Z') ? s1.substring(0, s1.size() - 1) : s1

        if (s2.startsWith('24')) {
            return false
        }

        isCorrect(s2, DTF1) ||
                isCorrect(s2, DTF2) ||
                isCorrect(s2, DTF3) ||
                isCorrect(s2, DTF4) ||
                isCorrect(s2, DTF5)
    }

    private boolean isCorrect(String s, DateTimeFormatter pattern) {

        try {

            LocalTime.parse(s, pattern)

            true
        } catch (e) {
            false
        }
    }
}
