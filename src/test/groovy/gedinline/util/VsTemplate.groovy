package gedinline.util

class VsTemplate {

    static def freeVariableRegex = /\{.*?\}/

    String s

    VsTemplate(String s) {
        this.s = s
    }

    VsTemplate bind(String variable, String value) {
        def variableRegex = "\\{$variable\\}" as String
        new VsTemplate(s.replaceAll(variableRegex, value))
    }

    VsTemplate bind(Map<String, String> bindings) {
        def resultat = this

        bindings.each { String k, String v ->
            resultat = resultat.bind(k, v)
        }

        resultat
    }

    VsTemplate bindAll(String value) {
        def resultat = this

        freeVariables.each { String variable ->
            resultat = resultat.bind(variable, value)
        }

        resultat
    }


    List<String> getFreeVariables() {
        def result = [] as TreeSet
        def matcher = s =~ freeVariableRegex

        if (matcher) {
            matcher.findAll() { String positionMarker ->
                def variableName = positionMarker.substring(1, positionMarker.size() - 1)

                result << variableName
            }
        }

        result as List
    }

    int getNumberOfFreeVariables() {
        freeVariables.size()
    }

    String toString() {
        s
    }
}
