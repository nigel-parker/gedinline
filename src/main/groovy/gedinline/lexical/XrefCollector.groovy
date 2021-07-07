package gedinline.lexical

import gedinline.main.*
import gedinline.value.*
import groovy.transform.*

@CompileStatic
class XrefCollector {

    Set<Pointer> labels = []
    Set<Pointer> pointers = []

    void addLabel(Pointer label) {

        if (label.isVoid()) {
            throw new GedcomException("Invalid label " + label);
        }

        if (label in labels) {
            throw new GedcomException("Duplicate occurrence of label " + label);
        }

        labels << label
    }

    void addPointer(Pointer pointer) {
        if (!pointer.isVoid()) {
            pointers << pointer
        }
    }

    Set<Pointer> getUnsatisfiedPointers() {
        pointers - labels
    }
}
