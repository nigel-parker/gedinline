package gedinline.value

import gedinline.lexical.*
import gedinline.main.*
import groovy.transform.*

@CompileStatic
class Uri extends Validator {

    Uri() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def scheme = /(?<scheme>\w+)/
        def host = /(?<host>[^\/]*)/
        def authority = "(?<authority>$host)"
        def path = /(?<path>[^\?#]*)/
        def query = /(?<query>.*)/
        def fragment = /(?<fragment>.*)/

        def regex = "$scheme:(//$authority)?$path(\\?$query)?(#$fragment)?"

        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        def result = [
                scheme   : matcher.group('scheme'),
                host     : matcher.group('host'),
                authority: matcher.group('authority'),
                path     : matcher.group('path'),
                query    : matcher.group('query'),
                fragment : matcher.group('fragment')
        ]

        if (Debug.active()) {
            println "--- URI parse result = ${result}"
        }

        true
    }
}
