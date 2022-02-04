package gedinline.main

import gedinline.lexical.*
import gedinline.value.*
import spock.lang.*

class LinkListenerSpec extends Specification {

    static GedcomVersion gedcomVersion = null

    LinkListener linkListener

    void 'FAMC link present'() {

        when:

            inputLine '@I1@', 0, 'INDI', null
            inputLine null, 1, 'FAMC', '@F1@'

            inputLine '@F1@', 0, 'FAM', null
            inputLine null, 1, 'CHIL', '@I1@'

        then:

            linkListener.warningCount == 0
    }

    void 'FAMC link missing'() {

        when:

            inputLine '@I1@', 0, 'INDI', null

            inputLine '@F1@', 0, 'FAM', null
            inputLine null, 1, 'CHIL', '@I1@'

        then:

            linkListener.warningCount == 1
            linkListener.missingFamc.toString() == '[(@I1@, @F1@)]'
    }

    void 'FAMS link present'() {

        when:

            inputLine '@I1@', 0, 'INDI', null
            inputLine null, 1, 'FAMS', '@F1@'

            inputLine '@F1@', 0, 'FAM', null
            inputLine null, 1, 'HUSB', '@I1@'
            inputLine null, 1, 'WIFE', '@I1@'

        then:

            linkListener.warningCount == 0
    }

    void 'FAMS link missing'() {

        when:

            inputLine '@I1@', 0, 'INDI', null

            inputLine '@F1@', 0, 'FAM', null
            inputLine null, 1, 'HUSB', '@I1@'
            inputLine null, 1, 'WIFE', '@I1@'

        then:

            linkListener.warningCount == 1
            linkListener.missingFams.toString() == '[(@I1@, @F1@)]'
    }

    void 'test for pointer/pair equality'() {

        expect:

            def p1 = new Pointer('@I1@', gedcomVersion)
            def p2 = new Pointer('@I1@', gedcomVersion)
            def f1 = new Pointer('@Ff1@', gedcomVersion)

            p1 == p2

            def pair1 = new LinkListener.Pair(p1, f1)
            def pair2 = new LinkListener.Pair(p2, f1)

            pair1 == pair2

    }

    private void inputLine(String label, int level, String tag, String pointer) {
        Line inputLine = Stub()
        inputLine.tag >> Tag.getInstance(tag)
        inputLine.level >> new Level(level)
        inputLine.label >> (label ? new Pointer(label, gedcomVersion) : null)
        inputLine.pointer >> (pointer ? new Pointer(pointer, gedcomVersion) : null)

        linkListener.handle(inputLine)
    }

    void setup() {
        linkListener = new LinkListener()
    }
}