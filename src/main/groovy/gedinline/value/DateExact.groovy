package gedinline.value

import gedinline.lexical.*
import gedinline.main.*
import groovy.transform.*

@CompileStatic
class DateExact extends Validator {

    DateExact() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

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
