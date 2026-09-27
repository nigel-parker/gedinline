package gedinline.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The subset of commons-io 1.3.2 IOUtils used by GED-inline.
 */
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * Reads a UTF-8 stream (the grammar resources) into a list of lines.
     */
    public static List<String> readLines(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        List<String> result = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            result.add(line);
        }

        return result;
    }

    public static LineIterator lineIterator(Reader reader) {
        return new LineIterator(reader);
    }

    public static LineIterator lineIterator(InputStream inputStream, String encoding) throws IOException {
        return new LineIterator(new InputStreamReader(inputStream, encoding));
    }
}
