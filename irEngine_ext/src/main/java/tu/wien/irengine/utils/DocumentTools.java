/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tu.wien.irengine.contract.IDocument;
import tu.wien.irengine.model.Document;
import tu.wien.irengine.utils.Debug;
import tu.wien.irengine.utils.IOTools;

/**
 *
 */
public class DocumentTools {

    private static List<String> documentPaths;

    private DocumentTools() {
    }

    public static List<String> getAllDocumentPaths(String path) {
        documentPaths = new ArrayList<String>();
        processDir(new File(path));
        return documentPaths;
    }

    public static IDocument createDocument(String path) {
        try {
            File file = new File(path);
            String name = file.getName();
            String group = file.getParent().substring(file.getParent().lastIndexOf("\\") + 1);
            String content =
                    IOTools.slurp(IOTools.getWashingFileReader(file));

            return new Document(String.format("%s/%s", group, name), content);
        } catch (IOException e) {
            Debug.debugOut("Error reading " + path);
        }

        return null;
    }

    private static void processDir(File file) {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                processDir(subfile);
            }
        } else if (file.isFile()) {
            processFile(file);
        }
    }

    private static void processFile(File file) {
        documentPaths.add(file.getAbsolutePath());
    }
}
