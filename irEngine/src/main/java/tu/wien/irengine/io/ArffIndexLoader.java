/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.io;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ISearchIndexLoader;
import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.vectors.TermVector;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.IOTools;
import weka.core.Instance;
import weka.core.converters.ArffLoader.ArffReader;

/**
 *
 */
public class ArffIndexLoader implements ISearchIndexLoader {

    public <T extends ISearchIndex> T load(Class<T> clazz, String src, boolean usegzip) {

        try {
            Debug.debugOut(String.format("Loading index from %s...", src));

            // create new instance of index
            T index = clazz.newInstance();

            // get input reader
            BufferedReader fileReader = null;

            if (usegzip) {
                fileReader = IOTools.getGZIPFileReader(src);
            } else {
                fileReader = IOTools.getFileReader(src);
            }

            // create reader
            ArffReader reader = new ArffReader(fileReader);

            // create instance iterator
            Enumeration dataIt = reader.getData().enumerateInstances();

            // define doc vector
            Map<Serializable, ITermVector> docVec = new HashMap<Serializable, ITermVector>();

            while (dataIt.hasMoreElements()) {
                // get instance
                Instance inst = (Instance) dataIt.nextElement();
                // get values
                String thisTerm = inst.stringValue(0);
                String thisDoc = inst.stringValue(1);
                Double termValue = inst.value(2);

                ITermVector tv = docVec.get(thisDoc);
                if (tv == null) {
                    tv = new TermVector();
                }
                tv.put(thisTerm, termValue);
                docVec.put(thisDoc, tv);
            }

            // put doc vec into index
            for (Entry<Serializable, ITermVector> entry : docVec.entrySet()) {
                index.put(entry.getKey(), entry.getValue());
            }

            Debug.debugOut("Initialization complete");

            return index;
        } catch (Exception e) {
            Debug.setFatal();
            Debug.reportError(e.toString());
        }

        return null;
    }
}
