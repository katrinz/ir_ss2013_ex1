/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.indices;

import java.util.List;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ISearchIndexCreator;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.contract.ITermVectorCreator;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.DocumentTools;

/**
 *
 */
public class InvertedIndexCreator implements ISearchIndexCreator {

    private ITermVectorCreator creator;

    public InvertedIndexCreator(ITermVectorCreator creator) {
        Debug.notNull(creator, "term vector creator");

        this.creator = creator;
    }
    
    public ITermVectorCreator getTermVectorCreator(){
        return creator;
    }

    public <T extends ISearchIndex> T create(Class<T> clazz, String src) {
        Debug.notNullOrEmpty(src, "src");

        try {
            Debug.debugOut(String.format("Creating index from %s...", src));

            // create new instance of index
            T index = clazz.newInstance();

            Debug.debugOut(String.format("Reading of documents...", src));

            List<String> pathList = DocumentTools.getAllDocumentPaths(src);

            Debug.debugOut(String.format("%d documents have been found", pathList.size()));

            for (String path : pathList) {
                IDocument doc = DocumentTools.createDocument(path);
                Debug.debugOut(String.format("Reading document %s", doc.getId().toString()));
                creator.addDoc(doc);
            }

            for (String path : pathList) {
                IDocument doc = DocumentTools.createDocument(path);
                Debug.debugOut(String.format("Calculating tf-idf for document %s", doc.getId().toString()));
                ITermVector tv = creator.createVector(doc);
                index.put(doc.getId(), tv);
            }

            Debug.debugOut("Creation complete");

            return index;
        } catch (Exception e) {
            Debug.setFatal();
            Debug.reportError(e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
