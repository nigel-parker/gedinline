package gedinline.main

import com.google.common.collect.*
import gedinline.lexical.*
import gedinline.value.*
import groovy.transform.*

import java.beans.*

@CompileStatic
class LinkListener implements PropertyChangeListener {

    private static Set<String> tags = ['INDI', 'FAMC', 'FAMS', 'FAM', 'CHIL', 'HUSB', 'WIFE'] as Set

    WarningCollector warningCollector

    Set<Pair> chil = []
    Set<Pair> famc = []
    Set<Pair> parent = []
    Set<Pair> fams = []

    Pointer currentIndi
    Pointer currentFam

    LinkListener(WarningCollector warningCollector) {
        this.warningCollector = warningCollector
    }

    void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        InputLine inputLine = (InputLine) propertyChangeEvent.getNewValue()
        handle(inputLine)
    }

    void handle(Line inputLine) {
        def label = inputLine.getLabel()
        def level = inputLine.getLevel().level
        def tag = inputLine.tag.tag
        def pointer = inputLine.getPointer()

        if (tag in tags) {

            switch (tag) {
                case { it == 'INDI' && level == 0 }:
                    currentIndi = label
                    break

                case { it == 'FAMC' && level == 1 }:
                    famc << new Pair(currentIndi, pointer)
                    break

                case { it == 'FAMS' && level == 1 }:
                    fams << new Pair(currentIndi, pointer)
                    break

                case { it == 'FAM' && level == 0 }:
                    currentFam = label
                    break

                case { it == 'CHIL' && level == 1 }:
                    chil << new Pair(pointer, currentFam)
                    break

                case { it in ['HUSB', 'WIFE'] && level == 1 }:
                    parent << new Pair(pointer, currentFam)
                    break
            }
        }
    }

    void reportMissingLinks() {

        missingFams.each { Pair pair ->
            warningCollector.warning("Missing FAMS tag for individual ${pair.individual}")
        }

        missingFamc.each { Pair pair ->
            warningCollector.warning("Missing FAMC tag for individual ${pair.individual}")
        }
    }

    int getWarningCount() {
        // for test purposes
        missingFamc.size() + missingFams.size()
    }

    Set<Pair> getMissingFamc() {
        Sets.difference(chil, famc)
    }

    Set<Pair> getMissingFams() {
        Sets.difference(parent, fams)
    }

    String toString() {
        System.identityHashCode(this)
    }

    @EqualsAndHashCode
    static class Pair {
        Pointer individual
        Pointer family

        Pair(Pointer individual, Pointer family) {
            this.individual = individual
            this.family = family
        }

        String toString() {
            "($individual, $family)"
        }
    }
}
