package gedinline.value;

import com.google.common.collect.ImmutableList;
import gedinline.lexical.GedcomVersionNew;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import static gedinline.lexical.GedcomVersionNew.V_55;
import static gedinline.lexical.GedcomVersionNew.V_551;

/**
 * User: nigel
 * Date: Jul 8, 2011
 * Time: 12:45:52 PM
 */
@SuppressWarnings("Duplicates")
public class DateValue {

    private static final String FROM = "FROM ";
    private static final String TO = " TO ";
    private static final String BET = "BET ";
    private static final String AND = " AND ";
    private static final String INT = "INT ";
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

    private String originalString;
    private String s;
    private GedcomVersionNew gedcomVersion;

    public DateValue(String s) {
        this(s, V_55);
    }

    public DateValue(String s, GedcomVersionNew gedcomVersion) {
        this.originalString = s;
        this.s = s.toUpperCase().trim();
        this.gedcomVersion = gedcomVersion;
    }

    public boolean isValid() {

        if (gedcomVersion.is555()) {
            return new DateValue555(s).isValid();
        } else if (gedcomVersion.is70()) {
            return new DateValue70(originalString).isValid();
        }

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
                calendar = Calendar.GREGORIAN;
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
        int length = s1.length();

        if (length >= 3 && s1.substring(length - 3).startsWith("/")) {
            if (!StringUtils.isNumeric(s1.substring(length - 2))) {
                return false;
            }

            s1 = s1.substring(0, length - 3);
        }

        length = s1.length();
        boolean bc = length >= 4 && s1.substring(length - 4).equals("B.C.");

        if (bc) {
            s1 = s1.substring(0, length - 4).trim();
        }

        if (bc && gedcomVersion.equals(V_551)) {
            return checkPattern(s1, FORMAT_3);
        } else {
            boolean b = checkPattern(s1, FORMAT_1);

            if (!b) {
                b = checkPattern(s1, FORMAT_2);
            }

            if (!b) {
                b = checkPattern(s1, FORMAT_3);
            }

            return b;
        }
    }

    private boolean isValidFrenchDate(String s1) {
        return s1.matches("((\\d{1,2} )?(VEND|BRUM|FRIM|NIVO|PLUV|VENT|GERM|FLOR|PRAI|MESS|THER|FRUC|COMP) )?\\d{3,4}");
    }

    private boolean isValidHebrewDate(String s1) {
        return s1.matches("((\\d{1,2} )?(TSH|CSH|KSL|TVT|SHV|ADR|ADS|NSN|IYR|SVN|TMZ|AAV|ELL) )?\\d{3,4}");
    }

    private boolean isValidJulianDate(String s1) {
        int length = s1.length();
        boolean bc = length >= 4 && s1.substring(length - 4).equals("B.C.");

        if (bc && gedcomVersion.equals(V_551)) {
            s1 = s1.substring(0, length - 4).trim();
            return s1.matches("\\d{3,4}");
        } else {
            return s1.matches("((\\d{1,2} )?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC) )?\\d{3,4}( B\\.C\\.)?");
        }
    }

    private boolean checkPattern(String s, DateTimeFormatter formatter) {
        try {

            if (!s.matches("((\\d{1,2} )?(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC) )?\\d{3,4}")) {
                return false;
            }

            formatter.parseDateTime(s);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    enum Calendar {
        GREGORIAN, JULIAN, FRENCH, HEBREW
    }
}
