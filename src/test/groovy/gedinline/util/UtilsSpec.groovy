package gedinline.util

import spock.lang.*

class UtilsSpec extends Specification {

    void 'test GEDCOM list splitting rules'() {

        expect:

            Utils.splitGedcomList(input).size() == numberOfValues

        where:

            input            || numberOfValues

            '1,2'            || 2
            ', , one, more,' || 5
    }
}
