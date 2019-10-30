package gedinline.main;

public class ValidatorBugException extends RuntimeException {

    public ValidatorBugException(String s) {
        super(s);
    }

    public ValidatorBugException(String s, Exception e) {
        super(s, e);
    }
}
