package gedinline.lexical;

import gedinline.main.GedcomException;

public class Level {

    private int level;

    public Level() throws GedcomException {
        level = -1;
    }

    public Level(int i) throws GedcomException {
        level = i;
    }

    public int getLevel() {
        return level;
    }

    public static int getDifference(Level previous, Level present) {
        return previous.getLevel() - present.getLevel();
    }

    public String toString() {
        return level == -1 ? "" : "" + level;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Level level1 = (Level) o;
        return level == level1.level;
    }

    public int hashCode() {
        return level;
    }
}
