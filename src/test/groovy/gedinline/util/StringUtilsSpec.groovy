package gedinline.util

import spock.lang.*

class StringUtilsSpec extends Specification {

    void 'isBlank'() {

        expect:

            StringUtils.isBlank(s) == result

        where:

            s      || result

            null   || true
            ''     || true
            ' \t ' || true
            ' a '  || false
    }

    void 'trimToEmpty'() {

        expect:

            StringUtils.trimToEmpty(s) == result

        where:

            s       || result

            null    || ''
            ''      || ''
            ' a b ' || 'a b'
    }

    void 'substringAfter'() {

        expect:

            StringUtils.substringAfter(s, separator) == result

        where:

            s       | separator || result

            null    | 'a'       || null
            ''      | 'a'       || ''
            'abc'   | null      || ''
            'abc'   | ''        || 'abc'
            'abcba' | 'b'       || 'cba'
            'abc'   | 'c'       || ''
            'abc'   | 'd'       || ''
    }

    void 'substringBefore'() {

        expect:

            StringUtils.substringBefore(s, separator) == result

        where:

            s       | separator || result

            null    | 'a'       || null
            ''      | 'a'       || ''
            'abc'   | null      || 'abc'
            'abc'   | ''        || ''
            'abcba' | 'b'       || 'a'
            'abc'   | 'a'       || ''
            'abc'   | 'd'       || 'abc'
    }

    void 'substringBetween'() {

        expect:

            StringUtils.substringBetween(s, open, close) == result

        where:

            s           | open | close || result

            null        | '='  | ':'   || null
            'a=b:c'     | null | ':'   || null
            'a=b:c'     | '='  | null  || null
            'a=b:c'     | '='  | ':'   || 'b'
            'a=:c'      | '='  | ':'   || ''
            'a:b=c:d'   | '='  | ':'   || 'c'
            'a=b'       | '='  | ':'   || null
            'abc'       | '='  | ':'   || null
            'FROM x TO' | 'FROM ' | ' TO' || 'x'
    }

    void 'rightPad and leftPad'() {

        expect:

            StringUtils.rightPad(s, size) == right
            StringUtils.leftPad(s, size) == left

        where:

            s     | size || right   | left

            null  | 3    || null    | null
            ''    | 3    || '   '   | '   '
            'ab'  | 4    || 'ab  '  | '  ab'
            'abc' | 2    || 'abc'   | 'abc'
    }

    void 'abbreviate'() {

        expect:

            StringUtils.abbreviate(s, maxWidth) == result

        where:

            s         | maxWidth || result

            null      | 4        || null
            ''        | 4        || ''
            'abcd'    | 4        || 'abcd'
            'abcdefg' | 6        || 'abc...'
    }

    void 'indexOfAny and indexOfAnyBut'() {

        expect:

            StringUtils.indexOfAny(s, chars) == any
            StringUtils.indexOfAnyBut(s, chars) == anyBut

        where:

            s       | chars || any | anyBut

            null    | '01'  || -1  | -1
            ''      | '01'  || -1  | -1
            '12'    | ''    || -1  | -1
            '12 a'  | '0123456789' || 0  | 2
            'a12'   | '0123456789' || 1  | 0
            '123'   | '0123456789' || 0  | -1
            'abc'   | '0123456789' || -1 | 0
    }

    void 'indexOf'() {

        expect:

            StringUtils.indexOf(s, search) == result

        where:

            s     | search || result

            null  | 'a'    || -1
            'abc' | null   || -1
            'abc' | 'c'    || 2
            'abc' | 'd'    || -1
    }

    void 'isNumeric'() {

        expect:

            StringUtils.isNumeric(s) == result

        where:

            s     || result

            null  || false
            ''    || true
            '123' || true
            '12a' || false
            ' 12' || false
    }

    void 'split'() {

        expect:

            StringUtils.split(s, ' ' as char) == result

        where:

            s          || result

            null       || null
            ''         || [] as String[]
            '   '      || [] as String[]
            ' a  b c ' || ['a', 'b', 'c'] as String[]
    }

    void 'repeat'() {

        expect:

            StringUtils.repeat(s, n) == result

        where:

            s    | n  || result

            null | 2  || null
            ' '  | 0  || ''
            ' '  | -1 || ''
            'ab' | 3  || 'ababab'
    }

    void 'remove'() {

        expect:

            StringUtils.remove(s, '.' as char) == result

        where:

            s       || result

            null    || null
            ''      || ''
            '5.5.1' || '551'
            '70'    || '70'
    }
}
