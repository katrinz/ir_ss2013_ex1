/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * This class provides some helper procedures to make IO a bit simpler.
 *
 */
public class IOTools extends Object {

    public static final String GENERIC_WORD_DELIMITERS =
            " ~!@#$%^&*()_+`={}[]|:;<>,./?"
            + "\\\"\'\t\n\f\r-"
            + System.getProperty("line.separator");
    public static final String NUMBERS = "0123456789";
    public static final String WHITE_SPACE_CHARACTERS =
            " \t\n\f\r" + System.getProperty("line.separator");
    private static BufferedReader standardInput =
            new BufferedReader(
            new InputStreamReader(System.in));

    /**
     * Prevent instantiation.
     *
     */
    private IOTools() {
    }

    /**
     * Reads a line of text from stdin and returns it as a string.
     *
     */
    public static String readLine() {
        String result = "";

        try {
            result = standardInput.readLine();
        } catch (IOException e) {
            System.err.println("IO error reading from stdin: " + e);
        }

        return result;
    }

    /**
     * Reads
     * <code>numLines</code> lines from the reader, concatenates them into a
     * String, and returns the String. If the reader doesn't contain enough
     * lines, all lines that exist are returned.
     *
     */
    public static String readLines(BufferedReader rdr, int numLines) {
        String result = "";
        String aLine = null;
        int linesRead = 0;

        if (numLines <= 0) {
            return result;
        }

        try {
            aLine = rdr.readLine();
            linesRead++;
            while ((linesRead <= numLines) && (aLine != null)) {
                result += aLine + "\n";
                aLine = rdr.readLine();
                linesRead++;
            }
        } catch (IOException e) {
            Debug.reportError("IO problem: " + e);
        }

        return result;
    }

    /**
     * Reads the entire contents of a reader and returns them as a String. The
     * reader is also closed, since there is nothing more to read from it.
     *
     */
    public static String slurp(BufferedReader rdr) {
        Debug.dassert(rdr != null);

        String result = "";

        try {
            int n = 0;
            while (!rdr.ready() && n < 5) { // wait until there is something to read 
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }       // or for 5 secs 
                n++;                       // (important for slow streams)
            }

            String aLine = rdr.readLine();

            while (aLine != null) {
                result += aLine + "\n";
                aLine = rdr.readLine();
            }

            rdr.close();
        } catch (IOException e) {
            Debug.reportError("IO problem: " + e);
        }

        return result;
    }

    /**
     * Creates a File that will work on any operating system, assuming the
     * directory structures are the same. This method should only be used for
     * hardcoded filenames. Filenames input by the user should be left as-is, so
     * they may be interpreted in an OS-dependent manner.
     *
     * <p> If the first character of the string is a forward slash or tilde, or
     * the second character is a colon, then the string is assumed to be an
     * absolute filename, and the file is created directly from the string.
     * Otherwise, the user's home directory is used as a base, and the file is
     * created relative to this base.
     *
     */
    public static File getIndependentFile(String filename) {
        Debug.dassert((filename != null)
                && (filename.length() > 0));

        File result = null;

        if ((filename.charAt(0) == '/')
                || (filename.charAt(0) == '~')
                || ((filename.length() > 1) && (filename.charAt(1) == ':'))) {
            result = new File(filename);
        } else {
            File baseDir = new File(System.getProperty("user.home"));
            result = new File(baseDir, filename);
        }

        return result;
    }

    /**
     * Returns a
     * <code>PrintWriter</code> that can write to the given file.
     *
     */
    public static PrintWriter getFileWriter(String filename) {
        return getFileWriter(new File(filename));
    }

    /**
     * Returns a
     * <code>PrintWriter</code> that can write to the given file.
     *
     */
    public static PrintWriter getFileWriter(File theFile) {
        PrintWriter result = null;

        try {
            result = new PrintWriter(
                    new BufferedWriter(
                    new FileWriter(theFile)));
        } catch (IOException e) {
            Debug.setFatal();
            Debug.reportError("FATAL ERROR creating output files:\n" + e);
        }

        return result;
    }

    /**
     * Returns a
     * <code>BufferedReader</code> that can read the contents of the file
     * <code>filename</code>.
     *
     */
    public static BufferedReader getFileReader(String filename)
            throws FileNotFoundException {
        return getFileReader(new File(filename));
    }

    /**
     * Returns a
     * <code>BufferedReader</code> that can read the contents of a File.
     *
     */
    public static BufferedReader getFileReader(File theFile)
            throws FileNotFoundException {
        BufferedReader theReader;
        theReader = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(theFile)));

        return theReader;
    }
    
    /**
     * Returns a
     * <code>BufferedReader</code> that can read the contents of the GZIP file
     * <code>filename</code>.
     *
     */
    public static BufferedReader getGZIPFileReader(String filename)
            throws FileNotFoundException, IOException {
        return getGZIPFileReader(new File(filename));
    }

    /**
     * Returns a
     * <code>BufferedReader</code> that can read the contents of a GZIP File.
     *
     */
    public static BufferedReader getGZIPFileReader(File theFile)
            throws FileNotFoundException, IOException {
        BufferedReader theReader;
        theReader = new BufferedReader(
                new InputStreamReader(
                        new GZIPInputStream(new FileInputStream(theFile))));

        return theReader;
    }
    
    /**
    Returns a <code>BufferedReader</code> that can read the sanitized
    contents of the file <code>filename</code>. For sanitizing,
    contents are passed through the WashingFilter, which throws away
    non-ASCII characters.
     **/
    public static BufferedReader getWashingFileReader(String filename)
            throws FileNotFoundException {
        return getWashingFileReader(getIndependentFile(filename));
    }

    /**
    Returns a <code>BufferedReader</code> that can read the sanitized
    contents of the File. For sanitizing,
    contents are passed through the WashingFilter, which throws away
    non-ASCII characters.
     **/
    public static BufferedReader getWashingFileReader(File theFile)
            throws FileNotFoundException {
        BufferedReader theReader = null;
        
        theReader = new BufferedReader(
                new InputStreamReader(
                new WashingFilterInputStream(
                new FileInputStream(theFile))));

        return theReader;
    }
    

    /**
     * Saves a single object to the specified file. The object must be
     * <code>Serializable</code>. If the file exists, it is overwritten. If it
     * doesn't exist, it is created.
     *
     */
    public static void saveObject(Object item, File theFile) throws IOException {
        Debug.trace("Writing " + item + " to " + theFile);
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(theFile));

        out.writeObject(item);
        out.flush();
        out.close();
    }

    /**
     * Retrieves an object from the specified file. The object must be
     * <code>Serializable</code>. Assumes that there is only one object (or
     * object graph) in the file.
     *
     * @return The object if it is found and readable, <code>null</code> if it
     * is not.
     *
     */
    public static Object retrieveObject(File theFile) throws IOException {
        Object item = null;

        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(theFile));
            item = in.readObject();
            in.close();
            Debug.trace("File " + theFile + " found and used.");
        } catch (FileNotFoundException e) {
            // this is a fairly common occurrence, if an object hasn't
            // been saved yet (the only way to check if an object
            // is saved is to try loading it), so don't report an
            // error, just print a trace message and return null
            Debug.trace("File " + theFile + " not found");
        } catch (ClassNotFoundException e) {
            Debug.reportError("Object not found in " + theFile + ", " + e);
        }

        return item;
    }

    /**
     * Changes the extension of a filename.
     *
     * @return The filename with the new extension.
     *
     */
    public static String changeExtension(String filename, String extension) {
        String result = "";

        int lastDot = filename.lastIndexOf(".");
        if (lastDot >= 0) {
            result = filename.substring(0, lastDot) + "." + extension;
        } else {
            result = filename + "." + extension;
        }

        return result;
    }

    public static byte[] objectToBytes(Serializable o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Error encodeing: " + o + ", instance of " + o.getClass().getName());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Object objectFromBytes(byte bytes[]) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Serializable o = (Serializable) ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * A convienence class which writes serializable objects out to disk.
     * Deletes the file if it already exists.
     *
     */
    public static boolean objectToFile(Serializable o, String filename) {
        try {
            File f = new File(filename);
            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            return true;
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e);
            return false;
        }

    }

    /**
     * Check to see if the specified file exists
     *
     */
    public static boolean fileExists(String name) {
        try {
            File f = new File(name);
            return f.exists();
        } catch (Exception e) {
            throw new RuntimeException("Error in fileExists: " + e);
        }
    }

    /**
     * A convienence class which reads serializable object from the disk. For
     * use with files written by
     * <code>objectToFile</code>
     *
     */
    public static Serializable objectFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Serializable o = (Serializable) ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            System.err.println("Error reading from file: " + e);
            return null;
        }
    }
}
