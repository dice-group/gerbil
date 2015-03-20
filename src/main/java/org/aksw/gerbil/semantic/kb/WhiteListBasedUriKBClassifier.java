package org.aksw.gerbil.semantic.kb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteListBasedUriKBClassifier extends AbstractWhiteListBasedUriKBClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhiteListBasedUriKBClassifier.class);

    public static WhiteListBasedUriKBClassifier create(File file) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            return create(fin);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to read knowledge base namespaces.", e);
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    public static WhiteListBasedUriKBClassifier create(InputStream stream) {
        try {
            return new WhiteListBasedUriKBClassifier(IOUtils.readLines(stream));
        } catch (IOException e) {
            LOGGER.error("Exception while trying to read knowledge base namespaces.", e);
            return null;
        }
    }

    protected WhiteListBasedUriKBClassifier(List<String> kbNamespaces) {
        super(kbNamespaces);
    }
}
