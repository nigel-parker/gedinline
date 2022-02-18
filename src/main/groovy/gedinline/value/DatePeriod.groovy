package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

import java.util.regex.*

import static gedinline.lexical.GedcomVersion.*
import static gedinline.value.ValidationResult.*

@CompileStatic
class DatePeriod extends Validator {

    DatePeriod() {
    }

    ValidationResult validate(String s, GedcomVersion gedcomVersion) {

        assert gedcomVersion == V_70

        if (s == '') {
            return TRUE
        }

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
            return FALSE
        }

        try {

            check(matcher, 'dateFrom1')
            check(matcher, 'dateFrom2')
            check(matcher, 'dateTo1')
            check(matcher, 'dateTo2')

            TRUE
        } catch (DateValidationFailure dvf) {
            dvf.validationResult
        }
    }

    private void check(Matcher matcher, String date) {

        def group = matcher.group(date)

        if (group != null) {
            def result = DateValue70.isValidDate(group)

            if (!result.isValid()) {
                throw new DateValidationFailure(result)
            }
        }
    }

    private class DateValidationFailure extends RuntimeException {
        ValidationResult validationResult

        DateValidationFailure(ValidationResult validationResult) {
            this.validationResult = validationResult
        }
    }
}
