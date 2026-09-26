package gedinline.main

import spock.lang.*

class MainSpec extends Specification {

    static final String HARVEY = 'src/test/resources/gedcom-files/harvey.ged'

    void 'a file is validated with the default parallelism'() {

        when:

            def output = run(HARVEY)

        then:

            output.contains('Records                    45')
            output.contains('Report generated on')
    }

    void 'the parallelism can be given as #flag'() {

        when:

            def output = run(flag, '2', HARVEY)

        then:

            output.contains('Records                    45')
            output.contains('Report generated on')

        where:

            flag << ['--parallelism', '-p']
    }

    void 'an invalid parallelism is reported: #arguments'() {

        when:

            def output = run(*arguments)

        then:

            output.contains(expected)
            !output.contains('Report generated on')

        where:

            arguments                            || expected
            ['--parallelism', 'two', HARVEY]     || "Invalid parallelism 'two'"
            ['--parallelism', '0', HARVEY]       || "Invalid parallelism '0'"
            ['--parallelism']                    || 'Option --parallelism needs a value'
            []                                   || 'You should specify a GEDCOM file to analyse'
            ['no-such-file.ged']                 || "Can't find file no-such-file.ged"
    }

    private static String run(String... arguments) {
        def original = System.out
        def buffer = new ByteArrayOutputStream()
        System.out = new PrintStream(buffer, true, 'UTF-8')

        try {
            Main.main(arguments)
        } finally {
            System.out = original
        }

        buffer.toString('UTF-8')
    }
}
