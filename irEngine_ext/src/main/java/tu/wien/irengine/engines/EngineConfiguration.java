/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.engines;

import java.util.List;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ISearchIndexCreator;
import tu.wien.irengine.contract.ISearchIndexLoader;
import tu.wien.irengine.contract.ISearchIndexSaver;
import tu.wien.irengine.contract.ITermVectorCreator;
import tu.wien.irengine.contract.ITermVectorEvaluator;
import tu.wien.irengine.utils.Debug;

/**
 *
 */
public class EngineConfiguration {

    private String docSetLocation;
    private String indexLocation;
    private ISearchIndexCreator indexCreator;
    private ITermVectorCreator vectorCreator;
    private ITermVectorEvaluator evaluator;
    private List<IDocumentFilter> documentFilters;
    private ISearchIndexSaver saver;
    private ISearchIndexLoader loader;
    private boolean usegzip;
    private Class<? extends ISearchIndex> indexClass;

    public String getDocSetLocation() {
        return docSetLocation;
    }

    public void setDocSetLocation(String docSetLocation) {
        Debug.notNullOrEmpty(docSetLocation, "docSetLocation");
        Debug.debugOut(String.format("docSetLocation = %s", docSetLocation));
        this.docSetLocation = docSetLocation;
    }

    public String getIndexLocation() {
        return indexLocation;
    }

    public void setIndexLocation(String indexLocation) {
        Debug.notNullOrEmpty(indexLocation, "indexLocation");
        Debug.debugOut(String.format("indexLocation = %s", indexLocation));
        this.indexLocation = indexLocation;
    }

    public ISearchIndexCreator getIndexCreator() {
        return indexCreator;
    }

    public void setIndexCreator(ISearchIndexCreator indexCreator) {
        Debug.notNull(indexCreator, "indexCreator");
        Debug.debugOut(String.format("indexCreator class = %s", indexCreator.getClass().getName()));
        this.indexCreator = indexCreator;
    }

    public ITermVectorCreator getVectorCreator() {
        return vectorCreator;
    }

    public void setVectorCreator(ITermVectorCreator vectorCreator) {
        Debug.notNull(vectorCreator, "vectorCreator");
        Debug.debugOut(String.format("vectorCreator class = %s", vectorCreator.getClass().getName()));
        this.vectorCreator = vectorCreator;
    }

    public ITermVectorEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ITermVectorEvaluator evaluator) {
        Debug.notNull(evaluator, "evaluator");
        Debug.debugOut(String.format("evaluator class = %s", evaluator.getClass().getName()));
        this.evaluator = evaluator;
    }

    public List<IDocumentFilter> getDocumentFilters() {
        return documentFilters;
    }

    public void setDocumentFilters(List<IDocumentFilter> documentFilters) {
        Debug.notNull(documentFilters, "documentFilters");
        Debug.debugOut("Filters:");
        for (IDocumentFilter filter : documentFilters) {
            Debug.notNull(filter, "filter");
            Debug.debugOut(String.format("fitler class = %s", filter.getClass().getName()));
        }
        this.documentFilters = documentFilters;
    }

    public ISearchIndexSaver getSaver() {
        return saver;
    }

    public void setSaver(ISearchIndexSaver saver) {
        Debug.notNull(saver, "saver");
        Debug.debugOut(String.format("saver class = %s", saver.getClass().getName()));
        this.saver = saver;
    }

    public ISearchIndexLoader getLoader() {
        return loader;
    }

    public void setLoader(ISearchIndexLoader loader) {
        Debug.notNull(loader, "loader");
        Debug.debugOut(String.format("loader class = %s", loader.getClass().getName()));
        this.loader = loader;
    }

    public boolean isUsegzip() {
        return usegzip;
    }

    public void setUsegzip(boolean usegzip) {
        Debug.debugOut(String.format("usegzip = %s", String.valueOf(usegzip)));
        this.usegzip = usegzip;
    }

    public Class<? extends ISearchIndex> getIndexClass() {
        return indexClass;
    }

    public void setIndexClass(Class<? extends ISearchIndex> indexClass) {
        Debug.debugOut(String.format("loader class = %s", indexClass.getName()));
        this.indexClass = indexClass;
    }
}
