package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class Uri extends Validator {

    Uri() {
    }

    Uri(String s, GedcomVersion gedcomVersion) {
        this.s = s;
        this.gedcomVersion = gedcomVersion;
    }

    boolean isValid() {

        def scheme = /(?<scheme>\w+)/
        def host = /(?<host>[^\/]*)/
        def authority = "(?<authority>$host)"
        def path = /(?<path>[^\?#]*)/
        def query = /(?<query>.*)/
        def fragment = /(?<fragment>.*)/

        def regex = "$scheme:(//$authority)?$path(\\?$query)?(#$fragment)?"
//        println "%%% regex = ${regex}"

        def matcher = s =~ regex

        if (!matcher.matches()) {
            return false
        }

        def result = [
                scheme: matcher.group('scheme'),
                host: matcher.group('host'),
                authority: matcher.group('authority'),
                path: matcher.group('path'),
                query: matcher.group('query'),
                fragment: matcher.group('fragment')
        ]

//        println "%%% result = ${result}"

        true
    }
}
