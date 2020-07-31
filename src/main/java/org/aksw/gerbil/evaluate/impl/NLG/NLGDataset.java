package org.aksw.gerbil.evaluate.impl.NLG;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;

public class NLGDataset extends AbstractDataset implements InitializableDataset {
    protected List<Document> documents;
    protected String refDirectory;

    public NLGDataset(String textsDirectory) {
        this.refDirectory = textsDirectory;
    }

    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public List<Document> getInstances() {
        return documents;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(refDirectory));
    }

    protected List<Document> loadDocuments(File textDir) throws GerbilException {
        if ((!textDir.exists()) || (!textDir.isDirectory())) {
            throw new GerbilException("The given text directory (" + textDir.getAbsolutePath()
                    + ") does not exist or is not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        return Arrays.asList((Document) new DocumentImpl("", Arrays.asList(new SimpleFileRef(textDir))));
    }

}
