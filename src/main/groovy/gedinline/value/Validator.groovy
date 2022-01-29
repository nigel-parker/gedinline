package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
@EqualsAndHashCode(includes = 's, gedcomVersion')
class Validator {

    String s
    GedcomVersion gedcomVersion

    boolean isValid() {
        false
    }

    boolean isValid(String s, GedcomVersion gedcomVersion1) {
        false
    }

    ValidationResult validate(String s, GedcomVersion gedcomVersion1) {
        ValidationResult.of(isValid(s, gedcomVersion1))
    }

    private static Map<String, Validator> validators = [:]

    static Validator of(String shortName) {

        if (!validators) {
            init()
        }

        validators[shortName]
    }

    private static List<String> names = [
            'AgeAtEvent',
            'DateExact',
            'DatePeriod',
            'Email',
            'ExtensionTag',
            'FileReference',
            'Language',
            'Latitude',
            'Longitude',
            'MediaType',
            'NonEmptyString',
            'Pointer',
            'SemanticVersionNumber',
            'Text',
            'Time',
            'Uri',
            'Url',
            'Uuid',
    ]

    private static void init() {

        names.each { String name ->
            def validator = Class.forName('gedinline.value.' + name).newInstance() as Validator
            validators.put(name, validator)
        }
    }
}
