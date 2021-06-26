package gedinline.util

class TestFileGenerator {

    String getValidVariant(int variant) {
        switch (variant) {
            case 1: return generate([:])
            case 2: return generate([headerBody: '\n1 SCHMA\n2 TAG _SKYPEID http://xmlns.com/foaf/0.1/skypeID'])
        }
    }

    String withBody(String body) {
        generate([body: body])
    }

    String getErrorVariant(int variant) {
        // %%% midlertidig
        new File('/Users/nigel/Filer/slektsforskning/gedinline/src/test/resources/gedcom-files/harvey-70.ged').text
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
