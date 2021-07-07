package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@EqualsAndHashCode(includes = 's, gedcomVersion')
class Validator {

    String s
    GedcomVersion gedcomVersion

    boolean isValid() {
        false
    }
}
