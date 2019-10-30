package gedinline.value;

import com.google.common.collect.ImmutableList;
import gedinline.lexical.DualYearValidator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nigel
 * Date: Jul 8, 2011
 * Time: 12:45:52 PM
 */
@SuppressWarnings("Duplicates")
public class DateValue555 {

    private static final String FROM = "FROM ";
    private static final String TO = " TO ";
    private static final String BET = "BET ";
    private static final String AND = " AND ";
    private static final String INT = "INT ";
    private static final String MONTHS = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
    private static final String JULIAN_REGEX = "(1|2|3)?\\d )?" + MONTHS + " (\\d{3,4})(\\/\\d{2})?|(\\d{3,4})( B\\.C\\.| BC| BCE)?";
    private static final String SPACE_OPEN_BRACKET = " (";
    private static final String OPEN_BRACKET = "(";
    private static final String ESCAPE_F = "@#DFRENCH R@ ";
    private static final String ESCAPE_G = "@#DGREGORIAN@ ";
    private static final String ESCAPE_H = "@#DHEBREW@ ";
    private static final String ESCAPE_J = "@#DJULIAN@ ";

    private static DateTimeFormatter FORMAT_1 = DateTimeFormat.forPattern("dd MMM yyyy")
            .withLocale(Locale.ENGLISH)
            .withZone(DateTimeZone.UTC);
    private static DateTimeFormatter FORMAT_2 = DateTimeFormat.forPattern("MMM yyyy")
            .withLocale(Locale.ENGLISH)
            .withZone(DateTimeZone.UTC);
    private static DateTimeFormatter FORMAT_3 = DateTimeFormat.forPattern("yyyy")
            .withLocale(Locale.ENGLISH)
            .withZone(DateTimeZone.UTC);

    private String s;

    public DateValue555(String s) {
        this.s = s.toUpperCase().trim();
    }

    public boolean isValid() {

        if (isValidDatePhrase(s)) {
            return true;
        }

        if (s.startsWith(FROM) && s.contains(TO)) {
            return isValidDate(StringUtils.substringBetween(s, FROM, TO)) &&
                    isValidDate(StringUtils.substringAfter(s, TO));
        }

        if (s.startsWith(BET) && s.contains(AND)) {
            return isValidDate(StringUtils.substringBetween(s, BET, AND)) &&
                    isValidDate(StringUtils.substringAfter(s, AND));
        }

        if (s.startsWith(INT) && s.contains(SPACE_OPEN_BRACKET)) {
            return isValidDate(StringUtils.substringBetween(s, INT, SPACE_OPEN_BRACKET)) &&
                    isValidDatePhrase(OPEN_BRACKET + StringUtils.substringAfter(s, SPACE_OPEN_BRACKET));
        }

        for (String prefix : ImmutableList.of("ABT", "AFT", "BEF", "CAL", "EST", "FROM", "TO")) {
            String prefix1 = prefix + " ";

            if (s.startsWith(prefix1)) {
                return isValidDate(s.substring(prefix1.length()));
            }
        }

        return isValidDate(s);
    }

    private boolean isValidDatePhrase(String s1) {
        return s1.startsWith(OPEN_BRACKET) && s1.endsWith(")");
    }

    private boolean isValidDate(String s1) {

        Calendar calendar = Calendar.GREGORIAN;

        if (s1.startsWith("@#D")) {
            if (s1.startsWith(ESCAPE_F)) {
                calendar = Calendar.FRENCH;
                s1 = StringUtils.substringAfter(s1, ESCAPE_F);
            } else if (s1.startsWith(ESCAPE_G)) {
                s1 = StringUtils.substringAfter(s1, ESCAPE_G);
            } else if (s1.startsWith(ESCAPE_H)) {
                calendar = Calendar.HEBREW;
                s1 = StringUtils.substringAfter(s1, ESCAPE_H);
            } else if (s1.startsWith(ESCAPE_J)) {
                calendar = Calendar.JULIAN;
                s1 = StringUtils.substringAfter(s1, ESCAPE_J);
            } else {
                return false;
            }
        }

        switch (calendar) {
            case GREGORIAN:
                return isValidGregorianDate(s1);

            case FRENCH:
                return isValidFrenchDate(s1);

            case HEBREW:
                return isValidHebrewDate(s1);

            case JULIAN:
                return isValidJulianDate(s1);

        }

        return false;
    }

    private boolean isValidGregorianDate(String s1) {

        // the regex checks for one of 3 valid patterns:
        //
        // [D] M Y [/99]
        //       Y [BC]
        //  D  M

        String regex = "^((" + JULIAN_REGEX + "|(((1|2|3)?\\d)) " + MONTHS + ")$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s1);
        boolean valid = matcher.find();

        if (!valid) {
            return false;
        }

        String dualYear = matcher.group(6);
        String bcMarker = matcher.group(8);
        boolean yearMissing = matcher.group(10) != null;

        if (dualYear != null) {
            s1 = s1.substring(0, s1.length() - 3);
        }

        if (bcMarker != null || yearMissing) {
            // we have tested enough
            return true;
        }

        boolean b = checkValidDate(s1, FORMAT_1);

        if (!b) {
            b = checkValidDate(s1, FORMAT_2);
        }

        if (!b) {
            b = checkValidDate(s1, FORMAT_3);
        }

        if (b && dualYear != null) {
            b = new DualYearValidator(s1, dualYear).isValid();
        }

        return b;
    }

    private boolean isValidJulianDate(String s1) {

        String regex = "^((" + JULIAN_REGEX + ")$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s1);

        return matcher.find();
    }

    private boolean isValidFrenchDate(String s1) {
        return s1.matches("(((1|2|3)?\\d )?(VEND|BRUM|FRIM|NIVO|PLUV|VENT|GERM|FLOR|PRAI|MESS|THER|FRUC|COMP) )?\\d{3,4}");
    }

    private boolean isValidHebrewDate(String s1) {
        return s1.matches("(((1|2|3)?\\d )?(TSH|CSH|KSL|TVT|SHV|ADR|ADS|NSN|IYR|SVN|TMZ|AAV|ELL) )?\\d{3,4}");
    }

    private boolean checkYearLength(String s) {
        int length = s.length();

        return (length >= 3 && StringUtils.isNumeric(s.substring(length - 3)));
    }

    private boolean checkValidDate(String s, DateTimeFormatter formatter) {
        try {

            formatter.parseDateTime(s);
            return checkYearLength(s);

        } catch (Exception e) {
            return false;
        }
    }

    enum Calendar {
        GREGORIAN, JULIAN, FRENCH, HEBREW
    }
}
