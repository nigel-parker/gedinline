package gedinline.value

import spock.lang.*

import static gedinline.lexical.GedcomVersion.*

@Unroll
class UrlSpec extends Specification {

    void 'test URL validation for \'#input\''() {

        expect:

            new Url(input, V_70).isValid() == expectedResult

        where:

            input                                             | expectedResult

            'http://sottovoce.no'                             | true
            'HTTPS://SOTTOVOCE.NO'                            | true

            'www.sottovoce.no'                                | true
            'sottovoce.no/'                                   | true
            'sottovoce.no:80'                                 | true
            '97.46.132.8/'                                    | true
            'www.com'                                         | true
            'www.google.com/search?q=hello+world&test=no#xyz' | true
            'www.google.com/search?q=hello%2bworld%23brs'     | true
            'nigel@sottovoce.no'                              | true

            ''                                                | false
            'sottovoce'                                       | false
            'sottovoce.'                                      | false
            'sottovoce.nonExistentTLD'                        | false
            '@sottovoce.no'                                   | false
            ' sottovoce.no'                                   | false
            ' sottovoce.no '                                  | false
            'www.google.com/search?q=%xxpeople'               | false
    }
}
