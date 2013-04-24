/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface ISearchIndex {

    boolean containsDocument(Serializable docId);

    Set<Serializable> documentSet();

    Map<Serializable, Double> getDocuments(String term);
    
    Collection<ITermVector> getAllTermVectors();

    Set<Serializable> getDocumentsByTerms(ITermVector tv);

    ITermVector getTermVector(Serializable docId);

    void limit(double lowerBound, double upperBound);

    void put(Serializable docId, ITermVector termVector);

    int size();

    Set<String> termSet();

    int termsCount();
}
