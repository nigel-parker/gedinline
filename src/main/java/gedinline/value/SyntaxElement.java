package gedinline.value;

public class SyntaxElement {

    private String name;
    private Cardinality cardinality;
    private SyntaxExpression syntaxExpression;
    private String description;

    public SyntaxElement(String name, Cardinality cardinality, SyntaxExpression syntaxExpression, String description) {
        this.name = name;
        this.cardinality = cardinality;
        this.syntaxExpression = syntaxExpression;
        this.description = description;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public String getName() {
        return name;
    }

    public SyntaxExpression getSyntaxExpression() {
        return syntaxExpression;
    }

    public String getDescription() {
        return description;
    }
}
