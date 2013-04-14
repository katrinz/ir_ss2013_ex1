package tu.wien.irengine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
Filters control characters and high-bit characters from an input stream.
This allows text processors to only deal with printable characters.

Note that this class assumes the input stream is encoded in ASCII.
 */
public class WashingFilterInputStream extends FilterInputStream {
    // define some ASCII values

    private static final int EOF = -1;
    private static final int SPACE = 32;
    private static final int TILDE = 126;
    private static final int TAB = 9;
    private static final int NEWLINE = 10;
    private static final int RETURN = 13;

    /////////////////////////////////////////////////////////////////
    /**
    Creates a filtered input stream.
     **/
    public WashingFilterInputStream(InputStream in) {
        super(in);
    }

    /////////////////////////////////////////////////////////////////
    /**
    Reads in a character from the input stream. If the character is
    less than 32 (Ascii space) or greater than 126 (Ascii tilde), it
    is converted to a space. However, newlines, tabs, and carriage
    returns (Ascii 9, 10, 13) are kept.
     **/
    @Override
    public int read() throws IOException {
        int i = super.read();
        int result = i;

        if ((i != TAB) && (i != NEWLINE)
                && (i != RETURN) && (i != EOF)
                && ((i < SPACE) || (i > TILDE))) {
            result = SPACE;
        }

        return result;
    }

    /////////////////////////////////////////////////////////////////
    /**
    Reads up to len bytes of data from this input stream into an
    array of bytes. This method blocks until some input is available. 
     **/
    @Override
    public int read(byte[] buffer, int off, int len)
            throws IOException {
        int bytesRead = 0;

        if ((off < 0) || (len < 0) || ((len + off) > buffer.length)) {
            throw new IndexOutOfBoundsException();
        }

        if (available() == 0) {
            bytesRead = -1;
        } else if (len > 0) {
            for (bytesRead = 0; bytesRead < len; bytesRead++) {
                int thisByte = read();
                if (thisByte < 0) {

                    break;
                }

                buffer[off + bytesRead] = (byte) thisByte;
            }
        }

        return bytesRead;
    }

    /////////////////////////////////////////////////////////////////
    /**
    Washes the contents of a single string.
     **/
    public static String washString(String s) {
        if (s == null) {
            return s;
        }
        WashingFilterInputStream theStream =
                new WashingFilterInputStream(new ByteArrayInputStream(s.getBytes()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[128];
        try {
            int numRead = theStream.read(buffer, 0, 128);
            while (numRead != -1) {
                output.write(buffer, 0, numRead);
                numRead = theStream.read(buffer, 0, 128);
            }
        } catch (Exception e) {
        }
        return output.toString();
    }
}
