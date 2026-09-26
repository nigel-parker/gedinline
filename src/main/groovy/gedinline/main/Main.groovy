package gedinline.main

class Main {

    static final String USAGE = 'Usage: java -jar gedinline.jar [--parallelism N] <file.ged>'

    static void main(String[] args) {

        def arguments = args as List<String>
        Integer parallelism = null

        while (arguments && arguments[0] in ['--parallelism', '-p']) {
            if (arguments.size() < 2) {
                println "Option ${arguments[0]} needs a value"
                println USAGE
                return
            }

            def value = arguments[1]
            parallelism = value.isInteger() ? value as Integer : null

            if (parallelism == null || parallelism < 1) {
                println "Invalid parallelism '${value}', expected a whole number of at least 1"
                println USAGE
                return
            }

            arguments = arguments.drop(2)
        }

        if (!arguments) {
            println 'You should specify a GEDCOM file to analyse'
            println USAGE
            return
        }

        def gedcomFile = new File(arguments[0])

        if (gedcomFile.exists()) {
            println ''
            def outputReportWriter = new PrintWriter(new OutputStreamWriter(System.out, 'UTF-8'))
            def validator = new GedInlineValidator(gedcomFile, outputReportWriter)

            if (parallelism != null) {
                validator.parallelism = parallelism
            }

            validator.validate()
        } else {
            println "Can't find file ${gedcomFile.name}"
        }
    }
}
