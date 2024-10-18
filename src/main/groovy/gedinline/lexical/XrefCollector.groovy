package gedinline.lexical

import gedinline.tagtree.*
import gedinline.value.*
import groovy.transform.*

@CompileStatic
class XrefCollector {

    Map<String, XrefStore> map = [:]

    XrefCollector() {
        XrefType.values().each { XrefType xrefType ->
            map.put(xrefType.toString(), new XrefStore())
        }
    }

    void addLabel(Pointer label, Tag tag) {
        getXrefStore(tag).addLabel(label)
    }

    void addPointer(Pointer pointer, Tag tag) {
        if (pointer.isValid() && !tag.isUserDefined()) {
            getXrefStore(tag).addPointer(pointer)
        }
    }

    Set<Pointer> getUnsatisfiedPointers() {
        def set = [] as Set

        map.keySet().each { String key ->
            set.addAll(map[key].getUnsatisfiedPointers())
        }

        set
    }

    private XrefStore getXrefStore(Tag tag) {
        def key

        if (tag.isUserDefined()) {
            def tagAsString = tag.toString()

            if (!map.containsKey(tagAsString)) {
                map.put(tagAsString, new XrefStore())
            }

            key = tagAsString
        } else {
            key = tag.xrefType.toString()
        }

        map[key]
    }
}
