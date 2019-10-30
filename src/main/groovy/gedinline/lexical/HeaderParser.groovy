package gedinline.lexical

/**
 * Implements the requirements of the 5.5.5 specification which states that the file must begin with an
 * invariant header.
 *
 * It is currently unclear whether the elements must always appear in the stated order. This version assumes
 * that the required order is mandatory.
 */
class HeaderParser {

    private static GEDCOM_HEADER_PREFIX = '0 HEAD1 GEDC2 VERS 5.5.52 FORM LINEAGE-LINKED3 VERS 5.5.51 CHAR '
    private static VARIANT_1 = GEDCOM_HEADER_PREFIX + 'UTF-8'
    private static VARIANT_2 = GEDCOM_HEADER_PREFIX + 'UNICODE'

    int lineCount = 6
    def lines = ''

    void addLine(String line) {
        lines += line
    }

    boolean isValidGedcom555Header() {
        lines in [VARIANT_1, VARIANT_2]
    }
}
