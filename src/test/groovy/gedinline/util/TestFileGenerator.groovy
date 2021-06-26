package gedinline.util

class TestFileGenerator {

    String getVariant(int variant) {
        switch (variant) {
            case 1: return withBody('')
            case 2: return withHeaderBody('\n1 SCHMA\n2 TAG _SKYPEID http://xmlns.com/foaf/0.1/skypeID')
            case 3: return withBody(new File('src/test/resources/gedcom-files/gordon-70.txt').text)
            case 4: return new File('src/test/resources/gedcom-files/harvey-70.ged').text
            case 5: return withBody('') // stack overflow
        }
    }

    String withHeaderBody(String body) {
        generate([headerBody: body])
    }

    String withBody(String body) {
        generate([body: body])
    }

    private String generate(Map map) {
        def initial = '0 HEAD\n1 GEDC\n2 VERS 7.0{patchNumber}{headerBody}\n{body}0 TRLR\n'
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
