package tu.wien.irengine.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Provides utility methods for dealing with Strings.
 */
public class StringTools extends Object {

    /**
     * Prevent instantiation.
     */
    private StringTools() {
    }

    /**
     * Returns true if the lookFor string occurs in the base string.
     */
    public static boolean contains(String base, String lookFor) {
        boolean result = false;

        int index = base.indexOf(lookFor);
        if (index != -1) {
            result = true;
        }

        return result;
    }

    /**
     * Returns true if the given string contains at least one letter.
     */
    public static boolean containsLetter(String look) {
        boolean result = false;

        for (int i = 0; i < look.length(); i++) {
            if (Character.isLetter(look.charAt(i))) {
                result = true;
                i = look.length();
            }
        }

        return result;
    }

    /**
     * Returns the number of words in a string, using
     * <code>IOTools.GENERIC_WORD_DELIMITERS</code> as dividing points between
     * the words.
     */
    public static long countWords(String item) {
        long result = 0;
        StringTokenizer itemIt =
                new StringTokenizer(item, IOTools.GENERIC_WORD_DELIMITERS);

        while (itemIt.hasMoreTokens()) {
            itemIt.nextToken();
            result++;
        }

        return result;
    }

    /**
     * Returns
     * <code>base</code> without
     * <code>remove</code> (only the first occurance is removed). If
     * <code>remove</code> doesn't appear in
     * <code>base</code>, it is returned unchanged.
     */
    public static String removeFirst(String base, String remove) {
        String result;

        int index = base.indexOf(remove);
        if (index != -1) {
            result = base.substring(0, index)
                    + base.substring(index + remove.length());
        } else {
            result = base;
        }

        return result;
    }

    /**
     * Removes duplicate words from the string and also trims whitespace at the
     * beginning and ending of words. The string
     * <code>s</code> is assumed to be a list of terms seperated by commas.
     */
    public static String removeDuplicates(String s) {

        String result = "";
        String temp;

        StringTokenizer st = new StringTokenizer(s, ",");

        // get the first word and remove surrounding whitspace
        if (st.hasMoreTokens()) {
            result = st.nextToken().trim();
        }

        // for all remaining words, remove surrounding whitespace, and if 
        // the word is already in the string, throw it away
        while (st.hasMoreTokens()) {
            temp = st.nextToken().trim();
            if (result.indexOf(temp) == -1) // if item not found
            {
                result += "," + temp;
            }
        }

        return result;
    }

    /**
     * Removes the last character from a string.
     */
    public static String chop(String s) {
        String result = s;

        if ((s != null) && (s.length() > 0)) {
            result = s.substring(0, s.length() - 1);
        }

        return result;
    }

    /**
     * Splits a string into pieces according to the
     * <code>delimiters</code>, and returns the pieces in an array.
     */
    public static String[] split(String s, String delimiters) {

        StringTokenizer sTok = new StringTokenizer(s, delimiters);
        String[] result = new String[sTok.countTokens()];
        int i = 0;

        while (sTok.hasMoreTokens()) {
            result[i] = sTok.nextToken();
            i++;
        }

        return result;
    }

    /**
     * Creates a HashSet from an array of Strings. Particularly useful for
     * dealing with the arguments to a
     * <code>main</code> method.
     */
    public static HashSet toSet(String[] theArray) {
        HashSet theSet = new HashSet();
        theSet.addAll(Arrays.asList(theArray));
        return theSet;
    }

    /**
     * Removes any tags of the form <blah> from the string, and replaces them
     * with spaces.
     */
    public static String removeHtmlTags(String original) {
        String result = "";
        int currentIndex = 0;
        int tagStart = original.indexOf("<");

        while (tagStart >= 0) {
            result += original.substring(currentIndex, tagStart) + " ";
            int tagEnd = original.indexOf(">", tagStart);

            if (tagEnd < 0) {
                break;
            }

            currentIndex = tagEnd + 1;
            tagStart = original.indexOf("<", currentIndex);
        }

        result += original.substring(currentIndex, original.length());
        return result;
    }

    /**
     * Drops all instances of s2 from s1
     *
     * @param s1 Source string
     * @param s2 substring to drop
     */
    public static String dropAll(String s1, String s2) {
        int location = s1.indexOf(s2);
        String breakDown = s1;
        StringBuilder sb = new StringBuilder();
        while (location >= 0) {
            if (location > 0) {
                sb.append(breakDown.substring(0, location));
            }
            breakDown = breakDown.substring(location + s2.length(), breakDown.length());
            location = breakDown.indexOf(s2);
        }
        sb.append(breakDown);
        return sb.toString();
    }

    /**
     * Replace all instances of s2 with s3 in s1
     *
     * @param s1 Source string
     * @param s2 String to replace
     * @param s3 What to replace s2 with.
     */
    public static String replaceAll(String s1, String s2, String s3) {
        int location = s1.indexOf(s2);
        String breakDown = s1;
        StringBuilder sb = new StringBuilder();
        while (location >= 0) {
            if (location > 0) {
                sb.append(breakDown.substring(0, location));
            }
            sb.append(s3);
            breakDown = breakDown.substring(location + s2.length(), breakDown.length());
            location = breakDown.indexOf(s2);
        }
        sb.append(breakDown);
        return sb.toString();
    }

    /**
     * Remove all the punctuation, single characters, and white space from the
     * string, using IOTools.GENERIC_WORD_DELIMITERS as the list of things to
     * drop. Returns a string with all words separated by one space.
     *
     */
    public static String removeDelimiters(String s) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st =
                new StringTokenizer(s, IOTools.GENERIC_WORD_DELIMITERS);

        while (st.hasMoreTokens()) {
            String candidate = st.nextToken();
            if (candidate.length() > 1) {
                sb.append(candidate.toLowerCase()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Remove all the numbers from the string, using IOTools.NUMBERS as the list
     * of things to drop.
     *
     */
    public static String removeNumbers(String s) {
        String[] numbers = toArray(IOTools.NUMBERS);
        for (String number : numbers) {
            s = replaceAll(s, number, "");
        }

        return s;
    }
    
    /**
     * Converts string to array of single chars
     * 
     * @param s
     * @return 
     */
    public static String[] toArray(String s) {
        return s.split("(?!^)");
    }

    public static String whiteSpaceToSpaces(String s) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s, IOTools.WHITE_SPACE_CHARACTERS);
        while (st.hasMoreTokens()) {
            String candidate = st.nextToken();
            if (candidate.length() > 1) {
                sb.append(candidate.toLowerCase()).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * returns the string with the characters reversed
     */
    public static String reverse(String st) {
        StringBuilder sb = new StringBuilder();
        char chars[] = st.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Takes an array of strings and concatenates them together, separated by
     * <code>sep</code>
     *
     * @param array a <code>String[]</code> value
     * @param sep a <code>char</code> value
     * @return a <code>String</code> value
     */
    public static String concat(String[] array, char sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < (array.length - 1)) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }

    /**
     * Converts an Object array to a String array.
     *
     */
    public static String[] makeStringArray(Object[] a) {
        String[] result = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (String) a[i];
        }

        return result;
    }
}
