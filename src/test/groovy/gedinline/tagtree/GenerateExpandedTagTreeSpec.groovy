package gedinline.tagtree

import gedinline.lexical.*
import spock.lang.*

class GenerateExpandedTagTreeSpec extends Specification {

    void generate() {

        given:

            def grammar = new TagTreeGrammar(GedcomVersion.V_555);
            def tagTreeStructures = grammar.expandAll(grammar.getSubtree('FILE'))
            def expandedTagTree = new StringBuilder()

            tagTreeStructures.each { TagTree tagTree ->
                expandedTagTree << tagTree.toString() + '\n\n'
            }

            def output = new File('src/main/doc/expanded-tag-tree.txt')
            output.text = expandedTagTree.toString()

    }
}
