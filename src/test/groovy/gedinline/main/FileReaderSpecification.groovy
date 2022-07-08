package gedinline.main

import gedinline.lexical.*
import spock.lang.*

class FileReaderSpecification extends Specification {

    List<InputLinePrecursor> getInputLinePrecursors(String filename) {

        def parser = new BasicParser(getInputStream(filename))
        def result = []

        while (parser.hasNext()) {
            result << parser.next()
        }

        result
    }

    List<InputRecord> getInputRecords(String filename) {
        new RecordCollector(getInputStream(filename)).inputRecords
    }

    void verify(String filename, int expectedWarningCount, String expectedContent) {
        verify(filename, expectedWarningCount, expectedContent, true)
    }

    void verify(String filename, int expectedWarningCount, String expectedContent, boolean ok) {
        def inputStream = getInputStream(filename)

        assert inputStream

        def stringWriter = new StringWriter()

        def gedcomValidator = new GedInlineValidator(inputStream, filename, new PrintWriter(stringWriter))
        def okResult = gedcomValidator.validate()

        assert okResult == ok
        assert stringWriter.toString().contains(expectedContent)
        assert gedcomValidator.numberOfWarnings == expectedWarningCount
    }

    InputStream getInputStream(String filename) {
        def location = 'gedcom-files/' + filename
        getClass().classLoader.getResourceAsStream(location)
    }
}
