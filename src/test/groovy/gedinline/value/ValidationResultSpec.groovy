package gedinline.value

import spock.lang.*

class ValidationResultSpec extends Specification {

    void test() {

        expect:

            def errorMessage = 'error message'

            def vr1 = ValidationResult.TRUE
            def vr2 = ValidationResult.FALSE
            def vr3 = ValidationResult.of(errorMessage)
            def vr4 = ValidationResult.combine(vr1, vr3)
            def vr5 = ValidationResult.combine(vr2, vr3)

            vr1.isValid() == true
            vr2.isValid() == false
            vr3.isValid() == false
            vr4.isValid() == false
            vr5.isValid() == false

            vr1.message == null
            vr2.message == null
            vr3.message == errorMessage
            vr4.message == errorMessage
            vr5.message == errorMessage
    }
}
