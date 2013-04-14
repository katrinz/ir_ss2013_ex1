package tu.wien.irengine.indices;

import tu.wien.irengine.contract.ISearchIndex;
import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.vectors.TermVector;
import tu.wien.irengine.utils.Debug;

/**
 * Computes and stores an inverted index, which can be used by a search engine.
 *
 */
public class InvertedIndex extends Object implements ISearchIndex {

    private TreeMap<String, HashMap<Serializable, Double>> invertedVectors;
    private TreeMap<Serializable, ITermVector> forwardVectors;  // stores normal TermVectors for each docID

    public InvertedIndex() {
        invertedVectors = new TreeMap<String, HashMap<Serializable, Double>>();
        forwardVectors = new TreeMap<Serializable, ITermVector>();
    }
    
    @Override
    public void put(Serializable docId, ITermVector termVector) {
        Debug.notNull(docId, "null ID");
        Debug.notNull(termVector, "null vector");

        forwardVectors.put(docId, termVector);

        // for each term
        for (String term : termVector.termSet()) {
            HashMap<Serializable, Double> docVector = this.invertedVectors.get(term);

            if (docVector == null) {
                docVector = new HashMap<Serializable, Double>();
            }

            // add this doc to the term's index
            docVector.put(docId, termVector.get(term));

            invertedVectors.put(term, docVector);
        }
    }

    @Override
    public ITermVector getTermVector(Serializable docId) {
        Debug.notNull(docId != null, "null ID");
        return (ITermVector) forwardVectors.get(docId);
    }
    
    @Override
    public Collection<ITermVector> getAllTermVectors(){
        return forwardVectors.values();
    }

    @Override
    public Map<Serializable, Double> getDocuments(String term) {
        Debug.notNullOrEmpty(term, "term");

        Map<Serializable, Double> result = invertedVectors.get(term);

        if (result == null) {
            result = new HashMap<Serializable, Double>();
        }

        return result;
    }

    @Override
    public Set<Serializable> getDocumentsByTerms(ITermVector tv) {
        Debug.notNull(tv, "termVector");

        Set<Serializable> res = new HashSet<Serializable>();
        for (String term : tv.termSet()) {
            for (Entry<Serializable, Double> entry : this.invertedVectors.get(term).entrySet()) {
                res.add(entry.getKey());
            }
        }
        return res;
    }

    @Override
    public boolean containsDocument(Serializable docId) {
        return forwardVectors.containsKey(docId);
    }

    @Override
    public Set<Serializable> documentSet() {
        return forwardVectors.keySet();
    }

    @Override
    public Set<String> termSet() {
        return invertedVectors.keySet();
    }

    @Override
    public int size() {
        return forwardVectors.size();
    }

    /**
     * @return the number of terms in this index.
     */
    @Override
    public int termsCount() {
        return invertedVectors.size();
    }

    /**
     * Throws from index items which frequency isn't in boundaries [
     * <code>lowerBound</code>,
     * <code>upperBound</code>].
     *
     * Requeries for successful execution of this method are:
     * <code>lowerBound</code> should be in [0;1].
     * <code>upperBound</code> should be in [0;1].
     * <code>lowerBound</code> < <code>upperBound</code>.
     *
     * @param lowerBound is lower boundary of term frequency.
     * @param upperBound is lower boundary of term frequency
     */
    @Override
    public void limit(double lowerBound, double upperBound) {
        Debug.dassert(lowerBound >= 0 && lowerBound <= 1, "lowerBound is out of range [0;1].");
        Debug.dassert(upperBound >= 0 && upperBound <= 1, "upperBound is out of range [0;1].");
        Debug.dassert(upperBound > lowerBound, "upperBound is less than lowerBound.");

        Debug.debugOut(String.format("Index is limited by frequency...",
                lowerBound, upperBound));

        // get index items iterator 
        Iterator termIt = ((TreeMap<String, HashMap<Serializable, Double>>) invertedVectors.clone()).keySet().iterator();
        while (termIt.hasNext()) {

            // get independent term
            String term = (String) termIt.next();

            // get docVec
            HashMap<Serializable, Double> docVec = invertedVectors.get(term);

            for (Entry<Serializable, Double> entry : docVec.entrySet()) {

                Serializable doc = entry.getKey();
                Double freq = entry.getValue();

                if (freq < lowerBound || freq > upperBound) {

                    // delete term from invertedvectors
                    invertedVectors.remove(term);

                    // delete term from frowardvectors
                    ITermVector tv = forwardVectors.get(doc);
                    tv.subtract(term);
                    forwardVectors.put(doc, tv);
                }
            }
        }

        Debug.debugOut(String.format("Index is limited successfully."));
    }
}
