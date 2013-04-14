package tu.wien.irengine.vectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.utils.CollectionsTools;
import tu.wien.irengine.utils.Debug;

public class TermVector implements ITermVector {

    private Map<String, Double> allTerms;

    public TermVector() {
//        allTerms = Collections.synchronizedMap(new HashMap< String, Double>());
        allTerms = new HashMap< String, Double>();
    }

    @Override
    public Map<String, Double> getAllTerms() {
        return allTerms;
    }

    @Override
    public void setAllTerms(Map<String, Double> allTerms) {
        this.allTerms = allTerms;
    }

    @Override
    public Set<Map.Entry<String, Double>> getAllSortedTerms() {
        return CollectionsTools.orderByValuesDesc(allTerms);
    }

    @Override
    public void clear() {
        allTerms.clear();
    }

    /**
     * Returns the number of terms.
     *
     */
    @Override
    public int size() {
        return allTerms.size();
    }

    /**
     * Returns average length of terms.
     * 
     * @return 
     */
    @Override
    public double avgLength() {
        int sum = 0;
        for (String term : termSet()) {
            sum += term.length();
        }
        double res = (double) sum / termSet().size();
        
        return res;
    }

    /**
     * Associates
     * <code>value</code> with
     * <code>term</code> in the vector.
     *
     */
    @Override
    public void put(String term, double value) {
        Debug.notNullOrEmpty(term, "term");

        if (value == 0) {
            allTerms.remove(term);
        } else {
            allTerms.put(term, value);
        }
    }

    /**
     * Adds the contents of another term vetor to this one. If the vectors both
     * contain nonzero values for the same term, the values are added together.
     *
     */
    @Override
    public void putAll(ITermVector additional) {
        Debug.notNull(additional, "additional");

        for (String term : additional.termSet()) {
            double newValue = additional.get(term) + get(term);
            put(term, newValue);
        }
    }

    /**
     * Adds one to the value of a term.
     *
     */
    @Override
    public void increment(String term) {
        Debug.notNull(term, "term");

        allTerms.put(term, allTerms.get(term) + 1.0);
    }

    /**
     * Returns the value associated with
     * <code>term</code>. Terms that haven't had explicit values set will return
     * a value of zero.
     *
     */
    @Override
    public double get(String term) {
        Debug.notNullOrEmpty(term, "term");

        if (allTerms.containsKey(term)) {
            return allTerms.get(term);
        }

        return 0.0;
    }

    /**
     * Normalizes the vector. The resulting vector will have a (Euclidean)
     * length of 1.
     *
     */
    @Override
    public void normalize() {
        // find the vector's length
        double squaredWeightSum = 0.0;
        Collection<Double> values = this.getAllTerms().values();
        for (Double value : values) {
            squaredWeightSum += value * value;
        }
        double vectorLength = Math.sqrt(squaredWeightSum);

        for (Entry<String, Double> item : this.getAllTerms().entrySet()) {
            put(item.getKey(), item.getValue() / vectorLength);
        }
    }

    /**
     * Scales all terms of the vector by the given value.
     *
     */
    @Override
    public void scaleBy(double n) {
        for (Entry<String, Double> item : this.getAllTerms().entrySet()) {
            put(item.getKey(), item.getValue() * n);
        }
    }

    /**
     * Performs a set difference on the list of terms.
     *
     */
    @Override
    public void subtract(ITermVector subWords) {
        Debug.notNull(subWords, "subWords");

        for (String term : subWords.termSet()) {
            this.subtract(term);
        }
    }

    /**
     * Removes a single term from the list of terms.
     *
     */
    @Override
    public void subtract(String term) {
        Debug.notNullOrEmpty(term, "term");

        allTerms.remove(term);
    }

    @Override
    public Set<String> termSet() {
        return allTerms.keySet();
    }

    /**
     * Truncates this term vector to the given length. The top
     * <code>numTerms</code> terms, ordered by value, are kept.
     *
     */
    @Override
    public void truncateTo(int numTerms) {

        Set<Map.Entry<String, Double>> terms = this.getAllSortedTerms();

        this.clear();

        for (Entry<String, Double> entry : CollectionsTools.take(terms, numTerms)) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set boundaries for term vector.
     *
     * All terms between
     * <code>lowerBound</code> and
     * <code>upperBound</code> are kept.
     *
     * @param lowerBound
     * @param upperBound
     *
     */
    public void limit(double lowerBound, double upperBound) {
        Set<Map.Entry<String, Double>> terms = this.getAllSortedTerms();

        this.clear();

        for (Entry<String, Double> entry : terms) {
            double freq = entry.getValue();

            if (freq > upperBound) {
                continue;
            } else if (freq < lowerBound) {
                break;
            }

            String term = entry.getKey();
            this.put(term, freq);
        }
    }

    /**
     * Creates and returns a copy of this object.
     *
     */
    @Override
    public Object clone() {
        ITermVector result = new TermVector();
        result.putAll(this);

        return result;
    }

    /**
     * Returns a string representation of the vector. Only the terms with
     * non-zero values are shown.
     *
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("[");

        for (Entry<String, Double> entry : this.getAllSortedTerms()) {
            result.append(String.format("<%s,%d>\r\n", entry.getKey(), entry.getValue()));
        }

        result.append("]");

        return result.toString();
    }

    /**
     * Returns a new (clone) ITermVector containing the top n words in the
     * ITermVector, along with their values.
     */
    @Override
    public ITermVector topN(int n) {
        ITermVector tv = (ITermVector) this.clone();
        tv.truncateTo(n);
        return tv;
    }
}
