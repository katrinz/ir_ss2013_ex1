/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.model;

import java.io.Serializable;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.utils.Debug;

/**
 *
 */
public class Document implements IDocument {

    private String content = "";
    private Serializable id;
    
    public Document(Serializable docId, String content) {
        Debug.notNullOrEmpty(docId.toString(), "docId");
        
        this.id = docId;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Serializable getId() {
        return id;
    }
}
