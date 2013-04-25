/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map.Entry;
import tu.wien.irengine.contract.ISearchIndex;
import tu.wien.irengine.contract.ISearchIndexSaver;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.Debug;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractSaver;
import weka.core.converters.ArffSaver;

/**
 *
 */
public class ArffIndexSaver implements ISearchIndexSaver {

    public void save(ISearchIndex index, String dest, boolean usegzip) {
        Debug.notNull(index, "index");
        Debug.notNullOrEmpty(dest, "destination");

        try {

            Debug.debugOut(String.format("Writing index into %s...", dest));

            // create structure
            FastVector attr;
            Instances data;
            double[] values;

            attr = new FastVector();
            attr.addElement(new Attribute("termId", (FastVector) null));
            attr.addElement(new Attribute("docId", (FastVector) null));
            attr.addElement(new Attribute("value"));
            attr.addElement(new Attribute("dl"));

            data = new Instances("ir", attr, 0);

            for (String term : index.termSet()) {
                for (Entry<Serializable, Double> entry : index.getDocuments(term).entrySet()) {
                    values = new double[data.numAttributes()];
                    // termId
                    values[0] = data.attribute(0).addStringValue(term);
                    // docId
                    values[1] = data.attribute(1).addStringValue(entry.getKey().toString());
                    // term freq
                    values[2] = entry.getValue();
                    // dl value
                    values[3] = index.getDL(entry.getKey());
                    data.add(new Instance(1.0, values));
                }
            }
            // create saver
            AbstractSaver saver = new ArffSaver();

            saver.setInstances(data);

            ((ArffSaver) saver).setCompressOutput(usegzip);
            // save file
            OutputStream outStream = new FileOutputStream(new File(dest));

            saver.setDestination(outStream);

            saver.writeBatch();

            outStream.close();
            
            Debug.debugOut("Index saved");
        } catch (IOException e) {
            // should never occur, since we checked the file when the
            // index was constructed.
            Debug.setFatal();
            Debug.reportError(e.toString());
        }
    }
}
