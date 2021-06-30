package gedinline.util

class TestFileGenerator {

    String getVariant(int variant) {
        switch (variant) {
            case 1: return withBody('')
            case 2: return withHeaderBody('\n1 SCHMA\n2 TAG _SKYPEID http://xmlns.com/foaf/0.1/skypeID')
            case 3: return new File('src/test/resources/gedcom-files/harvey-70.ged').text
            case 4: return withBody(new File('src/test/resources/gedcom-files/stockdale-70.txt').text)
            case 5: return withBody(new File('src/test/resources/gedcom-files/allen-70.txt').text)
            case 6: return withBody('0 @I@ INDI\n1 BAPM Y\n')
            case 7: return withBody('0 @I@ INDI\n1 ADOP\n2 FAMC @F@\n3 ADOP\n')
            case 8: return withBody('0 @I@ INDI\n1 OBJE @VOID@\n2 CROP\n3 TOP 1\n')
            case 9: return withBody('0 @S@ SOUR\n1 REPO @VOID@\n2 CALN 1\n3 MEDI Book\n')
            case 10: return withHeaderBody('\n1 PLAC\n2 FORM a,b,c')
            case 11: return withBody('0 @I@ INDI\n1 RESN LOCKED, PRIVACY \n')
            case 12: return withBody('0 @S@ SOUR\n1 DATA\n2 EVEN BARM, BASM\n')
            case 13: return withBody('0 @I@ INDI\n1 NAME Napoleon\n2 TRAN Napoléon\n3 LANG fr\n')
            case 14: return withHeaderBody('\n1 NOTE note\n2 MIME font/ttf')
            case 15: return generate([patchNumber: '.1'])

            case 101: return withBody(new File('src/test/resources/gedcom-files/gordon-70.txt').text)
            case 102: return withBody('0 @A@ REPO\n1 NAME test\n1 ADDR\n')
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
