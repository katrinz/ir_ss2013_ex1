package tu.wien.irengine.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

/**
Provides tools for debugging programs.

Errors are reported to stderr unless a logfile has been set.  
If the logfile is set, case all debugging information goes to the file.  

There are several ways to use this class:
<ul>
<li>Full-time mode. Any message sent to the untagged version of
<code>debugOut()</code> will appear in the debugging output every time
the code is run.
<li>Tracing mode. When writing code, add the <code>trace()</code>
method at points where you would like tracing information. This information
will only appear if <code>traceOn()</code> is set.
<li>Tagged mode. Use the tagged version of <code>debugOut</code>. This
information will appear any time the tag appears in the list that is set
with <code>setLevel</code>.
<li>Assertions. At critical points in your code (or in testing code), you
can <code>dassert</code> that a statement is true. If the statement is
true, nothing will happen. If the statement is false, an error will
be triggered.
</ul>

All of these debugging methods are independent, so you may use them in
combinations without causing problems.

<b>Known Issues:</b> on some JVMs (especially Windows versions),
when the stack trace contains
&quot;Compiled Code&quot;, failed dassertions will report an incorrect
failure point. The system for parsing stack traces needs to be tweaked.
 */
public class Debug {

    private static boolean errorsFatal = false;
    private static boolean traceOn = false;
    private static PrintWriter logFileWriter = null;
    private static String debugLevel[] = {};
    private static boolean quieter = false;
    private static boolean viewAllErrors = false;
    private static long timeStamp = 0;

    /////////////////////////////////////////////////////////////////
  /*
    Prevent instantiation.
     */
    private Debug() {
    }

    /////////////////////////////////////////////////////////////////
    /**
    Redirects all output to logFile.
     **/
    public static void setLogFile(File logFile) throws IOException {
        logFileWriter = new PrintWriter(
                new BufferedWriter(
                new FileWriter(logFile)));
    }
    ///////////////////////////////////////////////////////////////////////

    /**
    Send a message to the debugging output stream.
     **/
    public static void debugOut(String s) {
        if (logFileWriter == null) {
            if (!quieter) {
                System.err.println("DEBUG " + s);
            } else {
                System.err.println(s);
            }
            System.err.flush();
        } else {
            logFileWriter.println(s);
            logFileWriter.flush();
        }
    }
    ///////////////////////////////////////////////////////////////////////

    /**
    Control verbosity of messages. If <code>nQuieter</code> is true,
    messages will be as short as possible. If it is false, all
    messages will include an indication that they were generated by
    the Debugging system, along with the the associated level tag, if any.
     **/
    public static void setQuieter(boolean nQuieter) {
        quieter = nQuieter;
    }
    ///////////////////////////////////////////////////////////////////////

    /**
    Sets all errors to be fatal. Errors are considered to be failed
    dassertions or anything reported with <code>setError()</code>.
     **/
    public static void setFatal() {
        errorsFatal = true;
    }

    ////////////////////////////////////////////////////////////////
    /**
    Sets list of debugging tags to view. Any tags in this list
    will trigger debugging messages that are labeled with the
    same tag.
     **/
    public static void setLevel(String newLevel[]) {
        debugLevel = newLevel;
    }

    ///////////////////////////////////////////////////////////////
    /**
    Sets the debugger to view ALL debugging tags.
     **/
    public static void setLevelAll() {
        viewAllErrors = true;
    }

    ///////////////////////////////////////////////////////////////
    /**
    Returns true if the triggerLevel is one of the active tags
     */
    public static boolean isSet(String triggerLevel) {
        for (int i = 0; i < debugLevel.length; i++) {
            if (debugLevel[i].equals(triggerLevel)) {
                return true;
            }
        }
        return false;
    }

    /////////////////////////////////////////////////////////////////
    public static void debugOut(String triggerLevel, int message) {
        debugOut(triggerLevel, String.valueOf(message));
    }

    ///////////////////////////////////////////////////////////////
    /**
    Prints a comment if the triggerLevel is one of the active tags,
    as set by <code>setLevel</code>.
     **/
    public static void debugOut(String triggerLevel, String message) {
        boolean triggered = false;

        for (int i = 0; i < debugLevel.length; i++) {
            if (debugLevel[i].equals(triggerLevel)) {
                triggered = true;
            }

        }

        if (triggered || viewAllErrors) {
            if (quieter) {
                debugOut(message);
            } else {
                debugOut("[" + triggerLevel + "] " + message);
            }
        }
    }

    /////////////////////////////////////////////////////////////////
    /**
    Turns tracing mode on. Tracing mode is the less-powerful version of
    tagged debugging. When using tracing, all debugging statements are
    printed with <code>trace()</code>. These statements can be
    seen if tracing is on.
     **/
    public static void traceOn() {
        traceOn = true;
    }

    /////////////////////////////////////////////////////////////////
    /**
    Turns tracing mode off.
     **/
    public static void traceOff() {
        traceOn = false;
    }

    /////////////////////////////////////////////////////////////////
    /**
    Prints a message to System.out if tracing mode is on. By default,
    tracing mode is off, so most programs run with minimal output.
     **/
    public static void trace(String message) {
        if (traceOn) {
            debugOut("[trace] " + message);
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    If the test is false, prints a message.
     **/
    public static void dassert(boolean test) {
        if (!test) {
            debugOut("[Warning] dassertion failed at " + getCaller());

            checkFatal();
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    If the test is false, prints the message.
     **/
    public static void dassert(boolean test, String message) {
        if (!test) {
            debugOut("[Warning] dassertion failed at " + getCaller()
                    + "\n" + message);
            checkFatal();
        }
    }
    ///////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////
    /**
    If the args is null, prints the message.
     **/
    public static void notNull(Object args, String message) {
        dassert(args != null, message);
    }
    ///////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////
    /**
    If the args is null or empty, prints the message.
     **/
    public static void notNullOrEmpty(String args, String message) {
        dassert(args != null && !args.isEmpty(), message);
    }
    ///////////////////////////////////////////////////////////////////////

    /**
    Returns the name of the method that called the debugger.
    Adapted from ideas by Gregory Rawlins and Dr. Dobb's Journal,
    January 1998.<p>
    
    The stack should look something like:
    
    java.lang.Throwable
    at iglu.util.Debug.getCaller(Debug.java:155)
    at iglu.util.Debug.dassert(Debug.java:139)
    at <<caller>>
    
    so caller will be on the fourth line of the trace, starting at
    the fifth character.
     **/
    private static String getCaller() {
        int MAGIC_LINE = 4;
        int MAGIC_CHAR = 5;

        String caller = "      unknown";

        Throwable err = new Throwable();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        err.printStackTrace(ps);

        String dump = os.toString();
        StringTokenizer tokens = new StringTokenizer(dump, "\n");

        int count = 0;
        while (tokens.hasMoreTokens() && count < MAGIC_LINE) {
            caller = tokens.nextToken();
            count++;
        }

        caller = caller.substring(MAGIC_CHAR - 1);

        return caller;
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    Reports an error, exiting if errors are fatal.
    This is similar to calling
    <code>dassert(false,<i>message</i>)</code>
     **/
    public static void reportError(String message) {
        debugOut("[ERROR] " + message);
        checkFatal();
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    Reports an error, exiting if errors are fatal.
    This is similar to calling
    <code>dassert(false,<i>message</i>)</code>
     **/
    public static void reportError(String module, String message) {
        debugOut(module, "[ERROR in " + module + "] " + message);
        checkFatal();
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    Kills the program if errors have been set to fatal.
     **/
    private static void checkFatal() {
        if (errorsFatal) {
            System.exit(1);
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    Sets the timer
     */
    public static void tic() {
        timeStamp = System.currentTimeMillis();
    }

    ///////////////////////////////////////////////////////////////////////
    /**
    Returns the number of seconds transpired since the last time
    tic() was called.
     */
    public static double toc() {
        return ((double) (System.currentTimeMillis() - timeStamp)) / 1000.0;
    }
}
