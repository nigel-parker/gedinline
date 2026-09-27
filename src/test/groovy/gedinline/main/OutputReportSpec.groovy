package gedinline.main

import spock.lang.*

import static gedinline.main.OutputReport.*

class OutputReportSpec extends Specification {

    void 'warnings are printed by line number, sorted within a line and without duplicates'() {

        given:

            def stringWriter = new StringWriter()
            def outputReport = new OutputReport(new PrintWriter(stringWriter))
            outputReport.reportValue(WARNINGS_PER_10000_LINES, '0')
            outputReport.reportValue(LINES_PER_SECOND, '0')

        when:

            outputReport.outputWarning(30, 'w30')
            outputReport.outputWarning(10, 'w10-b')
            outputReport.outputWarning(20, 'w20')
            outputReport.outputWarning(10, 'w10-a')
            outputReport.outputWarning(20, 'w20')
            outputReport.printReport()

        then:

            stringWriter.toString().readLines().findAll { it.startsWith('w') } == ['w10-a', 'w10-b', 'w20', 'w30']
    }
}
