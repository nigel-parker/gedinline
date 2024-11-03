package gedinline.value;

import gedinline.lexical.GedcomVersion;
import gedinline.main.Debug;
import gedinline.util.Utils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * User: nigel
 * Date: Jun 24, 2011
 * Time: 8:25:50 AM
 */
public class ExpressionParser {

    public final static String REPLACE_WITH = "Replace with:";

    private SyntaxElement syntaxElement;
    private SyntaxExpression syntaxExpression;
    private String input;
    private SyntaxElementLocator syntaxElementLocator;
    private GedcomVersion gedcomVersion;
    private boolean suppressValue = false;

    public ExpressionParser(SyntaxElement syntaxElement, SyntaxElementLocator syntaxElementLocator, GedcomVersion gedcomVersion) {
        this(syntaxElement.getSyntaxExpression(), syntaxElementLocator, gedcomVersion);
        this.syntaxElement = syntaxElement;
    }

    public ExpressionParser(SyntaxExpression syntaxExpression, SyntaxElementLocator syntaxElementLocator, GedcomVersion gedcomVersion) {
        this.syntaxExpression = syntaxExpression;
        this.syntaxElementLocator = syntaxElementLocator;
        this.gedcomVersion = gedcomVersion;
    }

    public ParsingResult parse(String input) {
        log("parsing '" + input + "' as " + syntaxExpression);
        this.input = input;

        if (syntaxExpression.isNull()) {
            return okResult(new StringResult(""), input);

        } else if (syntaxExpression.isDecimal()) {
            int i = StringUtils.indexOfAnyBut(input, "0123456789");

            if (i < 0) {
                return okResult(new StringResult(input), "");
            } else if (i == 0) {
                return fail();
            } else {
                return okResult(new StringResult(input.substring(0, i)), input.substring(i));
            }

        } else if (syntaxExpression.isWhitespace()) {
            int i = StringUtils.indexOfAnyBut(input, " \\t");

            if (i < 0) {
                return okResult(new StringResult(input), "");
            } else if (i == 0) {
                return fail();
            } else {
                return okResult(new StringResult(input.substring(0, i)), input.substring(i));
            }

        } else if (syntaxExpression.isStringInBrackets()) {
            int i = StringUtils.indexOf(input, ")");

            if (i < 0) {
                return okResult(new StringResult(input), "");
            } else {
                return okResult(new StringResult(input.substring(0, i)), input.substring(i));
            }

        } else if (syntaxExpression.isRegex()) {
            String regex = syntaxExpression.getTerm().getTerms().get(0).getAtom();

            if (input.matches(regex)) {
                return okResult(new StringResult(""), "");
            } else {
                return fail();
            }
        } else if (syntaxExpression.isNonSpaceString()) {
            int i = StringUtils.indexOf(input, " ");

            if (i < 0) {
                return okResult(new StringResult(input), "");
            } else {
                return okResult(new StringResult(input.substring(0, i)), input.substring(i));
            }

        } else if (syntaxExpression.isString()) {
            if (input.startsWith("@")) {
                return fail();
            } else {
                return okResult(new StringResult(input), "");
            }

        } else if (syntaxExpression.isDateValue()) {

            // Note that for speed this code overrides most of the date parsing specification in the value-grammar.
            // DATE_EXACT and DATE_PERIOD are still parsed the old way.

            ValidationResult vr = new DateValue(input, gedcomVersion).validate();

            if (vr.hasMessage()) {
                return fail(vr.getMessage());
            } else if (vr.isValid()) {
                return okResult(new StringResult(input), "");
            } else {
                return fail();
            }

        } else if (syntaxExpression.isPersonalName()) {
            if (new PersonalName(input, gedcomVersion).isValid()) {
                return okResult(new StringResult(input), "");
            } else {
                return fail();
            }

        } else if (syntaxExpression.isList()) {

            SyntaxExpression listType = syntaxExpression.getTerms().get(0);
            ExpressionParser parser = new ExpressionParser(listType, syntaxElementLocator, gedcomVersion);
            List<String> items = Utils.splitGedcomList(input);

            if (items.isEmpty()) {
                return fail();
            }

            for (String s : items) {
                String trimmed = s.trim();
                ParsingResult result = parser.parse(trimmed);

                if (!result.isOk()) {
                    return fail();
                }
            }

            return okResult(new StringResult(""), "");

        } else if (syntaxExpression.isConjunction()) {
            String remainder = input;
            ConcatenationResult concatenationResult = new ConcatenationResult();

            for (SyntaxExpression expression : syntaxExpression.getTerms()) {
                ExpressionParser parser = new ExpressionParser(expression, syntaxElementLocator, gedcomVersion);
                ParsingResult result = parser.parse(remainder);

                if (result.isOk()) {
                    concatenationResult.add(result.getValue());
                    remainder = result.getRemainder();
                } else {
                    return fail();
                }
            }

            return okResult(concatenationResult, remainder);

        } else if (syntaxExpression.isDisjunction()) {

            for (SyntaxExpression expression : syntaxExpression.getTerms()) {
                ExpressionParser parser = new ExpressionParser(expression, syntaxElementLocator, gedcomVersion);
                ParsingResult result = parser.parse(input);

                if (result.isOk()) {
                    return okResult(result.getValue(), result.getRemainder());
                }
            }

            return fail();

        } else if (syntaxExpression.isSyntaxElement()) {

            SyntaxElement syntaxElement = syntaxElementLocator.find(syntaxExpression.getSyntaxElementName());
            ExpressionParser parser1 = new ExpressionParser(syntaxElement, syntaxElementLocator, gedcomVersion);
            return parser1.parse(input);

        } else {

            String syntaxExpression = this.syntaxExpression.getExpression();
            Validator validator = Validator.of(syntaxExpression);
            String result = validator == null ? "not found" : "located";

            log("--- syntax expression '" + syntaxExpression + "': validator " + result);

            if (validator != null) {
                ValidationResult vr = validator.validate(input, gedcomVersion);

                if (vr.hasMessage()) {
                    return fail(vr.getMessage());
                } else if (vr.isValid()) {
                    log("--- okResult()");
                    return okResult(new StringResult(input), "");
                } else {
                    log("--- fail()");
                    return fail();
                }

            } else {

                if (input.toUpperCase().startsWith(syntaxExpression.toUpperCase())) {
                    return okResult(new StringResult(syntaxExpression), input.substring(syntaxExpression.length()));
                } else {
                    return fail();
                }
            }
        }
    }

    private ParsingResult fail() {
        return fail(null);
    }

    private ParsingResult fail(String errorMessage) {
        ParsingResult parsingResult = new ParsingResult();
        parsingResult.setInput(input);
        parsingResult.setOk(false);
        parsingResult.setValue(null);
        parsingResult.setSuppressValue(suppressValue);
        parsingResult.setRemainder(input);
        parsingResult.setErrorMessage(errorMessage);
        log("fail");
        return parsingResult;
    }

    private ParsingResult okResult(ParseResultValue value, String remainder) {
        String parsed = input.substring(0, input.length() - remainder.length());

        if (syntaxElement != null) {
            Cardinality cardinality = syntaxElement.getCardinality();
            int parsedLength = parsed.length();

            if (parsedLength < cardinality.getMinimum()) {
                if (parsedLength == 0) {
                    log("fail: missing value");
                    return fail("missing value for");
                } else {
                    log("fail: too short");
                    return fail("is too short to be");
                }
            } else if (parsedLength > cardinality.getMaximum()) {
                log("fail: too long");
                suppressValue = true;
                return fail("is more than " + cardinality.getMaximum() + " characters, the maximum length for");
            }
        }

        ParsingResult parsingResult = new ParsingResult();
        parsingResult.setInput(input);
        parsingResult.setOk(true);
        parsingResult.setValue(value);
        parsingResult.setRemainder(remainder);
        log("OK");
        return parsingResult;
    }

    private void log(String s) {
        if (Debug.active()) {
            System.out.println("--- " + s);
        }
    }
}
