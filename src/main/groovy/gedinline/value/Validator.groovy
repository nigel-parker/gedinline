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

    private static final List<String> names = [
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

    // Built once, eagerly, so that concurrent callers never observe a partly filled map
    private static final Map<String, Validator> validators = createValidators()

    static Validator of(String shortName) {
        validators[shortName]
    }

    private static Map<String, Validator> createValidators() {
        Map<String, Validator> map = [:]

        names.each { String name ->
            map.put(name, Class.forName('gedinline.value.' + name).newInstance() as Validator)
        }

        Collections.unmodifiableMap(map)
    }
}
