/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

/**
 *
 */
public interface ISearchIndexSaver {

    void save(ISearchIndex index, String dest, boolean usegzip);
}
