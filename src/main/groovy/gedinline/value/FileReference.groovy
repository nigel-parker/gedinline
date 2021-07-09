package gedinline.value

import gedinline.lexical.*
import groovy.transform.*
import org.apache.commons.validator.routines.*

@CompileStatic
class FileReference extends Validator {

    FileReference() {
    }

    boolean isValid(String s, GedcomVersion gedcomVersion) {

        def schemes = ['http', 'https', 'ftp', 'file'] as String[]
        def urlValidator = new UrlValidator(schemes)
        def decoded = URLDecoder.decode('file:/' + s, 'UTF-8')

        if (urlValidator.isValid(s)) {
            true
        } else {
            urlValidator.isValid(decoded) && !decoded.contains('..')
        }
    }
}
