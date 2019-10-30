package gedinline.lexical

import spock.lang.*

class LevelSpec extends Specification {

    void test() {

        expect:

            new Level(1) == new Level(1)
            new Level(1) != new Level(2)

            Level.getDifference(new Level(1), new Level(0)) == 1
            Level.getDifference(new Level(0), new Level(1)) == -1

            new Level(1).level == 1
            new Level().level == -1

            new Level(1).toString() == '1'
            new Level().toString() == ''
    }
}
