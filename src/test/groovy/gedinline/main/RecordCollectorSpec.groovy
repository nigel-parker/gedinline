package gedinline.main

class RecordCollectorSpec extends FileReaderSpecification {

    void test() {

        when:

            def head = getInputRecords('head.ged')

        then:

            head.size() == 2

            def header = head.first()
            header.inputLine.tag.toString() == 'HEAD'

            def headerInputRecords = header.inputRecords
            headerInputRecords.size() == 6

            def last = headerInputRecords.last()
            last.inputLine.tag.toString() == 'GEDC'
            last.inputRecords.size() == 2

        when:

            def simple = getInputRecords('simple.ged')

        then:

            simple.size() == 6
    }
}
