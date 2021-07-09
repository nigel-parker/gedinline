package gedinline.value

import gedinline.lexical.*
import groovy.transform.*

@CompileStatic
class AgeAtEvent extends Validator{

    AgeAtEvent() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {
        String regex551 = "(<|>|)" +
                "(\\d{1,2}Y \\d{1,2}M \\d{1,3}D|" +
                "\\d{1,2}Y|" +
                "\\d{1,2}M|" +
                "\\d{1,3}D|" +
                "\\d{1,2}Y \\d{1,2}M|" +
                "\\d{1,2}Y \\d{1,3}D|" +
                "\\d{1,2}M \\d{1,3}D|" +
                "CHILD|" +
                "INFANT|" +
                "STILLBORN)";

        String regex555 = "(< |> |)" +
                "(\\d{1,3}Y \\d{1,2}M \\d{1,3}D|" +
                "\\d{1,3}Y|" +
                "\\d{1,2}M|" +
                "\\d{1,3}D|" +
                "\\d{1,3}Y \\d{1,2}M|" +
                "\\d{1,3}Y \\d{1,3}D|" +
                "\\d{1,3}M \\d{1,3}D|" +
                "CHILD|" +
                "INFANT|" +
                "STILLBORN)";

        String regex = gedcomVersion.is555() ? regex555 : regex551;
        s.toUpperCase().matches(regex);
    }
}
