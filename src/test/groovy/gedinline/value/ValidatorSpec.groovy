package gedinline.value

import spock.lang.*

import java.util.concurrent.*

class ValidatorSpec extends Specification {

    void 'validators are found by short name'() {

        expect:

            Validator.of('Uuid') instanceof Uuid
            Validator.of('Language') instanceof Language
            Validator.of('NoSuchValidator') == null
    }

    void 'concurrent lookups all see the same fully initialised validators'() {

        given:

            def pool = Executors.newFixedThreadPool(8)
            def names = ['AgeAtEvent', 'DateExact', 'Email', 'Language', 'Pointer', 'Time', 'Uri', 'Uuid']

        when:

            def futures = (1..64).collect { int i ->
                pool.submit({ names.collect { Validator.of(it) } } as Callable)
            }
            def results = futures*.get()
            pool.shutdown()

        then:

            results.every { List list -> list.every { it != null } }
            results.every { List list -> list == results[0] }
            results.every { List list -> list.eachWithIndex { v, i -> assert v.is(results[0][i]) } }
    }

    void 'language codes are loaded once and are immutable'() {

        expect:

            'en' in Language.languageCodes
            !('xx-not-a-code' in Language.languageCodes)

        when:

            Language.languageCodes.add('zz')

        then:

            thrown(UnsupportedOperationException)
    }
}
