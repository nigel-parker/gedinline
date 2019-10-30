package gedinline.value;

import gedinline.main.ValidatorBugException;
import org.apache.commons.lang.StringUtils;

public class Cardinality {

    private int minimum;
    private int maximum;

    public Cardinality(String cardinality) {
        cardinality = cardinality.trim();

        if (!cardinality.matches("\\{Size=\\d+(:\\d+)?}")) {
            throw new ValidatorBugException("Invalid cardinality " + cardinality);
        }

        if (cardinality.contains(":")) {
            minimum = Integer.parseInt(StringUtils.substringBetween(cardinality, "=", ":"));
            maximum = Integer.parseInt(StringUtils.substringBetween(cardinality, ":", "}"));
        } else {
            minimum = Integer.parseInt(StringUtils.substringBetween(cardinality, "=", "}"));
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
