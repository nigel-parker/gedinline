package gedinline.main

import gedinline.lexical.*
import gedinline.tagtree.*
import gedinline.value.*
import spock.lang.*

import static gedinline.main.RecordResult.*

class RecordValidatorSpec extends Specification {

    void 'the result lists one LineValidated event per validated line, carrying that line\'s own warnings'() {

        given:

            def gedcom = '''0 HEAD
1 GEDC
2 VERS 5.5.1
0 @I1@ INDI
1 SEX Q
1 BIRT
2 DATE 1 JAN 2000
0 TRLR
'''
            def records = new RecordCollector(new ByteArrayInputStream(gedcom.bytes)).inputRecords
            def indi = records.find { it.inputLine.tag.tag == 'INDI' }
            def version = GedcomVersion.V_551
            def tagTree = new TagTreeGrammar(version).expand('INDIVIDUAL_RECORD')

        when:

            def result = new RecordValidator(new ValueGrammar(version), version).validate(indi, tagTree)
            def lineEvents = result.events.findAll { it instanceof LineValidated } as List<LineValidated>

        then:

            result.events[0] instanceof LineValidated
            result.events[0].inputLine.lineNumber == 4
            lineEvents*.inputLine*.lineNumber == [4, 5, 6, 7]
            lineEvents.find { it.inputLine.lineNumber == 5 }.warnings*.message == ["Invalid content for SEX tag: 'Q' is not a valid <SEX_VALUE>"]
            lineEvents.findAll { it.inputLine.lineNumber != 5 }.every { it.warnings.isEmpty() }
            result.events.findAll { it instanceof Warning }.isEmpty()
    }

    void 'record level warnings are separate events in the order they arise'() {

        given:

            def gedcom = '''0 HEAD
1 GEDC
2 VERS 5.5.1
0 @F1@ FAM
1 HUSB @I1@
1 HUSB @I2@
1 FOO bar
0 TRLR
'''
            def records = new RecordCollector(new ByteArrayInputStream(gedcom.bytes)).inputRecords
            def fam = records.find { it.inputLine.tag.tag == 'FAM' }
            def version = GedcomVersion.V_551
            def tagTree = new TagTreeGrammar(version).expand('FAM_RECORD')

        when:

            def result = new RecordValidator(new ValueGrammar(version), version).validate(fam, tagTree)
            def warnings = result.events.findAll { it instanceof Warning } as List<Warning>

        then:

            warnings*.lineNumber == [4, 7]
            warnings*.message == ['Tag HUSB occurs more than once', 'Tag FOO is not allowed under FAM']
    }
}
