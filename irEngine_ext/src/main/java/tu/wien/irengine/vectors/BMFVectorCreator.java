/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.vectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.model.Document;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.StringTools;

/**
 *
 * @author katie
 */
public class BMFVectorCreator extends BMVectorCreator {

    private static final String defaultStream = "body";
    private static final String streamNameDelimiter = ":";
    protected Map<String, Double> streamWeigths = new HashMap<String, Double>();
    private double streamAvdl;

    public BMFVectorCreator() {
        super();
    }

    public BMFVectorCreator(List<IDocumentFilter> filters) {
        super(filters);
    }

    public BMFVectorCreator(List<IDocumentFilter> filters, double k, double b) {
        super(filters, k, b);
    }

    public BMFVectorCreator(List<IDocumentFilter> filters, double k, double b, String weights) {
        super(filters, k, b);

        initWeights(weights);
    }

    /**
     * Inits stream weights
     *
     * @param weights
     */
    private void initWeights(String weights) {
        for (String s : StringTools.split(weights, ";")) {
            try {
                String[] parameters = StringTools.split(s, ":");
                String streamName = parameters[0];
                Double weight = Double.parseDouble(parameters[1]);
                streamWeigths.put(streamName, weight);
                Debug.debugOut(String.format(
                        "Stream '%s' with weight %f added to BMF creator", streamName, weight));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(ISearchIndex index) {
        super.init(index);

        for (Serializable docId : index.documentSet()) {
            updateAVDL(index.getDL(docId));
        }
    }

    /**
     * Computes average document stream lengths
     *
     * @param d
     * @return
     */
    @Override
    public double getDocDL(IDocument d) {
        Double sum = 0.0;
        
        Map<String, String> streamMap = getStreams(d);
        for (Entry<String, String> entry : streamMap.entrySet()) {
            String streamName = entry.getKey();
            Double w = streamWeigths.get(streamName);
            if (w == null) {
                w = 1.0;
            }
            sum += w * getTermVectorForStream(streamName, entry.getValue()).size();
        }
        return sum / streamMap.size();
    }

    @Override
    protected ITermVector getFrequencyTermVector(IDocument d) {

        ITermVector docTermVector = new TermVector();
        List<ITermVector> streamTermVectors = new ArrayList<ITermVector>();

        for (Entry<String, String> entry : getStreams(d).entrySet()) {
            String streamName = entry.getKey();
            ITermVector tv = getTermVectorForStream(streamName, entry.getValue());
            if (!streamName.equalsIgnoreCase(defaultStream)) {
                Double w = streamWeigths.get(streamName);
                if (w != null) {
                    tv.scaleBy(w);
                }
            }

            streamTermVectors.add(tv);
        }

        for (ITermVector tv : streamTermVectors) {
            docTermVector.putAll(tv);
        }

        return docTermVector;
    }

    @Override
    public void addDoc(IDocument d) {
        updateAVDL(getDocDL(d));
        super.addDoc(d);
    }

    @Override
    public ITermVector createVector(IDocument d) {
        ITermVector temp = this.getFrequencyTermVector(d);
        return getBMFVector(temp, getDocDL(d));
    }

    private ITermVector getTermVectorForStream(String streamName, String streamContent) {
        IDocument sourceDoc = new Document(streamName, streamContent);
        return super.getFrequencyTermVector(sourceDoc);
    }

    protected double computeBMFMeasure(String term, double docFreq, ITermVector freqVec, double docDL) {
        double tf = freqVec.get(term);
        double customtf = tf / (parameterK * (1 - parameterB + parameterB * docDL / streamAvdl));
        double idf = Math.log((numDocs - docFreq + 0.5) / (docFreq + 0.5)) / Math.log(2);
        return customtf * idf;
    }

    @Override
    public String toString() {
        return String.format("BMF [k1 = %f, b = %f] with doc frequency vector: %d",
                parameterK, parameterB, docOccurs.toString());
    }

    /**
     * Separates document content to streams and returns map that for each
     * stream name stores its content.
     *
     * @param d Document
     * @return Map that for each stream name stores its content
     *
     */
    private Map<String, String> getStreams(IDocument d) {

        Map<String, String> streamMap = new HashMap<String, String>();

        String docContent = d.getContent();
        StringTokenizer st = new StringTokenizer(docContent, "\r\n");

        // count term frequency (tf)
        while (st.hasMoreTokens()) {

            String stream = defaultStream;

            String s = st.nextToken();

            // try to get stream name from line
            StringTokenizer stStreamNames = new StringTokenizer(s, streamNameDelimiter);
            if (stStreamNames.hasMoreElements()) {
                String streamName = stStreamNames.nextToken();
                // name should have 1 word. ':' should follow the name
                if (StringTools.countWords(streamName) == 1) {
                    stream = streamName;
                    s = StringTools.removeFirst(s, stream.concat(streamNameDelimiter));
                    stream = stream.toLowerCase();
                }
            }

            String content = streamMap.get(stream);
            if (content == null) {
                content = "";
            }

            content = content.concat(s);
            streamMap.put(stream, content);
        }

        return streamMap;
    }

    /**
     * Computes document term vector regarding index based on doc freq vector
     * and document average average stream length.
     *
     * @param freqVec document frequency vector
     * @param docDL document avg stream length
     * @return
     */
    protected ITermVector getBMFVector(ITermVector freqVec, Double docDL) {
        Debug.dassert(numDocs > 0,
                String.format("%s must have documents added", this.getClass().getSimpleName()));

        ITermVector temp = new TermVector();
        ITermVector topRanked =
                (highestRank > 0) ? docOccurs.topN(highestRank) : docOccurs;

        for (String term : freqVec.termSet()) {
            double docFreq = topRanked.get(term);
            if (docFreq > 0) {
                double value = computeBMFMeasure(term, docFreq, freqVec, docDL);
                temp.put(term, value);
            }
        }

        cleanUp(temp);
        return temp;
    }

    /**
     * Updates AVDL for creator
     *
     * @param dl
     */
    private void updateAVDL(double dl) {
        streamAvdl = (streamAvdl * numDocs + dl) / (numDocs + 1);
    }
}
