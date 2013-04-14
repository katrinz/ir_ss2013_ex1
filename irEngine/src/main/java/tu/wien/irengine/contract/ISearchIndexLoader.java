/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

/**
 *
 */
public interface ISearchIndexLoader {

    <T extends ISearchIndex> T load(Class<T> clazz, String src, boolean usegzip);
}
