package gedinline.util

class Utils {

    static List<String> splitGedcomList(String s) {
        def input = ' ' + s + ' '

        input.split(',').collect { it.trim() }
    }
}
