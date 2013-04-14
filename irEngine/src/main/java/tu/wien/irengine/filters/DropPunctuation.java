/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.filters;

import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.contract.IDocumentFilter;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.StringTools;

/**
 *
 */
public class DropPunctuation implements IDocumentFilter {

    public String getFilteredContent(IDocument document) {
        Debug.notNull(document, "document");

        String content = document.getContent();
        content = StringTools.removeNumbers(content);
        content = StringTools.removeDuplicates(content);
        content = StringTools.removeHtmlTags(content);
        content = StringTools.removeDelimiters(content);
        content = StringTools.whiteSpaceToSpaces(content);

        return content;
    }

    public void applyTo(IDocument document) {
        Debug.notNull(document, "document");
        document.setContent(this.getFilteredContent(document));
    }
}
