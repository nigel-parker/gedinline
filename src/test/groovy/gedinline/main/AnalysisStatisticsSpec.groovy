package gedinline.main

import spock.lang.*

class AnalysisStatisticsSpec extends Specification {

    void 'test constructor accepts warnings field'() {

        given:

        def stats = new AnalysisStatistics(
            new Date(),
            'test.ged',
            '5.5.1',
            'TestApp',
            '1.0',
            '2025-01-01',
            1000,
            500,
            5,
            '1.5s',
            '666 lines/s',
            42,
            666
        )

        expect:

        stats.warnings == 42
    }

    void 'test constructor accepts linesPerSecond field'() {

        given:

        def stats = new AnalysisStatistics(
            new Date(),
            'test.ged',
            '5.5.1',
            'TestApp',
            '1.0',
            '2025-01-01',
            1000,
            500,
            5,
            '1.5s',
            '666 lines/s',
            42,
            666
        )

        expect:

        stats.linesPerSecond == 666
    }

    void 'test both new fields work together'() {

        given:

        def stats = new AnalysisStatistics(
            new Date(),
            'sample.ged',
            '7.0',
            'MyApp',
            '2.0',
            '2025-10-12',
            5000,
            2500,
            10,
            '5.0s',
            '1000 lines/s',
            100,
            1000
        )

        expect:

        stats.warnings == 100
        stats.linesPerSecond == 1000
    }
}
