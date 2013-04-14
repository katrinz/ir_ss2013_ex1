package tu.wien.irengine.vectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.utils.Debug;

public class TFIDFVectorCreator extends AbstractVectorCreator {

    protected ITermVector docOccurs = new TermVector();
    protected int highestRank = -1;
    protected int numDocs = 0;
    protected List<IDocumentFilter> filters = new ArrayList<IDocumentFilter>();

    public TFIDFVectorCreator() {
    }

    public TFIDFVectorCreator(List<IDocumentFilter> filters) {
        this.filters.addAll(filters);
    }

    public void setHighestRank(int highestRank) {
        this.highestRank = highestRank;
    }

    public ITermVector getDocOccurs() {
        return docOccurs;
    }

    public void setNumDocs(int n) {
        numDocs = n;
    }

    public int getNumDocs() {
        return numDocs;
    }

    @Override
    public void addDoc(IDocument d) {
        Debug.notNull(d, "document");

        ITermVector temp = this.getFrequencyTermVector(d);
        addDoc(temp);
    }

    @Override
    public void addDoc(ITermVector freqVec) {
        ITermVector temp = new TermVector();
        for (String term : freqVec.termSet()) {
            temp.put(term, 1.0);
        }
        docOccurs.putAll(temp);
        numDocs++;
    }

    @Override
    public void addTermSet(Collection<ITermVector> ds) {
        Debug.notNull(ds, "docSet");

        for (ITermVector tv : ds) {
            addDoc(tv);
        }
    }

    @Override
    public void addDocSet(Collection<IDocument> ds) {
        Debug.notNull(ds, "docSet");

        for (IDocument doc : ds) {
            addDoc(doc);
        }
    }

    protected String getFilteredContent(IDocument d) {
        for (IDocumentFilter filter : filters) {
            filter.applyTo(d);
        }
        return d.getContent();
    }

    protected ITermVector getFrequencyTermVector(IDocument d) {

        ITermVector tv = new TermVector();
        String filteredContent = this.getFilteredContent(d);
        StringTokenizer st = new StringTokenizer(filteredContent);

        // count term frequency (tf)
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            Double value = 1.0 + tv.get(s);
            tv.put(s, value);
        }

        return tv;
    }

    @Override
    public ITermVector createVector(IDocument d) {
        ITermVector temp = this.getFrequencyTermVector(d);
        return getVector(temp);
    }

    protected ITermVector getVector(ITermVector freqVec) {
        Debug.dassert(numDocs > 0, 
                String.format("%s must have documents added", this.getClass().getSimpleName()));

        ITermVector temp = new TermVector();
        ITermVector topRanked =
                (highestRank > 0) ? docOccurs.topN(highestRank) : docOccurs;

        for (String term : freqVec.termSet()) {
            double docFreq = topRanked.get(term);
            if (docFreq > 0) {
                double value = computeMeasure(term, docFreq, freqVec);
                temp.put(term, value);
            }
        }

        cleanUp(temp);
        return temp;
    }

    protected double computeMeasure(String term, double docFreq, ITermVector freqVec) {
        return freqVec.get(term) * (Math.log(numDocs / docFreq) / Math.log(2));
    }

    @Override
    public String toString() {
        return "TFIDF with doc frequency vector: " + docOccurs.toString();
    }
}
