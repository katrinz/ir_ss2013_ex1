package tu.wien.irengine.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileReader;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Collection;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.utils.Debug;

/**
 * Implementation of a simple stoplist.
 */
public class StopList implements IDocumentFilter {

    private HashSet wordSet = new HashSet();

    public StopList(InputStream is) {
        initializeFromReader(new InputStreamReader(is));
    }

    public StopList(File list) {
        Debug.debugOut("StopList", "Opening stoplist in " + list);
        wordSet = new HashSet();

        try {
            initializeFromReader(new FileReader(list));
        } catch (Exception e) {
            Debug.reportError("Cannot create FileReader for " + list);
        }
    }

    public StopList(Collection c) {
        Object items[] = c.toArray();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                wordSet.add(items[i]);
            }
        }
    }

    private void initializeFromReader(Reader r) {
        try {
            BufferedReader theReader = new BufferedReader(r);
            String thisWord = theReader.readLine();

            while (thisWord != null) {
                wordSet.add(thisWord);
                thisWord = theReader.readLine();
            }
        } catch (IOException e) {
            Debug.reportError("Error reading stoplist " + e);
        }
    }

    public boolean contains(String word) {
        return wordSet.contains(word);
    }

    /**
     * Returns the string with the stop words dropped out.
     *
     * @param string a String containing words. This class assumes that the
     * punctuation has already been dropped and that the words are separated by
     * spaces.
     * @return the same string with the same words in the same order except that
     * the stop words are dropped.
     */
    public String processText(String string) {
        StringTokenizer st = new StringTokenizer(string);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String s = st.nextToken().toLowerCase();
            if (!contains(s)) {
                sb.append(s);
                if (st.hasMoreTokens()) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();

    }

    /**
     * Returns the list of stopwords.
     *
     */
    public HashSet getList() {
        return wordSet;
    }

    /**
     * Filters all stopwords out of a a document's indexible content. Implements
     * the DocumentFilter method.
     *
     * @param d a <code>Document</code> value
     *
     */
    @Override
    public void applyTo(IDocument d) {
        String s = d.getContent();
        d.setContent(processText(s));
    }
}
