package gedinline.lexical


import org.joda.time.*

import java.text.*

import static java.util.Calendar.*

class DualYearValidator {

    static final PATTERN_1 = 'dd MMM yyyy'
    static final PATTERN_2 = 'MMM yyyy'

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

        def year = date[YEAR]

        def last2Digits1 = year % 100
        def last2Digits2 = dualYear.substring(1) as int

        if (last2Digits2 != last2Digits1 + 1) {
            return false
        }

        def oldNewYear = new LocalDate(year, 3, 25).toDateTimeAtStartOfDay().toDate()

        if (date >= oldNewYear) {
            return false
        }

        if (year >= 1924) {
            return false
        }

        true
    }

    Date getDate() {
        try {
            new SimpleDateFormat(variant1 ? PATTERN_1 : PATTERN_2, Locale.ENGLISH).parse(s1)
        } catch (Exception ignored) {
            null
        }
    }
}
