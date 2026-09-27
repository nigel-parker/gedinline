package gedinline.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * A line iterator with the behaviour of commons-io 1.3.2: hasNext() reads ahead without consuming, and reaching the
 * end of input does not close the reader. Only close() does that.
 */
public class LineIterator {

    private final BufferedReader bufferedReader;
    private String cachedLine;
    private boolean finished;

    public LineIterator(Reader reader) {
        bufferedReader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public boolean hasNext() {
        if (cachedLine != null) {
            return true;
        } else if (finished) {
            return false;
        }

        try {
            cachedLine = bufferedReader.readLine();

            if (cachedLine == null) {
                finished = true;
                return false;
            }

            return true;

        } catch (IOException e) {
            close();
            throw new IllegalStateException(e);
        }
    }

    public String nextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }

        String line = cachedLine;
        cachedLine = null;
        return line;
    }

    public void close() {
        finished = true;
        cachedLine = null;

        try {
            bufferedReader.close();
        } catch (IOException ignored) {
        }
    }
}
