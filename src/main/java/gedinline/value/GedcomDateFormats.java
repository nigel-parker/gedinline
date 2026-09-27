package gedinline.value;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Formatters that check a GEDCOM date phrase is a real calendar date. Case-insensitive months, 1-2 digit
 * days, any number of year digits and strict resolution (no 31 FEB). Immutable and thread-safe.
 */
public final class GedcomDateFormats {

    public static final DateTimeFormatter DAY_MONTH_YEAR = formatter("d MMM u");
    public static final DateTimeFormatter MONTH_YEAR = formatter("MMM u");
    public static final DateTimeFormatter YEAR = formatter("u");

    private GedcomDateFormats() {
    }

    public static boolean parses(String s, DateTimeFormatter formatter) {
        try {
            formatter.parse(s);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static DateTimeFormatter formatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
