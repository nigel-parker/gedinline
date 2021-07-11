package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

import java.util.regex.*

@CompileStatic
class DatePeriod extends Validator {

    DatePeriod() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def dateFrom1 = /(?<dateFrom1>.*)/
        def dateFrom2 = /(?<dateFrom2>.*)/
        def dateTo1 = /(?<dateTo1>.*)/
        def dateTo2 = /(?<dateTo2>.*)/
        def from1 = "FROM $dateFrom1"
        def from2 = "FROM $dateFrom2"
        def to1 = "TO $dateTo1"
        def to2 = "TO $dateTo2"
        def both1 = "$from1 $to1"

        def regex = "($both1|$from2|$to2)"
        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        try {

            check(matcher, 'dateFrom1')
            check(matcher, 'dateFrom2')
            check(matcher, 'dateTo1')
            check(matcher, 'dateTo2')

            true
        } catch (Exception e) {
            false
        }
    }

    private void check(Matcher matcher, String date) {

        def group = matcher.group(date)

        if (group != null && !DateValue70.isValidDate(group)) {
            throw new RuntimeException()
        }
    }
}
