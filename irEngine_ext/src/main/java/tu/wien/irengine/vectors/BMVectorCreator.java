/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.vectors;

import java.util.Collection;
import java.util.List;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.utils.Debug;

/**
 * BM25 is a bag-of-words retrieval function that ranks a set of documents based
 * on the query terms appearing in each document, regardless of the
 * inter-relationship between the query terms within a document (e.g., their
 * relative proximity) (wiki: http://en.wikipedia.org/wiki/Okapi_BM25).
 *
 */
public class BMVectorCreator extends TFIDFVectorCreator {

    protected double parameterK = 2.0;
    protected double parameterB = 0.75;
    private double avdl;

    public BMVectorCreator() {
        super();
    }

    public BMVectorCreator(List<IDocumentFilter> filters) {
        super(filters);
    }

    public BMVectorCreator(List<IDocumentFilter> filters, double k, double b) {
        super(filters);
        this.parameterK = k;
        this.parameterB = b;
    }

    @Override
    protected void addDoc(ITermVector freqVec) {
        double dl = freqVec.size();
        avdl = (avdl * numDocs + dl) / (numDocs + 1);
        super.addDoc(freqVec);
    }

    @Override
    protected double computeMeasure(String term, double docFreq, ITermVector freqVec) {
        double tf = freqVec.get(term);
        double dl = freqVec.size();
        double customtf = tf * (parameterK + 1) / (tf + parameterK * (1 - parameterB + parameterB * dl / avdl));
        double idf = Math.log((numDocs - docFreq + 0.5) / (docFreq + 0.5)) / Math.log(2);
        return customtf * idf;
    }

    @Override
    public String toString() {
        return String.format("BM [k1 = %f, b = %f] with doc frequency vector: %d",
                parameterK, parameterB, docOccurs.toString());
    }
}
