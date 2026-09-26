package gedinline.main

import spock.lang.*

/**
 * The parallel validator must produce exactly the same report as the serial one, for every test file.
 */
@Unroll
class ParallelValidationSpec extends Specification {

    static final File FILES = new File('src/test/resources/gedcom-files')

    void 'parallelism #parallelism gives the same report as serial validation for #file.name'() {

        when:

            def serial = validate(file, 1)
            def parallel = validate(file, parallelism)

        then:

            parallel.ok == serial.ok
            parallel.warnings == serial.warnings
            parallel.report == serial.report
            parallel.statistics == serial.statistics

        where:

            [file, parallelism] << [gedcomFiles(), [4]].combinations() + [bigFiles(), [3, 8]].combinations()
    }

    void 'parallelism must be at least 1'() {

        when:

            new GedInlineValidator(new ByteArrayInputStream(new byte[0]), 'x', new PrintWriter(new StringWriter())).parallelism = 0

        then:

            thrown(IllegalArgumentException)
    }

    static List<File> gedcomFiles() {
        FILES.listFiles().findAll { it.name.endsWith('.ged') }.sort()
    }

    static List<File> bigFiles() {
        ['w11.ged', 'olson-555.ged', 'torture-test-5-5.ged', 'torture-test-5-5-1.ged', 'fs-maximal70.ged'].collect { new File(FILES, it) }
    }

    static Map validate(File file, int parallelism) {
        def stringWriter = new StringWriter()
        def validator = new GedInlineValidator(new FileInputStream(file), file.name, new PrintWriter(stringWriter))
        validator.parallelism = parallelism
        def ok = validator.validate()
        def statistics = validator.analysisStatistics

        [
                ok        : ok,
                warnings  : validator.numberOfWarnings,
                report    : stripVolatileLines(stringWriter.toString()),
                statistics: statistics == null ? null : statistics.properties.findAll { k, v -> !(k in ['class', 'analysisTime', 'speed', 'linesPerSecond', 'timestamp']) },
        ]
    }

    static String stripVolatileLines(String report) {
        report.readLines().findAll { line ->
            !(line =~ /^(Analysis time|Speed|Lines per second|Report generated on)/)
        }.join('\n')
    }
}
