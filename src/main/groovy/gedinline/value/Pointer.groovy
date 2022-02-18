package gedinline.value

import gedinline.lexical.*
import gedinline.main.*
import groovy.transform.*
import org.apache.commons.lang.*

@CompileStatic
class Pointer extends Validator {

    Pointer() {
    }

    Pointer(String s, GedcomVersion gedcomVersion) throws GedcomException {
        this.s = s
        this.gedcomVersion = gedcomVersion
    }

    String getPointer() {
        s
    }

    boolean isVoid() {
        gedcomVersion?.is70() && s == '@VOID@'
    }

    static boolean looksValid(String s) {
        def s1 = StringUtils.trimToEmpty(s)
        !StringUtils.isBlank(s1) && s1.startsWith("@") && !s1.startsWith("@#")
    }

    boolean isValid() {
        isValid(s, gedcomVersion)
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        String regex = gedcomVersion.is70() ? "@[A-Z0-9_]+@" : "@[A-Za-z0-9][^@]*@"

        s.matches(regex)
    }

    String toString() {
        s
    }
}
