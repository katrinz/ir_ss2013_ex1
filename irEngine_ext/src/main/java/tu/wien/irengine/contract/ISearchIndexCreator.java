/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

/**
 *
 */
public interface ISearchIndexCreator {

    <T extends ISearchIndex> T create(Class<T> clazz, String src);

    ITermVectorCreator getTermVectorCreator();
}
