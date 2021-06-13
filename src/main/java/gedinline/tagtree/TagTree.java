package gedinline.tagtree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gedinline.lexical.GedcomVersionNew;
import gedinline.lexical.Tag;
import gedinline.main.ValidatorBugException;

import java.util.List;
import java.util.Set;

public class TagTree {

    private SyntaxTreeNode syntaxTreeNode;
    private GedcomVersionNew gedcomVersion;
    private List<TagTree> subtrees = Lists.newArrayList();

    public TagTree(SyntaxTreeNode syntaxTreeNode, GedcomVersionNew gedcomVersion) {
        this.syntaxTreeNode = syntaxTreeNode;
        this.gedcomVersion = gedcomVersion;
    }

    public void addSubtree(TagTree tagTree) {
        subtrees.add(tagTree);
    }

    public Set<Tag> getSubTags() {
        Set<Tag> result = Sets.newHashSet();

        for (TagTree tagTree : subtrees) {
            result.add(tagTree.getSyntaxTreeNode().getTag());
        }

        Tag tag = syntaxTreeNode.getTag();

        if (gedcomVersion.is555() && !tag.isConxTag()) {
            result.add(Tag.CONC);
            result.add(Tag.CONT);
        }

        return result;
    }

    public TagTree getSubtree(Tag tag) {
        for (TagTree subtree : subtrees) {
            SyntaxTreeNode syntaxTreeNode1 = subtree.getSyntaxTreeNode();

            if (syntaxTreeNode1 == null) {
                throw new ValidatorBugException("Subtree " + subtree + " has no syntax tree node");
            }

            Tag tag1 = syntaxTreeNode1.getTag();

            if (tag1 == null) {
                throw new ValidatorBugException("Node '" + syntaxTreeNode1.getOrigin() + "' has no tag");
            }

            if (tag1.equals(tag)) {
                return subtree;
            }
        }

        return null;
    }

    public List<TagTree> getSubtrees(Tag tag) {
        List<TagTree> result = Lists.newArrayList();

        for (TagTree subtree : subtrees) {
            SyntaxTreeNode syntaxTreeNode1 = subtree.getSyntaxTreeNode();

            if (syntaxTreeNode1 == null) {
                throw new ValidatorBugException("Subtree " + subtree + " has no syntax tree node");
            }

            Tag tag1 = syntaxTreeNode1.getTag();

            if (tag1 == null) {
                throw new ValidatorBugException("Node '" + syntaxTreeNode1.getOrigin() + "' has no tag");
            }

            if (tag1.equals(tag)) {
                result.add(subtree);
            }
        }

        return result;
    }

    public List<TagTree> getSubtrees() {
        return subtrees;
    }

    public SyntaxTreeNode getSyntaxTreeNode() {
        return syntaxTreeNode;
    }

    public boolean isLeaf() {
        return subtrees.isEmpty();
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int level) {
        String m = syntaxTreeNode.toString(level) + "\n";

        for (TagTree tagTree : subtrees) {
            m += tagTree.toString(level + 1);
        }

        return m;
    }
}
