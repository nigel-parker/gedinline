package gedinline.value

import gedinline.lexical.*
import gedinline.main.*
import groovy.transform.*

import static gedinline.lexical.GedcomVersion.*
import static gedinline.value.DateValue70.*
import static gedinline.value.ValidationResult.*

@CompileStatic
class DateExact extends Validator {

    DateExact() {
    }

    ValidationResult validate(String s, GedcomVersion gedcomVersion) {
        def vr1 = isValid(s, gedcomVersion)
        def vr2 = isValid(s.toUpperCase(), gedcomVersion)

        if (vr1) {
            TRUE
        } else if (vr2) {
            ValidationResult.of(MONTH_VALUES_UPPER);
        } else {
            FALSE
        }
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        assert gedcomVersion == V_70

        def integer = /[1-9][0-9]*/
        def day = "(?<day>$integer)"
        def extension = /_[A-Z0-9_]*/
        def month = "(?<month>(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|$extension))"
        def year = "(?<year>$integer)"

        def matcher = s =~ "$day $month $year"

        if (!matcher.matches()) {
            return false
        }

        def result = [
                day  : matcher.group('day'),
                month: matcher.group('month'),
                year : matcher.group('year')
        ]

        if (Debug.active()) {
            println "--- DateExact parse result = ${result}"
        }

        true
    }
}
