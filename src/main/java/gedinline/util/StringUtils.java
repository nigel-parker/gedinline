package gedinline.util;

import java.util.StringTokenizer;

/**
 * The subset of commons-lang 2.1 StringUtils used by GED-inline, with the same null handling and edge cases.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        if (s != null) {
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    public static String substringAfter(String s, String separator) {
        if (isEmpty(s)) {
            return s;
        }

        if (separator == null) {
            return "";
        }

        int pos = s.indexOf(separator);
        return pos == -1 ? "" : s.substring(pos + separator.length());
    }

    public static String substringBefore(String s, String separator) {
        if (isEmpty(s) || separator == null) {
            return s;
        }

        if (separator.isEmpty()) {
            return "";
        }

        int pos = s.indexOf(separator);
        return pos == -1 ? s : s.substring(0, pos);
    }

    public static String substringBetween(String s, String open, String close) {
        if (s == null || open == null || close == null) {
            return null;
        }

        int start = s.indexOf(open);

        if (start != -1) {
            int end = s.indexOf(close, start + open.length());

            if (end != -1) {
                return s.substring(start + open.length(), end);
            }
        }

        return null;
    }

    public static String rightPad(String s, int size) {
        return s == null || s.length() >= size ? s : s + " ".repeat(size - s.length());
    }

    public static String leftPad(String s, int size) {
        return s == null || s.length() >= size ? s : " ".repeat(size - s.length()) + s;
    }

    public static String abbreviate(String s, int maxWidth) {
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }

        return s == null || s.length() <= maxWidth ? s : s.substring(0, maxWidth - 3) + "...";
    }

    public static int indexOfAny(String s, String searchChars) {
        if (isEmpty(s) || isEmpty(searchChars)) {
            return -1;
        }

        for (int i = 0; i < s.length(); i++) {
            if (searchChars.indexOf(s.charAt(i)) >= 0) {
                return i;
            }
        }

        return -1;
    }

    public static int indexOfAnyBut(String s, String searchChars) {
        if (isEmpty(s) || isEmpty(searchChars)) {
            return -1;
        }

        for (int i = 0; i < s.length(); i++) {
            if (searchChars.indexOf(s.charAt(i)) < 0) {
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(String s, String searchString) {
        return s == null || searchString == null ? -1 : s.indexOf(searchString);
    }

    public static boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static String[] split(String s, char separator) {
        if (s == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(s, String.valueOf(separator));
        String[] result = new String[tokenizer.countTokens()];

        for (int i = 0; i < result.length; i++) {
            result[i] = tokenizer.nextToken();
        }

        return result;
    }

    public static String repeat(String s, int n) {
        if (s == null) {
            return null;
        }

        return n <= 0 ? "" : s.repeat(n);
    }

    public static String remove(String s, char c) {
        return isEmpty(s) || s.indexOf(c) == -1 ? s : s.replace(String.valueOf(c), "");
    }
}
