package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

import java.time.*
import java.time.format.*

@CompileStatic
class Time extends Validator {

    Time() {
    }

    boolean isValid(String s1, GedcomVersion gedcomVersion) {

        def s2 = gedcomVersion.is70() && s1.endsWith('Z') ? s1.substring(0, s1.size() - 1) : s1

        if (s2.startsWith('24')) {
            return false
        }

        isCorrect(s2, 'H:m') ||
                isCorrect(s2, 'H:m:s') ||
                isCorrect(s2, 'H:m:s.S') ||
                isCorrect(s2, 'H:m:s.SS') ||
                isCorrect(s2, 'H:m:s.SSS')
    }

    private boolean isCorrect(String s, String pattern) {

        try {

            LocalTime.parse(s, DateTimeFormatter.ofPattern(pattern))

            true
        } catch (e) {
            false
        }
    }
}
