package gedinline.main;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OutputReport {

    public static final String ANALYSIS_TIME = "Analysis time";
    public static final String DATE = "Date";
    public static final String ENCODING = "Encoding";
    public static final String FAMILY = "Families";
    public static final String FEMALE = "Females";
    public static final String FILENAME = "Filename";
    public static final String GEDCOM_VERSION_ASSUMED = "GEDCOM version assumed";
    public static final String GEDCOM_VERSION_IN_FILE = "GEDCOM version in file";
    public static final String GENERATED_BY = "Generated by";
    public static final String INDIVIDUAL = "Individuals";
    public static final String LINE = "Lines";
    public static final String MALE = "Males";
    public static final String MARRIAGE = "Marriages";
    public static final String PLACE = "Places";
    public static final String RECORD = "Records";
    public static final String SPEED = "Speed";
    public static final String SOURCE = "Source records";
    public static final String SOURCE_VERSION = "Source version";
    public static final String SUBMITTED_BY = "Submitted by";
    public static final String UNKNOWN_GENDER = "Other";
    public static final String USER_DEFINED = "User-defined";
    public static final String WARNING = "Warnings";
    public static final String WARNINGS_PER_10000_LINES = "Warnings per 10000 lines";

    private static final String VALUE = "value";

    private Map<String, Counter> tellere = Maps.newTreeMap();
    private List<Counter> utskriftsliste = Lists.newArrayList();
    private TreeMultimap<Integer, String> warnings = TreeMultimap.create();
    private PrintWriter printWriter;
    private Map<String, String> values = Maps.newHashMap();
    private AnalysisStatistics analysisStatistics;

    public OutputReport(PrintWriter printWriter) {
        this.printWriter = printWriter;
        add(new Counter(GENERATED_BY, VALUE));
        add(new Counter(SUBMITTED_BY, VALUE));
        add(new Counter(ENCODING, VALUE));
        add(new Counter(GEDCOM_VERSION_IN_FILE, VALUE));
        add(new Counter(GEDCOM_VERSION_ASSUMED, VALUE));
        add(null);
        add(new Counter(ANALYSIS_TIME, VALUE));
        add(new Counter(SPEED, VALUE));
        add(null);
        add(new Counter(LINE, "Number of lines in the GEDCOM file"));
        add(new Counter(RECORD, "Number of records"));
        add(new Counter(WARNING, "Total number of warning messages"));
        add(new Counter(USER_DEFINED, "Number of lines with user-defined tags"));
        add(null);
        add(new Counter(INDIVIDUAL, "Number of individuals in the GEDCOM file"));
        add(new Counter(MALE, "Number of males"));
        add(new Counter(FEMALE, "Number of females"));
        add(new Counter(UNKNOWN_GENDER, ""));
        add(null);
        add(new Counter(FAMILY, "Number of families"));
        add(new Counter(MARRIAGE, "Number of marriages"));
        add(new Counter(PLACE, "Number of places mentioned (not necessarily unique)"));
        add(new Counter(SOURCE, "Number of source records"));
        reportValue(SOURCE_VERSION, "Unknown");
    }

    private void add(Counter counter) {
        if (counter != null && !counter.getForklaring().equals(VALUE)) {
            tellere.put(counter.getType(), counter);
        }

        utskriftsliste.add(counter);
    }

    public void incrementCounter(String type) {
        tellere.get(type).incrementTeller();
    }

    public void reportValue(String type, String value) {
        values.put(type, value);
    }

    public boolean hasKey(String type) {
        return values.containsKey(type);
    }

    public AnalysisStatistics getAnalysisStatistics() {
        return analysisStatistics;
    }

    private String getCount(String type) {
        return "" + getCounter(type);
    }

    private String getForklaring(String type) {
        return tellere.get(type).getForklaring();
    }

    public int getCounter(String type) {
        Counter counter = tellere.get(type);
        return counter.getAntall();
    }

    private String getValue(String type) {
        return values.containsKey(type) ? values.get(type) : "Unknown";
    }

    public void outputError(String s) {
        printWriter.println(s);
        printWriter.close();
    }

    public void outputWarning(int lineNumber, String s) {
        warnings.put(lineNumber, s);
    }

    public void printReport() {

        calculateUnknownGender();

        for (Counter counter : utskriftsliste) {
            if (counter == null) {
                printBlankLine();
            } else {
                printReportLine(counter.getType());
            }
        }

        printBlankLine();

        for (String s : warnings.values()) {
            printWriter.println(s);
        }

        printBlankLine();
        printWriter.close();

        analysisStatistics = new AnalysisStatistics(
                new Date(),
                getValue(FILENAME),
                getValue(GEDCOM_VERSION_IN_FILE),
                getValue(GENERATED_BY),
                getValue(SOURCE_VERSION),
                getValue(DATE),
                Integer.valueOf(getCount(LINE)),
                Integer.valueOf(getCount(RECORD)),
                Integer.valueOf(getValue(WARNINGS_PER_10000_LINES)),
                getValue(ANALYSIS_TIME),
                getValue(SPEED));
    }

    /**
     * The SEX tag is not obligatory although in the vast majority og cases it is present. This method corrects
     * the number of individuals with unknown gener for files which omit SEX.
     */
    private void calculateUnknownGender() {
        int individuals = findCounter(INDIVIDUAL).getAntall();
        int males = findCounter(MALE).getAntall();
        int females = findCounter(FEMALE).getAntall();
        findCounter(UNKNOWN_GENDER).setAntall(individuals - males - females);
    }

    private Counter findCounter(String type) {
        for (Counter counter : utskriftsliste) {
            if (counter != null && counter.getType().equals(type)) {
                return counter;
            }
        }

        return null;
    }

    private void printBlankLine() {
        printWriter.println("");
    }

    private void printReportLine(String type) {
        boolean isValue = !tellere.containsKey(type);

        String col1;
        String col2;
        String col3;

        if (isValue) {
            col1 = StringUtils.rightPad(StringUtils.abbreviate(type, 27), 29);
            col2 = "";
            col3 = getValue(type);
        } else {
            col1 = StringUtils.rightPad(StringUtils.abbreviate(type, 20), 22);
            col2 = StringUtils.leftPad(getCount(type), 7);
            col3 = getForklaring(type);
        }

        printWriter.println(col1 + col2 + "  " + col3);
    }
}
