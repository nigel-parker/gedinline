package gedinline.value;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gedinline.value.ExpressionParser.REPLACE_WITH;

@SuppressWarnings("Duplicates")
public class DateValue70 {

    public static final String MONTH_VALUES_UPPER = REPLACE_WITH + "Month values must be upper case";

    private static final String FROM = "FROM ";
    private static final String TO = " TO ";
    private static final String BET = "BET ";
    private static final String AND = " AND ";
    private static final String DAYS = "((1|2|3)?\\d )";
    private static final String MONTHS = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
    private static final String YEARS = "(\\d{1,4})";
    private static final String JULIAN_REGEX = "^(" + DAYS + "?" + MONTHS + " )?" + YEARS + "( BCE)?$";
    private static final Pattern JULIAN_PATTERN = Pattern.compile(JULIAN_REGEX);

    private static final String CAL_FRENCH = "FRENCH_R ";
    private static final String CAL_GREGORIAN = "GREGORIAN ";
    private static final String CAL_HEBREW = "HEBREW ";
    private static final String CAL_JULIAN = "JULIAN ";
    private static final List<String> PREFIXES = ImmutableList.of("ABT", "AFT", "BEF", "CAL", "EST", "FROM", "TO");

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

    public DateValue70(String s) {
        this.s = s.trim();
    }

    public ValidationResult isValid() {

        if (s.equals("")) {
            return ValidationResult.TRUE;
        }

        if (s.startsWith(FROM) && s.contains(TO)) {
            ValidationResult fromTo = isValidDate(StringUtils.substringBetween(s, FROM, TO));
            ValidationResult to = isValidDate(StringUtils.substringAfter(s, TO));

            return ValidationResult.combine(fromTo, to);
        }

        if (s.startsWith(BET) && s.contains(AND)) {
            ValidationResult betAnd = isValidDate(StringUtils.substringBetween(s, BET, AND));
            ValidationResult and = isValidDate(StringUtils.substringAfter(s, AND));

            return ValidationResult.combine(betAnd, and);
        }

        for (String prefix : PREFIXES) {
            String prefix1 = prefix + " ";

            if (s.startsWith(prefix1)) {
                return isValidDate(s.substring(prefix1.length()));
            }
        }

        return isValidDate(s);
    }

    static ValidationResult isValidDate(String s) {
        boolean sValid = isValidDateInternal(s);
        boolean sToUpperCaseValid = isValidDateInternal(s.toUpperCase());

        if (sValid) {
            return ValidationResult.TRUE;
        } else if (sToUpperCaseValid) {
            return ValidationResult.of(MONTH_VALUES_UPPER);
        } else {
            return ValidationResult.FALSE;
        }
    }

    static boolean isValidDateInternal(String s1) {

        if (s1.startsWith(CAL_FRENCH)) {
            return isValidFrenchDate(StringUtils.substringAfter(s1, CAL_FRENCH));

        } else if (s1.startsWith(CAL_HEBREW)) {
            return isValidHebrewDate(StringUtils.substringAfter(s1, CAL_HEBREW));

        } else if (s1.startsWith(CAL_JULIAN)) {
            return isValidJulianDate(StringUtils.substringAfter(s1, CAL_JULIAN));

        } else if (s1.startsWith(CAL_GREGORIAN)) {
            return isValidGregorianDate(StringUtils.substringAfter(s1, CAL_GREGORIAN));

        } else {
            return isValidGregorianDate(s1);
        }
    }

    private static boolean isValidGregorianDate(String s1) {

        Matcher matcher = JULIAN_PATTERN.matcher(s1);
        boolean valid = matcher.find();

        if (!valid) {
            return false;
        }

        int year = Integer.valueOf(matcher.group(5));

        if (year == 0) {
            return false;
        }

        String bcMarker = matcher.group(6);

        if (bcMarker != null) {
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

        return b;
    }

    private static boolean isValidJulianDate(String s1) {
        return JULIAN_PATTERN.matcher(s1).find();
    }

    private static boolean isValidFrenchDate(String s1) {
        return s1.matches("(((1|2|3)?\\d )?(VEND|BRUM|FRIM|NIVO|PLUV|VENT|GERM|FLOR|PRAI|MESS|THER|FRUC|COMP) )?" + YEARS);
    }

    private static boolean isValidHebrewDate(String s1) {
        return s1.matches("(((1|2|3)?\\d )?(TSH|CSH|KSL|TVT|SHV|ADR|ADS|NSN|IYR|SVN|TMZ|AAV|ELL) )?" + YEARS);
    }

    private static boolean checkValidDate(String s, DateTimeFormatter formatter) {
        try {

            formatter.parseDateTime(s);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
