package gedinline.lexical

import com.google.common.collect.*
import gedinline.main.*
import gedinline.value.*
import groovy.transform.*

@CompileStatic
class XrefStore {

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
        Sets.difference(pointers, labels)
    }
}
