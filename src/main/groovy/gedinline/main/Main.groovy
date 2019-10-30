package gedinline.main

class Main {

    static void main(String[] args) {

        if (!args) {
            println 'You should specify a GEDCOM file to analyse'
            return
        }

        def gedcomFile = new File(args[0])

        if (gedcomFile.exists()) {
            println ''
            def outputReportWriter = new PrintWriter(new OutputStreamWriter(System.out, 'UTF-8'))
            new GedInlineValidator(gedcomFile, outputReportWriter).validate()
        } else {
            println "Can't find file ${gedcomFile.name}"
        }
    }
}
