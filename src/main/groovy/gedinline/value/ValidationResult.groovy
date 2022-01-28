package gedinline.value

import groovy.transform.*

@CompileStatic
class ValidationResult {

    public static final ValidationResult TRUE = of(true)
    public static final ValidationResult FALSE = of(false)

    boolean valid
    String message

    static ValidationResult of(boolean valid) {
        new ValidationResult(valid: valid)
    }

    static ValidationResult of(String message) {
        new ValidationResult(message: message)
    }

    static ValidationResult combine(ValidationResult vr1, ValidationResult vr2) {
        if (vr1.hasMessage()) {
            vr1
        } else if (vr2.hasMessage()) {
            vr2
        } else if (vr2.isValid()) {
            vr1
        } else {
            vr2
        }
    }

    boolean hasMessage() {
        message
    }

    boolean isValid() {
        valid
    }

    String toString() {
        "valid: $valid message: $message"
    }
}
