package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class PointerSpec extends Specification {

    void 'test Pointer validation for \'#input\''() {

        expect:

            new Pointer(input, gedcomVersion).isValid() == expectedResult

        where:

            gedcomVersion | input          | expectedResult

            V_551         | '@A@'          | true
            V_551         | '@a@'          | true
            V_551         | '@9@'          | true
            V_551         | '@Z9_@'        | true
            V_551         | '@Z!"#$%&/()@' | true

            V_551         | '@_@'          | false
            V_551         | '@A@@'         | false

            V_70          | '@A@'          | true
            V_70          | '@9@'          | true
            V_70          | '@_@'          | true
            V_70          | '@Z9_@'        | true
            V_70          | '@Z9_@'        | true
            V_70          | '@VOID@'       | true

            V_70          | '@A@@'         | false
            V_70          | 'A'            | false
            V_70          | 'A@'           | false
            V_70          | '@A'           | false
            V_70          | '@@'           | false
            V_70          | '@a@'          | false
            V_70          | '@#@'          | false
            V_70          | 'file:disk'    | false
    }
}
