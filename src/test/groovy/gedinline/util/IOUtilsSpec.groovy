package gedinline.util

import spock.lang.*

import java.nio.charset.*

class IOUtilsSpec extends Specification {

    void 'readLines splits on every line terminator and reads UTF-8'() {

        expect:

            IOUtils.readLines(new ByteArrayInputStream('a\nb\r\nc\rø\n\nd'.getBytes(StandardCharsets.UTF_8))) == ['a', 'b', 'c', 'ø', '', 'd']
    }

    void 'lineIterator returns every line, and hasNext does not consume'() {

        given:

            LineIterator iterator = IOUtils.lineIterator(new StringReader('a\r\n\nb'))

        expect:

            iterator.hasNext()
            iterator.hasNext()
            iterator.nextLine() == 'a'
            iterator.nextLine() == ''
            iterator.nextLine() == 'b'
            !iterator.hasNext()
    }

    void 'lineIterator leaves the reader open at end of input'() {

        given:

            def closed = false
            def reader = new StringReader('a') {
                void close() {
                    closed = true
                    super.close()
                }
            }
            LineIterator iterator = IOUtils.lineIterator(reader)

        when:

            iterator.nextLine()

        then:

            !closed

        when:

            def hasNext = iterator.hasNext()

        then:

            !hasNext
            !closed
    }

    void 'nextLine past the end throws'() {

        given:

            LineIterator iterator = IOUtils.lineIterator(new StringReader(''))

        when:

            iterator.nextLine()

        then:

            thrown(NoSuchElementException)
    }

    void 'lineIterator on a stream decodes with the given encoding and leaves the stream open'() {

        given:

            def closed = false
            def inputStream = new ByteArrayInputStream('0 HEAD\n1 CHAR UNICODE'.getBytes('UTF-16')) {
                void close() {
                    closed = true
                }
            }
            LineIterator iterator = IOUtils.lineIterator(inputStream, 'UTF-16')

        expect:

            iterator.nextLine() == '0 HEAD'
            iterator.nextLine() == '1 CHAR UNICODE'
            !iterator.hasNext()
            !closed
    }

    void 'close closes the reader'() {

        given:

            def closed = false
            def reader = new StringReader('a\nb') {
                void close() {
                    closed = true
                    super.close()
                }
            }
            LineIterator iterator = IOUtils.lineIterator(reader)

        when:

            iterator.close()

        then:

            closed
            !iterator.hasNext()
    }
}
