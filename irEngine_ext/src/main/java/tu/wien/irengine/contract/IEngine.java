/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedSet;

/**
 *
 */
 public interface IEngine {
    
     void init();
    
     void createIndex();

     SortedSet<Map.Entry<Serializable, Double>> findBy(IDocument document);

     SortedSet<Map.Entry<Serializable, Double>> findBy(String term);
}
