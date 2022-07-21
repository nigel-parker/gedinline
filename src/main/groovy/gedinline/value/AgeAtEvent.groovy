package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class AgeAtEvent extends Validator {

    private static Y = '\\d+y'
    private static M = '\\d+m'
    private static W = '\\d+w'
    private static D = '\\d+d'

    private static String regex70 = "(< |> |)(" +
            "$Y( $M)?( $W)?( $D)?|" +
            "$M( $W)?( $D)?|" +
            "$W( $D)?|" +
            "$D" +
            ")"

    private static String regex551 = "(<|>|)" +
            "(\\d{1,2}Y \\d{1,2}M \\d{1,3}D|" +
            "\\d{1,2}Y|" +
            "\\d{1,2}M|" +
            "\\d{1,3}D|" +
            "\\d{1,2}Y \\d{1,2}M|" +
            "\\d{1,2}Y \\d{1,3}D|" +
            "\\d{1,2}M \\d{1,3}D|" +
            "CHILD|" +
            "INFANT|" +
            "STILLBORN)"

    private static String regex555 = "(< |> |)" +
            "(\\d{1,3}Y \\d{1,2}M \\d{1,3}D|" +
            "\\d{1,3}Y|" +
            "\\d{1,2}M|" +
            "\\d{1,3}D|" +
            "\\d{1,3}Y \\d{1,2}M|" +
            "\\d{1,3}Y \\d{1,3}D|" +
            "\\d{1,3}M \\d{1,3}D|" +
            "CHILD|" +
            "INFANT|" +
            "STILLBORN)"

    AgeAtEvent() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        if (gedcomVersion.is70()) {
            isValid70(s)
        } else {
            String regex = gedcomVersion.is555() ? regex555 : regex551
            s.toUpperCase().matches(regex);
        }
    }

    boolean isValid70(String s) {
        if (s == '') {
            true
        } else {
            s.matches(regex70)
        }
    }
}
