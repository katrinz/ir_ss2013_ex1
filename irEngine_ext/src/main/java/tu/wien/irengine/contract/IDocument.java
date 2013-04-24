/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.contract;

import java.io.Serializable;

/**
 *
 */
public interface IDocument {

    String getContent();

    void setContent(String content);

    Serializable getId();
}
