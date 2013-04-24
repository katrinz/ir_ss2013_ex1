/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface ITermVector extends Cloneable, Serializable {

    void clear();

    Object clone();
    
    double avgLength();

    double get(String term);

    Set<Map.Entry<String, Double>> getAllSortedTerms();

    Map<String, Double> getAllTerms();

    void increment(String term);

    void normalize();

    void put(String term, double value);

    void putAll(ITermVector additional);

    void scaleBy(double n);

    void setAllTerms(Map<String, Double> allTerms);

    int size();

    void subtract(ITermVector subWords);

    void subtract(String term);

    Set<String> termSet();

    @Override
    String toString();

    ITermVector topN(int n);

    void truncateTo(int numTerms);
    
    void limit(double lowerBound, double upperBound);
    
}
