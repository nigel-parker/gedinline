package gedinline.lexical

import gedinline.main.*
import gedinline.value.*
import spock.lang.*

class XrefCollectorSpec extends Specification {

    void test() {

        given:

            def xrefCollector = new XrefCollector()
            def gedcomVersion = GedcomVersion.V_70

            def p11 = new Pointer('@1@', gedcomVersion)
            def p12 = new Pointer('@1@', gedcomVersion)
            def p2 = new Pointer('@2@', gedcomVersion)
            def p3 = new Pointer('@3@', gedcomVersion)
            def pVoid = new Pointer('@VOID@', gedcomVersion)

        expect:

            pVoid.isVoid()

        when:

            xrefCollector.addLabel(p11)
            xrefCollector.addLabel(p12)

        then:

            def e1 = thrown(GedcomException)
            e1.message == "Duplicate occurrence of label @1@"


        when:

            xrefCollector.addLabel(p2)
            xrefCollector.addLabel(pVoid)

        then:

            def e2 = thrown(GedcomException)
            e2.message == "Invalid label @VOID@"

        when:

            xrefCollector.addPointer(p11)
            xrefCollector.addPointer(p12)
            xrefCollector.addPointer(p2)
            xrefCollector.addPointer(p3)
            xrefCollector.addPointer(pVoid)

        then:

            xrefCollector.getUnsatisfiedPointers() == [p3] as Set
    }
}
