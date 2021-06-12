package gedinline.util

class TestFileGenerator {

    String generate(Map map) {
        def initial = '0 HEAD\n1 GEDC\n2 VERS 7.0{patchNumber}\n{body}0 TRLR\n'
        def template = new VsTemplate(initial)

        template.freeVariables.each { String variable ->
            def inputValue = map[variable]

            if (inputValue) {
                def finalValue = inputValue instanceof String ? inputValue : inputValue.join('\n') + '\n' as String
                template = template.bind(variable, finalValue)
            }
        }

        template.bindAll('').toString()
    }
}
