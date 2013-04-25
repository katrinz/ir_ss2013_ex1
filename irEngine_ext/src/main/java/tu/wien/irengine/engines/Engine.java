/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.engines;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.IEngine;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.contract.ITermVectorCreator;
import tu.wien.irengine.contract.ITermVectorEvaluator;
import tu.wien.irengine.utils.CollectionsTools;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.IOTools;

/**
 *
 */
public class Engine implements IEngine {

    private EngineConfiguration configuration;
    private ISearchIndex index;

    public Engine(EngineConfiguration configuration) {
        Debug.notNull(configuration, "configuration");

        this.configuration = configuration;
    }

    public SortedSet<Entry<Serializable, Double>> findBy(IDocument document) {

        Map<Serializable, Double> res = new HashMap<Serializable, Double>();

        // create term vector
        ITermVectorCreator vectorCreator = configuration.getVectorCreator();
        ITermVector termVector = vectorCreator.createVector(document);

        // get list of all document that can have term from defined doc
        Set<Serializable> docs = index.getDocumentsByTerms(termVector);
        
        // remove current document if it exists in the list
        docs.remove(document.getId());
        
        // get evaluator
        ITermVectorEvaluator evaluator = configuration.getEvaluator();

        // calculate vectors
        for (Serializable docId : docs) {
            ITermVector tv = index.getTermVector(docId);
            Double value = evaluator.evaluate(termVector, tv);
            res.put(docId, value);
        }

        return CollectionsTools.orderByValuesDesc(res);
    }

    public SortedSet<Entry<Serializable, Double>> findBy(String term) {
        return CollectionsTools.orderByValuesDesc(index.getDocuments(term));
    }

    public void init() {
        if (indexExists()) {
            loadIndex();
            loadTermVectorCreator();
        } else {
            createIndex();
        }
    }

    private void loadIndex() {
        index = this.configuration.getLoader().load(this.configuration.getIndexClass(),
                this.configuration.getIndexLocation(), this.configuration.isUsegzip());
    }
    
    private void loadTermVectorCreator(){
        Debug.debugOut("Loading term vector creator...");
        this.configuration.getVectorCreator().init(
                this.index);
    }

    public void createIndex() {
        index = this.configuration.getIndexCreator().create(this.configuration.getIndexClass(),
                this.configuration.getDocSetLocation());

        this.configuration.getSaver().save(index,
                this.configuration.getIndexLocation(), this.configuration.isUsegzip());
    }

    private boolean indexExists() {
        return IOTools.fileExists(this.configuration.getIndexLocation());
    }
}
