package gedinline.lexical

import java.time.*
import java.time.format.*

import static gedinline.value.GedcomDateFormats.*

class DualYearValidator {

    String s1
    String dualYear
    boolean variant1

    DualYearValidator(String s1, String dualYear) {
        this.s1 = s1
        this.dualYear = dualYear
        variant1 = s1.length() >= 9
    }

    boolean isValid() {

        def date = getDate()

        if (!date) {
            return false
        }

        def year = date.year

        def last2Digits1 = year % 100
        def last2Digits2 = dualYear.substring(1) as int

        if (last2Digits2 != last2Digits1 + 1) {
            return false
        }

        def oldNewYear = LocalDate.of(year, 3, 25)

        if (!date.isBefore(oldNewYear)) {
            return false
        }

        if (year >= 1924) {
            return false
        }

        true
    }

    LocalDate getDate() {
        try {
            variant1 ? LocalDate.parse(s1, DAY_MONTH_YEAR) : YearMonth.parse(s1, MONTH_YEAR).atDay(1)
        } catch (DateTimeParseException ignored) {
            null
        }
    }
}
