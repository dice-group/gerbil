package org.aksw.gerbil.dataset.impl.mt;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewstestDataset extends AbstractDataset implements InitializableDataset {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewstestDataset.class);

    protected List<Document> documents;
    private String srcFile;
    private String refFile;

    public NewstestDataset(String srcFile, String refFile) throws GerbilException {
        this.srcFile = srcFile;
        this.refFile = refFile;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(srcFile), new File(refFile));
    }

    protected List<Document> loadDocuments(File src, File ref)throws GerbilException {
        String srcText;
        String refText;
        boolean test1 = src.exists();
        boolean test2 = ref.exists();
        if ((!src.exists()) && (!src.isDirectory())) {
            throw new GerbilException(
                    "The given text directory (" + src.getAbsolutePath() + ") is not existing or not a directory.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        String textDirPath = src.getAbsolutePath();
        if (!textDirPath.endsWith(File.separator)) {
            textDirPath = textDirPath + File.separator;
        }
        if ((!ref.exists()) && (!ref.isDirectory())) {
            throw new GerbilException("The given text directory (" + ref.getAbsolutePath()
                    + ") is not existing or not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        List<Document> documents = new ArrayList<Document>();
        NewstestDataset_Result parsedResult = null;
        for (File srcFile : src.listFiles()) {
            // read the src text file
            try {
                srcText = FileUtils.readFileToString(new File(textDirPath + parsedResult.getSrcFileName()));
            } catch (IOException e) {
                throw new GerbilException(
                        "Couldn't read text file \"" + textDirPath + parsedResult.getSrcFileName(),
                        e, ErrorTypes.DATASET_LOADING_ERROR);
            }
            // read the ref text file
            try {
                refText = FileUtils.readFileToString(new File(textDirPath + parsedResult.getRefFileName()));
            } catch (IOException e) {
                throw new GerbilException(
                        "Couldn't read text file \"" + textDirPath + parsedResult.getRefFileName(),
                        e, ErrorTypes.DATASET_LOADING_ERROR);
            }
            documents.add(createDocument(parsedResult.getSrcFileName(), srcText, parsedResult.getRefFileName(), refText));
        }
        return documents;
    }

    private Document createDocument(String srcFileName, String srcText, String refFileName, String refText) {
        return null;
    }

    protected String generateDocumentUri(String fileName){
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(fileName);
        return builder.toString();
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
