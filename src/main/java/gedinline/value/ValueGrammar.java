package gedinline.value;

import gedinline.lexical.GedcomVersion;
import gedinline.main.ValidatorBugException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: nigel
 * Date: Jun 24, 2011
 * Time: 5:31:25 PM
 */
public class ValueGrammar implements SyntaxElementLocator {

    @SuppressWarnings("unchecked")
    private Map<String, SyntaxElement> map = new HashMap();
    private String name;
    private Cardinality cardinality;
    private SyntaxExpression syntaxExpression;
    private String description = "";

    @SuppressWarnings("unchecked")
    public ValueGrammar(GedcomVersion gedcomVersion) {
        String filename = gedcomVersion.is555() ? "value-grammar-555.txt" : "value-grammar.txt";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        List<String> list;

        try {
            list = (List<String>) IOUtils.readLines(inputStream);
        } catch (IOException e) {
            throw new ValidatorBugException("Cant find grammar file", e);
        }

        State state = State.WAITING;

        for (String s : list) {

            switch (state) {
                case WAITING:
                    if (s.contains(":=")) {
                        createPrevious();
                        name = StringUtils.substringBefore(s, ":=").trim();
                        cardinality = new Cardinality(StringUtils.substringAfter(s, ":=").trim());
                        state = State.FOUND_NAME;
                    } else {
                        description += s + "\n";
                    }

                    break;

                case FOUND_NAME:
                    syntaxExpression = new SyntaxExpression(s.trim());
                    state = State.WAITING;
                    break;
            }
        }

        createPrevious();
    }

    private void createPrevious() {
        if (name != null) {
            map.put(name, new SyntaxElement(name, cardinality, syntaxExpression, description));
            name = null;
            description = "";
        }
    }

    public SyntaxElement find(String name) {
        if (name.startsWith("@")) {
            name = name.substring(2, name.length() - 2);
        }

        SyntaxElement syntaxElement = map.get(name);

        if (syntaxElement == null) {
            throw new ValidatorBugException("Cant find syntax element " + name);
        }

        return syntaxElement;
    }

    enum State {
        WAITING, FOUND_NAME
    }
}
