package gedinline.value;

import org.joda.time.format.DateTimeFormat;

public class TimeValue {

    private String s;

    public TimeValue(String s) {
        this.s = s;
    }

    public boolean isValid() {
        return isValid("HH:mm") || isValid("HH:mm:ss")|| isValid("HH:mm:ss.SSS");
    }

    private boolean isValid(String pattern) {
        try {
            DateTimeFormat.forPattern(pattern).parseDateTime(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
