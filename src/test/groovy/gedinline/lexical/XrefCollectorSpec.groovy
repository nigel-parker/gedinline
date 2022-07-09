package gedinline.lexical

import gedinline.main.*
import gedinline.value.*
import spock.lang.*

class XrefCollectorSpec extends Specification {

    void test() {

        given:

            def indiTag = new Tag('INDI')
            def xrefCollector = new XrefCollector()
            def gedcomVersion = GedcomVersion.V_70

            def p11 = new Pointer('@1@', gedcomVersion)
            def p12 = new Pointer('@1@', gedcomVersion)
            def p2 = new Pointer('@2@', gedcomVersion)
            def p3 = new Pointer('@3@', gedcomVersion)
            def pVoid = new Pointer('@VOID@', gedcomVersion)
            def pInvalid = new Pointer('@#@', gedcomVersion)

        expect:

            pVoid.isVoid()
            !pInvalid.isValid()

        when:

            xrefCollector.addLabel(p11, indiTag)
            xrefCollector.addLabel(p12, indiTag)

        then:

            def e1 = thrown(GedcomException)
            e1.message == "Duplicate occurrence of label @1@"

        when:

            xrefCollector.addLabel(p2, indiTag)
            xrefCollector.addLabel(pVoid, indiTag)

        then:

            def e2 = thrown(GedcomException)
            e2.message == "Invalid label @VOID@"

        when:

            xrefCollector.addPointer(p11, indiTag)
            xrefCollector.addPointer(p12, indiTag)
            xrefCollector.addPointer(p2, indiTag)
            xrefCollector.addPointer(p3, indiTag)
            xrefCollector.addPointer(pVoid, indiTag)

        then:

            xrefCollector.getUnsatisfiedPointers() == [p3] as Set

        when:

            xrefCollector.addPointer(pInvalid, indiTag)

        then:

            xrefCollector.getUnsatisfiedPointers() == [p3] as Set
    }

    void 'test for handling of extension tags'() {

        given:

            def tag = new Tag('_LOC')
            def xrefCollector = new XrefCollector()
            def gedcomVersion = GedcomVersion.V_70

            def p1 = new Pointer('@LOC1@', gedcomVersion)
            def p2 = new Pointer('@LOC2@', gedcomVersion)
            def p3 = new Pointer('@LOC3@', gedcomVersion)

        when:

            xrefCollector.addPointer(p1, tag)
            xrefCollector.addLabel(p1, tag)
            xrefCollector.addLabel(p2, tag)
            xrefCollector.addPointer(p2, tag)
            xrefCollector.addPointer(p3, tag)

        then:

            xrefCollector.getUnsatisfiedPointers() == [p3] as Set

    }
}
