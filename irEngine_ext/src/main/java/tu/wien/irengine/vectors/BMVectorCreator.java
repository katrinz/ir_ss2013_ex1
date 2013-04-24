/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.vectors;

import java.util.List;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.ITermVector;

/**
 * BM25 is a bag-of-words retrieval function that ranks a set of documents based
 * on the query terms appearing in each document, regardless of the
 * inter-relationship between the query terms within a document (e.g., their
 * relative proximity) (wiki: http://en.wikipedia.org/wiki/Okapi_BM25).
 *
 */
public class BMVectorCreator extends TFIDFVectorCreator {

    private double parameterK = 2.0;
    private double parameterB = 0.75;

    public BMVectorCreator() {
        super();
    }

    public BMVectorCreator(List<IDocumentFilter> filters) {
        super();
        this.filters.addAll(filters);
    }

    public BMVectorCreator(List<IDocumentFilter> filters, double k, double b) {
        super();
        this.filters.addAll(filters);
        this.parameterK = k;
        this.parameterB = b;
    }

    @Override
    protected double computeMeasure(String term, double docFreq, ITermVector freqVec) {
        double tf = freqVec.get(term);
        double avgdl = freqVec.avgLength();
        double customtf = tf * (parameterK + 1) / (tf + parameterK * (1 - parameterB + parameterB * numDocs / avgdl));
        double idf = Math.log((numDocs - docFreq + 0.5) / (docFreq + 0.5));
        return customtf * idf;
    }

    @Override
    public String toString() {
        return String.format("BM [k1 = %f, b = %f] with doc frequency vector: %d",
                parameterK, parameterB, docOccurs.toString());
    }
}
