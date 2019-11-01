package gedinline.main;

import gedinline.lexical.GedcomVersion;
import gedinline.lexical.InputLine;
import gedinline.lexical.InputRecord;
import gedinline.lexical.Tag;

import java.io.PrintWriter;

import static gedinline.main.OutputReport.*;

public class StructureListener {

    private long millis;
    private OutputReport outputReport;

    public StructureListener(PrintWriter printWriter) {
        millis = System.currentTimeMillis();
        outputReport = new OutputReport(printWriter);
        outputReport.reportValue(SUBMITTED_BY, "Unknown");
    }

    public void closeNormal() {
        long time = System.currentTimeMillis() - millis;
        long seconds = time / 1000;
        long teller = outputReport.getCounter(RECORD);
        int lines = outputReport.getCounter(LINE);
        int warnings = outputReport.getCounter(WARNING);
        String pluralEnding = seconds == 1 ? "" : "s";
        outputReport.reportValue(ANALYSIS_TIME, "" + seconds + " second" + pluralEnding + " to analyse the file (excluding upload time)");
        outputReport.reportValue(SPEED, (teller * 1000 / time) + " records per second");
        outputReport.reportValue(WARNINGS_PER_10000_LINES, (warnings * 10000 / lines) + "");
        outputReport.printReport();
    }

    public void closeError(String s) {
        outputReport.outputError(s);
    }

    public AnalysisStatistics getAnalysisStatistics() {
        return outputReport.getAnalysisStatistics();
    }

    public int getNumberOfWarnings() {
        return outputReport.getCounter(WARNING);
    }

    public void handleGedcomVersion(GedcomVersion gedcomVersion) {
        outputReport.reportValue(GEDCOM_VERSION_ASSUMED, gedcomVersion.toString());
    }

    public void handleInputLine(InputLine inputLine) {
        handleInputLine(null, inputLine, null);
    }

    public void handleInputLine(InputRecord parentRecord, InputLine inputLine, WarningSink warningSink) {
        Tag parentTag = parentRecord == null ? null : parentRecord.getInputLine().getTag();
        Tag tag = inputLine.getTag();
        String value = inputLine.getValue();
        int level = inputLine.getLevel().getLevel();

        outputReport.incrementCounter(LINE);

        if (level == 0) {
            outputReport.incrementCounter(RECORD);
        }

        if (tag.equals(new Tag("INDI"))) {
            outputReport.incrementCounter(INDIVIDUAL);
        } else if (tag.equals(new Tag("FAM"))) {
            outputReport.incrementCounter(FAMILY);
        } else if (tag.isUserDefined()) {
            outputReport.incrementCounter(USER_DEFINED);
        } else if (tag.equals(new Tag("SEX"))) {
            if (value.equals("M")) {
                outputReport.incrementCounter(MALE);
            } else if (value.equals("F")) {
                outputReport.incrementCounter(FEMALE);
            }
        } else if (tag.equals(new Tag("MARR"))) {
            outputReport.incrementCounter(MARRIAGE);
        } else if (tag.equals(new Tag("PLAC"))) {
            outputReport.incrementCounter(PLACE);
        } else if (tag.equals(new Tag("SOUR")) && level == 0) {
            outputReport.incrementCounter(SOURCE);
        } else if (tag.equals(new Tag("SOUR")) && parentTag != null && parentTag.equals(new Tag("HEAD"))) {
            outputReport.reportValue(GENERATED_BY, value);
        } else if (tag.equals(new Tag("NAME")) && parentTag != null && parentTag.equals(new Tag("SOUR"))) {
            if (outputReport.getValue(GENERATED_BY).equals("")) {
                outputReport.reportValue(GENERATED_BY, value);
            }
        } else if (tag.equals(new Tag("VERS")) && parentTag != null && parentTag.equals(new Tag("SOUR"))) {
            outputReport.reportValue(SOURCE_VERSION, value);
        } else if (tag.equals(new Tag("DATE")) && parentTag != null && parentTag.equals(new Tag("HEAD"))) {
            outputReport.reportValue(DATE, value);
        } else if (tag.equals(new Tag("NAME")) && parentTag != null && parentTag.equals(new Tag("SUBM"))) {
            outputReport.reportValue(SUBMITTED_BY, value);
        } else if (tag.equals(new Tag("VERS")) && parentTag != null && parentTag.equals(new Tag("GEDC"))) {
            if (!outputReport.hasKey(GEDCOM_VERSION_IN_FILE)) {
                outputReport.reportValue(GEDCOM_VERSION_IN_FILE, value);

                if (new GedcomVersion(value).equals(GedcomVersion.V_55)) {
                    warningSink.warning(inputLine.getLineNumber(), "Note that the de facto standard GEDCOM version is version 5.5.1");
                }
            }
        } else if (tag.equals(new Tag("CHAR"))) {
            outputReport.reportValue(ENCODING, value);
        }
    }

    public void handleLogWarning(int lineNumber, String s) {
        outputReport.incrementCounter(WARNING);
        outputReport.outputWarning(lineNumber, s);
    }

    public void setFilename(String filename) {
        outputReport.reportValue(FILENAME, filename);
    }
}
