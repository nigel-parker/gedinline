package gedinline.main;

import gedinline.lexical.InputLine;

import java.util.Observable;
import java.util.Observer;

public class GedcomListener implements Observer {

    private boolean headerSeen = false;
    private boolean submSeen = false;

    public void update(Observable o, Object arg) {

        InputLine inputLine = (InputLine) arg;
        String tag = inputLine.getTag().getTag();
        boolean gedcom555 = inputLine.getGedcomVersion().is555();
        int level = inputLine.getLevel().getLevel();

        if (gedcom555 && level == 0 && !tag.equals("HEAD")) {

            if (!headerSeen) {
                headerSeen = true;

                if (tag.equals("SUBM")) {
                    submSeen = true;
                } else {
                    throw new GedcomException("Mandatory <SUBMITTER_RECORD> is missing, should be placed directly after the header");
                }
            } else {
                if (tag.equals("SUBM")) {
                    if (submSeen) {
                        throw new GedcomException("Multiple <SUBMITTER_RECORD>s are not allowed");
                    }
                }
            }
        }
    }
}
