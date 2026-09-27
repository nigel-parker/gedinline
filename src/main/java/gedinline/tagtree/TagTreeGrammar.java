package gedinline.tagtree;

import gedinline.lexical.GedcomVersion;
import gedinline.main.ValidatorBugException;
import gedinline.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TagTreeGrammar {

    private Map<String, List<String>> subtrees = new HashMap<>();
    private GedcomVersion gedcomVersion;

    public TagTreeGrammar(GedcomVersion gedcomVersion) {
        this.gedcomVersion = gedcomVersion;
        handleFile(gedcomVersion.getTagTree());
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
                    subtrees.computeIfAbsent(subtreeName, k -> new ArrayList<>()).add(s.trim());
                }
            }
        }
    }

    public List<TagTree> getSubtree(String subtreeName) {
        List<TagTree> result = new ArrayList<>();
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
        List<TagTree> result = new ArrayList<>();

        for (TagTree tagTree : tagTrees) {
            result.addAll(expand(tagTree, tagTree.getSyntaxTreeNode().getOccurrence()));
        }

        return result;
    }

    public List<TagTree> expand(List<TagTree> tagTrees, Occurrence occurrence) {
        List<TagTree> result = new ArrayList<>();

        for (TagTree tagTree : tagTrees) {
            result.addAll(expand(tagTree, occurrence));
        }

        return result;
    }

    int expandLevel = 0;

    public List<TagTree> expand(TagTree tagTree, Occurrence occurrence) {
        expandLevel++;

        SyntaxTreeNode syntaxTreeNode = tagTree.getSyntaxTreeNode().with(occurrence);

        if (syntaxTreeNode.isSubtreeReference()) {
            if (expandLevel >= 10) {
                expandLevel--;

                return List.of();
            } else {
                List<TagTree> tagTrees = getSubtree(syntaxTreeNode.getSubtreeReference().getId());

                expandLevel--;
                return expand(tagTrees, syntaxTreeNode.getOccurrence());
            }
        } else {
            TagTree result = new TagTree(syntaxTreeNode, gedcomVersion);

            for (TagTree tree : expandAll(tagTree.getSubtrees())) {
                result.addSubtree(tree);
            }

            expandLevel--;
            return List.of(result);
        }
    }

    public void setGedcomVersion(GedcomVersion gedcomVersion) {
        this.gedcomVersion = gedcomVersion;
    }
}
