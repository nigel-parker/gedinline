package gedinline.lexical

import gedinline.tagtree.*
import gedinline.value.*
import groovy.transform.*

@CompileStatic
class XrefCollector {

    Map<XrefType, XrefStore> map = [:]

    XrefCollector() {
        XrefType.values().each { XrefType xrefType ->
            map.put(xrefType, new XrefStore())
        }
    }

    void addLabel(Pointer label, Tag tag) {
        getXrefStore(tag).addLabel(label)
    }

    void addPointer(Pointer pointer, Tag tag) {
        getXrefStore(tag).addPointer(pointer)
    }

    Set<Pointer> getUnsatisfiedPointers() {
        def set = [] as Set

        XrefType.values().each { XrefType xrefType ->
            set.addAll(map[xrefType].getUnsatisfiedPointers())
        }

        set
    }

    private XrefStore getXrefStore(Tag tag) {
        map[tag.xrefType]
    }
}
