package gedinline.value

import gedinline.lexical.*
import groovy.transform.*
import org.apache.commons.validator.routines.*

@CompileStatic
class FileReference extends Validator {

    FileReference() {
    }

    boolean isValid(String inputString, GedcomVersion gedcomVersion) {

        def withAddedFileScheme = 'file:/' + inputString
        def decoded = URLDecoder.decode(inputString, 'UTF-8')

        def webSchemes = ['http', 'https', 'ftp'] as String[]
        def fileScheme = ['file'] as String[]

        def webUrlValidator = new UrlValidator(webSchemes)
        def fileValidator = new UrlValidator(fileScheme)

        webUrlValidator.isValid(inputString) ||
                fileValidator.isValid(inputString) ||
                (fileValidator.isValid(withAddedFileScheme) &&
                        !inputString.contains('#') &&
                        !inputString.contains('?') &&
                        !decoded.contains('..') &&
                        !decoded.contains('\\'))
    }
}
