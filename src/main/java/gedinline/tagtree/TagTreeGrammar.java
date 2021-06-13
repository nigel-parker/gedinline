package gedinline.tagtree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import gedinline.lexical.GedcomVersionNew;
import gedinline.main.ValidatorBugException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TagTreeGrammar {

    private Multimap<String, String> subtrees = ArrayListMultimap.create();
    private GedcomVersionNew gedcomVersion;

    public TagTreeGrammar(GedcomVersionNew gedcomVersion) {
        this.gedcomVersion = gedcomVersion;
        handleFile(gedcomVersion.is555() ? "tag-tree-555.txt" : "tag-tree.txt");
    }

    @SuppressWarnings("unchecked")
    private void handleFile(String filename) {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        List<String> list;

        try {
            list = (List<String>) IOUtils.readLines(inputStream);
        } catch (IOException e) {
            throw new ValidatorBugException("Cant find grammar file", e);
        }

        String subtreeName = "";

        for (String s : list) {

            if (s.endsWith(":")) {
                subtreeName = StringUtils.substringBefore(s, ":");
            } else {
                if (!StringUtils.isBlank(s)) {
                    subtrees.put(subtreeName, s.trim());
                }
            }
        }
    }

    public List<TagTree> getSubtree(String subtreeName) {
        List<TagTree> result = Lists.newArrayList();
        Collection<String> stringCollection = getSubtreeNames(subtreeName);
        Stack<TagTree> stack = new Stack<TagTree>();
        int currentLevel = 0;

        for (String s : stringCollection) {
            SyntaxTreeNode syntaxTreeNode = new SyntaxTreeNode(s);
            TagTree tagTree = new TagTree(syntaxTreeNode, gedcomVersion);

            if (stack.isEmpty()) {
                stack.push(tagTree);
            } else {
                int pops = currentLevel - syntaxTreeNode.getLevel() + 1;

                for (int i = 0; i < pops; i++) {
                    if (stack.size() == 1) {
                        result.add(stack.pop());
                    } else {
                        stack.pop();
                    }
                }

                if (!stack.isEmpty()) {
                    stack.peek().addSubtree(tagTree);
                }

                stack.push(tagTree);
                currentLevel = syntaxTreeNode.getLevel();
            }
        }

        while (stack.size() >= 2) {
            stack.pop();
        }

        result.add(stack.pop());
        return result;
    }

    private Collection<String> getSubtreeNames(String subtreeName) {

        String expandedName = subtreeName + gedcomVersion.getSuffix();

        if (subtrees.containsKey(expandedName)) {
            return subtrees.get(expandedName);

        } else if (subtrees.containsKey(subtreeName)) {
            return subtrees.get(subtreeName);

        } else {
            throw new ValidatorBugException("Cant find subtree " + subtreeName);
        }
    }

    public Set<String> getSubtreeNames() {
        return new TreeSet<String>(subtrees.keySet());
    }

    public TagTree expand(String tagTreeId) {
        return expandAll(getSubtree(tagTreeId)).get(0);
    }

    public List<TagTree> expandAll(List<TagTree> tagTrees) {
        List<TagTree> result = Lists.newArrayList();

        for (TagTree tagTree : tagTrees) {
            result.addAll(expand(tagTree, tagTree.getSyntaxTreeNode().getOccurrence()));
        }

        return result;
    }

    public List<TagTree> expand(List<TagTree> tagTrees, Occurrence occurrence) {
        List<TagTree> result = Lists.newArrayList();

        for (TagTree tagTree : tagTrees) {
            result.addAll(expand(tagTree, occurrence));
        }

        return result;
    }

    public List<TagTree> expand(TagTree tagTree, Occurrence occurrence) {
        SyntaxTreeNode syntaxTreeNode = tagTree.getSyntaxTreeNode().with(occurrence);

        if (syntaxTreeNode.isSubtreeReference()) {
            List<TagTree> tagTrees = getSubtree(syntaxTreeNode.getSubtreeReference().getId());

            return expand(tagTrees, syntaxTreeNode.getOccurrence());
        } else {
            TagTree result = new TagTree(syntaxTreeNode, gedcomVersion);

            for (TagTree tree : expandAll(tagTree.getSubtrees())) {
                result.addSubtree(tree);
            }

            return ImmutableList.of(result);
        }
    }

    public void setGedcomVersion(GedcomVersionNew gedcomVersion) {
        this.gedcomVersion = gedcomVersion;
    }
}
