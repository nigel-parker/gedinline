package gedinline.lexical;

import gedinline.main.InvalidFormatException;
import gedinline.main.NullLogger;
import gedinline.main.WarningSink;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * BasicParser is the first step in processing a GEDCOM file. It decides which encoding to use and returns a List of
 * InputLinePrecursors.
 */
@SuppressWarnings("Duplicates")
public class BasicParser {

    private static final String ANSEL = "ANSEL";
    private static final String ASCII = "ASCII";
    private static final char UNICODE_REPLACEMENT_CHARACTER = '\uFFFD';
    private static final String UTF_8 = "UTF-8";
    private static final String UTF_16 = "UTF-16";
    private static final String UNICODE = "UNICODE";
    private static final int MAX_LINE_LENGTH = 255;
    private static final String ENCODING_TRIGGER = "1 CHAR ";
    private static final String GEDCOM_TRIGGER = "1 GEDC";
    private static final String GEDCOM_VERSION_TRIGGER = "2 VERS ";

    private GedcomVersionNew gedcomVersion;
    private boolean valid555HeaderSeen;
    private Level level;
    private int lineNumber;
    private WarningSink log;
    private Level previousLevel = new Level();
    private LineIterator lineIterator;
    private boolean fileFormatHasBeenVerified;
    private InputLinePrecursor next = null;
    private boolean finished = false;

    public BasicParser(InputStream inputStream) throws IOException {
        this(inputStream, new NullLogger());
    }

    public BasicParser(InputStream inputStream, WarningSink log) throws IOException {
        this.log = log;

        if (inputStream != null) {
            determineEncoding(new BufferedInputStream(inputStream, 100000));
        }
    }

    private void determineEncoding(InputStream inputStream) throws IOException {

        String encoding = getEncoding(inputStream);
        Reader reader;

        if (encoding == null) {

            log.warning("No character encoding found in GEDCOM file, assuming ANSEL");
            reader = new AnselInputStreamReader(inputStream);

        } else if (encoding.equals(UTF_8)) {
            UnicodeBomInputStream unicodeBomInputStream = new UnicodeBomInputStream(inputStream);
            unicodeBomInputStream.skipBOM();
            reader = new InputStreamReader(unicodeBomInputStream, UTF_8);

        } else if (encoding.equals(UNICODE)) {

            UnicodeBomInputStream unicodeBomInputStream = new UnicodeBomInputStream(inputStream);
            reader = new InputStreamReader(unicodeBomInputStream, UTF_16);

        } else if (encoding.equals(ASCII)) {
            reader = new InputStreamReader(inputStream, "US-ASCII");

        } else if (encoding.equals(ANSEL)) {
            reader = new AnselInputStreamReader(inputStream);

        } else {
            log.warning("Invalid character encoding found: " + encoding + ", assuming ANSEL");
            reader = new AnselInputStreamReader(inputStream);
        }

        BufferedReader bufferedReader = new BufferedReader(reader);
        lineIterator = IOUtils.lineIterator(bufferedReader);
        fileFormatHasBeenVerified = false;
        lineNumber = 0;
    }

    private String getEncoding(InputStream inputStream) throws IOException {
        inputStream.mark(0);
        String declaredEncoding;

        if (isValidGedcom(inputStream, ASCII)) {
            declaredEncoding = tryEncoding(inputStream, ASCII);
        } else {
            declaredEncoding = tryEncoding(inputStream, UTF_16);
        }

        if (gedcomVersion == null) {
            log.warning("No GEDCOM version number found, assuming version 5.5.1");
            gedcomVersion = GedcomVersionNew.V_551;
        } else if (!gedcomVersion.isSupported()) {
            log.warning("No validation support for GEDCOM version " + gedcomVersion + ", assuming rules for version 5.5.1");
            gedcomVersion = GedcomVersionNew.V_551;
        } else if (gedcomVersion.is70()) {
            declaredEncoding = "UTF-8";
        }

        return declaredEncoding;
    }

    public GedcomVersionNew getGedcomVersion() {
        return gedcomVersion;
    }

    public boolean hasNext() {

        if (finished) {
            return false;
        } else if (next != null) {
            return true;
        }

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            lineNumber++;

            if (line.length() > 0) {
                String trimmedLine = line.trim();

                if (!fileFormatHasBeenVerified && trimmedLine.equals("0 HEADER")) {
                    throw new InvalidFormatException("File not recognised as valid GEDCOM file, appears to be in FTW TEXT format");
                } else if (!fileFormatHasBeenVerified && !trimmedLine.equals("0 HEAD")) {
                    throw new InvalidFormatException("File not recognised as valid GEDCOM file");
                } else {
                    fileFormatHasBeenVerified = true;
                }

                if (gedcomVersion.is555()) {
                    handleLine555(line);
                } else {
                    handleLine(line);
                }

                return true;
            }
        }

        next = null;
        lineIterator.close();
        finished = true;
        return false;
    }

    public void handleLine555(String lineUntrimmed) {
        String line = lineUntrimmed;
        String remainder;
        int i = StringUtils.indexOfAnyBut(line, "0123456789");

        if (i == -1) {
            i = line.length();
            line = line + " ";
            remainder = "";
        } else {
            remainder = line.substring(i + 1);
        }

        if ((i == 1 || i == 2) && (!remainder.isEmpty())) {
            if (i == 2 && line.startsWith("0")) {
                warn("Level numbers should not have leading zeroes");
            }

            level = new Level(Integer.parseInt(line.substring(0, i)));
            int maxLevel = previousLevel.getLevel() + 1;

            if (level.getLevel() > maxLevel) {
                warn("Level numbers must increase by 1");
                level = new Level(maxLevel);
            }

            if (!line.substring(i, i + 1).equals(" ")) {
                warn("Invalid GEDCOM record '" + line + "'");
            }
        } else {
            warn("Invalid GEDCOM record '" + lineUntrimmed + "'");
        }

        if (lineUntrimmed.length() > MAX_LINE_LENGTH) {
            warn("Line is more than " + MAX_LINE_LENGTH + " characters");
        }

        if (checkForIllegalCharacters(line)) {
            if (line.contains("\t")) {
                warn("Line contains tab character, not allowed under GEDCOM rules");
            } else {
                warn("Line contains illegal character(s)");
            }
        }

        next = new InputLinePrecursor(lineNumber, level, remainder, lineUntrimmed);
        previousLevel = level;
    }

    public void handleLine(String lineUntrimmed) {
        String line = lineUntrimmed.trim();
        String remainder;
        int i = StringUtils.indexOfAnyBut(line, "0123456789");

        if (i == -1) {
            i = line.length();
            line = line + " ";
            remainder = "";
        } else {
            remainder = line.substring(i + 1);
        }

        if (i == 1 || i == 2) {
            if (i == 2 && line.startsWith("0")) {
                warn("Level numbers should not have leading zeroes");
            }

            level = new Level(Integer.parseInt(line.substring(0, i)));
            int maxLevel = previousLevel.getLevel() + 1;

            if (level.getLevel() > maxLevel) {
                warn("Level numbers must increase by 1");
                level = new Level(maxLevel);
            }

            if (!line.substring(i, i + 1).equals(" ")) {
                warn("Invalid GEDCOM record '" + line + "'");
            }
        } else {
            warn("Invalid GEDCOM record '" + lineUntrimmed + "'");
        }

        if (lineUntrimmed.length() > MAX_LINE_LENGTH) {
            warn("Line is more than " + MAX_LINE_LENGTH + " characters");
        }

        if (checkForIllegalCharacters(line)) {
            if (line.contains("\t")) {
                warn("Line contains tab character, not allowed under GEDCOM rules");
            } else {
                warn("Line contains illegal character(s)");
            }
        }

        next = new InputLinePrecursor(lineNumber, level, remainder, lineUntrimmed);
        previousLevel = level;
    }

    private boolean checkForIllegalCharacters(String s) {
        StringCharacterIterator iterator = new StringCharacterIterator(s);
        char c = iterator.current();

        while (c != CharacterIterator.DONE) {
            if ((Character.isISOControl(c) && c != '\n' && c != '\r') || c == UNICODE_REPLACEMENT_CHARACTER) {
                return true;
            }

            c = iterator.next();
        }

        return false;
    }

    public InputLinePrecursor next() {
        InputLinePrecursor result = next;
        next = null;
        return result;
    }

    private String tryEncoding(InputStream inputStream, String encodingString) throws IOException {
        String declaredEncoding = null;
        LineIterator iterator = IOUtils.lineIterator(inputStream, encodingString);
        int lineCount = 0;
        boolean gedcomTagSeen = false;
        gedcomVersion = null;
        HeaderParser headerParser = new HeaderParser();
        int headerLineCount = headerParser.getLineCount();

        while (iterator.hasNext() && lineCount++ < 50) {
            String line = iterator.nextLine();

            if (line.startsWith("0 HEADER")) {
                throw new InvalidFormatException("File not recognised as valid GEDCOM file, appears to be in FTW TEXT format");
            }

            if (line.contains(GEDCOM_TRIGGER)) {
                gedcomTagSeen = true;
            }

            if (gedcomTagSeen && line.contains(GEDCOM_VERSION_TRIGGER) && gedcomVersion == null) {
//                gedcomVersion = new GedcomVersion(StringUtils.substringAfter(line, GEDCOM_VERSION_TRIGGER).trim());
                String gedcomVersionS = StringUtils.substringAfter(line, GEDCOM_VERSION_TRIGGER).trim();
                System.out.println("%%% gedcomVersionS = " + gedcomVersionS);
                gedcomVersion = GedcomVersionNew.fromString(gedcomVersionS);
                gedcomTagSeen = false;
            }

            if (line.contains(ENCODING_TRIGGER) && declaredEncoding == null) {
                declaredEncoding = StringUtils.substringAfter(line, ENCODING_TRIGGER).trim();
            }

            if (lineCount <= headerLineCount) {
                headerParser.addLine(line);
            }
        }

        valid555HeaderSeen = headerParser.isValidGedcom555Header();

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new InvalidFormatException("File not recognised as valid GEDCOM file");
        }

        return declaredEncoding;
    }

    private boolean isValidGedcom(InputStream inputStream, String encodingString) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, encodingString);
        char[] buffer = new char[9];
        reader.read(buffer);

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new InvalidFormatException("Problem reading file");
        }

        return new String(buffer).contains("0 HEAD");
    }

    private void warn(String s) {
        log.warning(lineNumber, s);
    }
}
