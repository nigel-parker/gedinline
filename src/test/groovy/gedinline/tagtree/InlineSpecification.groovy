package gedinline.tagtree

import gedinline.main.*
import spock.lang.*

import static java.nio.charset.StandardCharsets.*

class InlineSpecification extends Specification {

    static def SHORT_HEADER = '''0 HEAD
1 GEDC
2 VERS 5.5.5
2 FORM LINEAGE-LINKED
3 VERS 5.5.5
1 CHAR UTF-8
1 SOUR Nigel
'''
    static def LONG_HEADER = SHORT_HEADER + '0 @U1@ SUBM\n1 NAME Nigel\n'

    String getResultShort(String insert, int errorCount, boolean trace = false) {
        getResult(SHORT_HEADER, insert, errorCount, trace)
    }

    String getResult(String insert, int errorCount, boolean trace = false) {
        getResult(LONG_HEADER, insert, errorCount, trace)
    }

    String getResult(String header, String insert, int errorCount, boolean trace = false) {

        def trailer = '\n0 TRLR\n'

        def input = header + insert + trailer

        def inputStream = new ByteArrayInputStream(input.getBytes(UTF_8))
        def stringWriter = new StringWriter()

        new GedInlineValidator(inputStream, 'Inline', new PrintWriter(stringWriter)).validate()
        def result = stringWriter.toString()

        if (trace) {
            def builder = new StringBuilder()

            input.split('\n').eachWithIndex { String entry, int i ->
                builder << "${i + 1} $entry\n"
            }

            println "\n\n$builder \n$result\n"
        }

        if (errorCount >= 0) {
            assert result.contains("$errorCount  Total number of warning messages")
        }

        result
    }
}
