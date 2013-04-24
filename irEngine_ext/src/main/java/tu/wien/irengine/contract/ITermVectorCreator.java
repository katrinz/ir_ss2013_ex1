/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

import java.util.Collection;

/**
 *
 */
public interface ITermVectorCreator {

    ITermVector createVector(IDocument doc);

    void addDocSet(Collection<IDocument> ds);
    
    void addTermSet(Collection<ITermVector> ds);

    void addDoc(ITermVector freqVec);

    void addDoc(IDocument doc);

    void setBounds(double lower, double upper);

    void setNormalize(boolean normalize);

    void setMaxSize(int n);

    void setHighestRank(int rank);
}
