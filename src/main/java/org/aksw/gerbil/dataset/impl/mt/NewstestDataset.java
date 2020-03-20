package org.aksw.gerbil.dataset.impl.mt;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class NewstestDataset extends AbstractDataset implements InitializableDataset {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewstestDataset.class);

    protected List<Document> documents;
    private String srcFile;
    private String refFile;
    private String dataset;

    public NewstestDataset(String dataset) throws GerbilException {
        this.dataset = dataset;
    }
    public NewstestDataset(String srcFile, String refFile) throws GerbilException {
        this.srcFile = srcFile;
        this.refFile = refFile;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(srcFile), new File(refFile));
    }

    private List<Document> loadDocuments(File src, File ref) {

        return documents;
    }

    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public List<Document> getInstances() {
        return documents;
    }
}
