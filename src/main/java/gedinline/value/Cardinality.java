package gedinline.value;

import gedinline.main.ValidatorBugException;
import org.apache.commons.lang.StringUtils;

public class Cardinality {

    private int minimum;
    private int maximum;

    public Cardinality(String c1) {
        String c2 = c1.equals("") ? "{Size=1:9999}" : c1.trim();

        if (!c2.matches("\\{Size=\\d+(:\\d+)?}")) {
            throw new ValidatorBugException("Invalid cardinality " + c2);
        }

        if (c2.contains(":")) {
            minimum = Integer.parseInt(StringUtils.substringBetween(c2, "=", ":"));
            maximum = Integer.parseInt(StringUtils.substringBetween(c2, ":", "}"));
        } else {
            minimum = Integer.parseInt(StringUtils.substringBetween(c2, "=", "}"));
            maximum = minimum;
        }
    }

    public int getMaximum() {
        return maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public String toString() {
        return "{Size=" + minimum + ":" + maximum + "}";
    }
}
