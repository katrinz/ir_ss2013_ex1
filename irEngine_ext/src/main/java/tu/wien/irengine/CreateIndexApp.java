/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.contract.IEngine;
import tu.wien.irengine.contract.ITermVectorCreator;
import tu.wien.irengine.engines.Engine;
import tu.wien.irengine.engines.EngineConfiguration;
import tu.wien.irengine.filters.DropPunctuation;
import tu.wien.irengine.filters.PorterStemmer;
import tu.wien.irengine.filters.StopList;
import tu.wien.irengine.indices.InvertedIndex;
import tu.wien.irengine.indices.InvertedIndexCreator;
import tu.wien.irengine.io.ArffIndexLoader;
import tu.wien.irengine.io.ArffIndexSaver;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.vectors.BMFVectorCreator;
import tu.wien.irengine.vectors.BMVectorCreator;
import tu.wien.irengine.vectors.CosineSimEvaluator;
import tu.wien.irengine.vectors.TFIDFVectorCreator;

/**
 *
 */
public class CreateIndexApp {

    private static int maxDocSize;
    private static int maxTermSize;
    private static boolean normalize;
    private static boolean usegzip;
    private static boolean stopWords;
    private static boolean stemming;
    private static double lowerBound;
    private static double upperBound;
    private static String documentSetPath;
    private static String indexPath;
    private static String irfunction;
    private static Double bmK;
    private static Double bmB;
    private static String bmWeights;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loadProperties();
        EngineConfiguration conf = loadEngineConfiguration();
        IEngine engine = new Engine(conf);
        engine.createIndex();
    }

    private static void loadProperties() {
        // init properties
        try {
            Debug.debugOut("Load properties.");

            Properties properties = new Properties();
            properties.loadFromXML(new FileInputStream("createindex.properties"));

            maxDocSize = Integer.parseInt(properties.getProperty("maxDocSize"));
            maxTermSize = Integer.parseInt(properties.getProperty("maxTermSize"));
            normalize = Boolean.parseBoolean(properties.getProperty("normalize"));
            usegzip = Boolean.parseBoolean(properties.getProperty("usegzip"));
            stemming = Boolean.parseBoolean(properties.getProperty("stemming"));
            stopWords = Boolean.parseBoolean(properties.getProperty("stopWords"));
            lowerBound = Double.parseDouble(properties.getProperty("lowerBound"));
            upperBound = Double.parseDouble(properties.getProperty("upperBound"));
            documentSetPath = properties.getProperty("documentSetPath");
            indexPath = properties.getProperty("indexPath");
            irfunction = properties.getProperty("irfunction");
            bmK = Double.parseDouble(properties.getProperty("bmK"));
            bmB = Double.parseDouble(properties.getProperty("bmB"));
            bmWeights = properties.getProperty("bmWeights");

            Debug.debugOut("Properties loaded correctly.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static EngineConfiguration loadEngineConfiguration() {
        List<IDocumentFilter> filters = new ArrayList<IDocumentFilter>();
        filters.add(new DropPunctuation());
        if (stopWords) {
            filters.add(new StopList(new File("stoplist.txt")));
        }
        if (stemming) {
            filters.add(new PorterStemmer());
        }

        // init vector
        ITermVectorCreator tvCreator;
        if (irfunction.equalsIgnoreCase("bm")) {
            if (bmK != 0 || bmB != 0) {
                tvCreator = new BMVectorCreator(filters, bmK, bmB);
            } else {
                tvCreator = new BMVectorCreator(filters);
            }
        } else if (irfunction.equalsIgnoreCase("bmf")) {
            if (bmK != 0 || bmB != 0) {
                tvCreator = new BMFVectorCreator(filters, bmK, bmB, bmWeights);
            } else {
                tvCreator = new BMFVectorCreator(filters);
            }
        } else {
            tvCreator = new TFIDFVectorCreator(filters);
        }
        tvCreator.setNormalize(normalize);
        tvCreator.setHighestRank(maxDocSize);
        tvCreator.setMaxSize(maxTermSize);
        tvCreator.setBounds(lowerBound, upperBound);

        EngineConfiguration conf = new EngineConfiguration();
        conf.setDocumentFilters(filters);
        conf.setEvaluator(new CosineSimEvaluator());
        conf.setIndexClass(InvertedIndex.class);
        conf.setIndexCreator(new InvertedIndexCreator(tvCreator));
        conf.setIndexLocation(indexPath);
        conf.setDocSetLocation(documentSetPath);
        conf.setLoader(new ArffIndexLoader());
        conf.setSaver(new ArffIndexSaver());
        conf.setUsegzip(usegzip);
        conf.setVectorCreator(tvCreator);

        return conf;
    }
}
