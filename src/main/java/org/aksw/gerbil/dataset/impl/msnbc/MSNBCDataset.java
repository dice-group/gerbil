package org.aksw.gerbil.dataset.impl.msnbc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MSNBCDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(MSNBCDataset.class);

    protected List<Document> documents;
    protected String name;
    protected String textsDirectory;
    protected String annotationsDirectory;

    public MSNBCDataset(String textsDirectory, String annotationsDirectory) throws GerbilException {
        this.textsDirectory = textsDirectory;
        this.annotationsDirectory = annotationsDirectory;
    }

    @Override
    public int size() {
        return documents.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Document> getInstances() {
        return documents;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(textsDirectory), new File(annotationsDirectory));
    }

    protected List<Document> loadDocuments(File textDir, File annoDir) throws GerbilException {
        if ((!textDir.exists()) || (!textDir.isDirectory())) {
            throw new GerbilException("The given text directory (" + textDir.getAbsolutePath()
                    + ") is not existing or not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        String textDirPath = textDir.getAbsolutePath();
        if (!textDirPath.endsWith(File.separator)) {
            textDirPath = textDirPath + File.separator;
        }
        if ((!annoDir.exists()) || (!annoDir.isDirectory())) {
            throw new GerbilException("The given annotation directory (" + annoDir.getAbsolutePath()
                    + ") is not existing or not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        MSNBC_XMLParser parser = new MSNBC_XMLParser();
        MSNBC_Result parsedResult;
        String text;
        List<Document> documents = new ArrayList<Document>();
        for (File annoFile : annoDir.listFiles()) {
            // parse the annotation file
            try {
                parsedResult = parser.parseAnnotationsFile(annoFile);
            } catch (Exception e) {
                throw new GerbilException("Couldn't parse given annotation file (\"" + annoFile.getAbsolutePath()
                        + "\".", e, ErrorTypes.DATASET_LOADING_ERROR);
            }
            if (parsedResult.getTextFileName() == null) {
                throw new GerbilException("The parsed annotation file (\"" + annoFile.getAbsolutePath()
                        + "\" did not define a text file name.", ErrorTypes.DATASET_LOADING_ERROR);
            }
            // read the text file
            try {
                text = FileUtils.readFileToString(new File(textDirPath + parsedResult.getTextFileName()));
            } catch (IOException e) {
                throw new GerbilException("Couldn't read text file \"" + textDirPath + parsedResult.getTextFileName()
                        + "\" mentioned in the annotations file \"" + annoFile.getAbsolutePath() + "\".", e,
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            // create document
            documents.add(createDocument(parsedResult.getTextFileName(), text, parsedResult));
        }
        return documents;
    }

    protected Document createDocument(String fileName, String text, MSNBC_Result parsedResult) {
        String documentUri = generateDocumentUri(fileName);
        List<Marking> markings = new ArrayList<Marking>(parsedResult.getMarkings().size());
        String retrievedSurfaceForm;
        for (MSNBC_NamedEntity ne : parsedResult.getMarkings()) {
            retrievedSurfaceForm = text.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
            if (!retrievedSurfaceForm.equals(ne.getSurfaceForm())) {
                LOGGER.warn("In document " + documentUri + ", the expected surface form of the named entity " + ne
                        + " does not fit the surface form derived from the text \"" + retrievedSurfaceForm + "\".");
            }
            addDBpediaUris(ne.getUris());
            markings.add(ne.toNamedEntity());
        }
        return new DocumentImpl(text, documentUri, markings);
    }

    protected String generateDocumentUri(String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(fileName);
        return builder.toString();
    }

    protected static void addDBpediaUris(Set<String> uris) {
        List<String> dbpediaUris = new ArrayList<String>(uris.size());
        for (String uri : uris) {
            dbpediaUris.add(uri.replace("en.wikipedia.org/wiki", "dbpedia.org/resource"));
            dbpediaUris.add(uri.replace("wikipedia.org/wiki", "dbpedia.org/resource"));
        }
        uris.addAll(dbpediaUris);
    }
}
