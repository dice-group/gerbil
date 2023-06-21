package org.aksw.gerbil.dataset.impl.indq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the IndQNERDataset class, which represents an InitializableDataset for the IndQNER dataset.
 */
public class IndQNERDataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndQNERDataset.class);

    private String file;
    private List<Document> documents;

    /**
     * Constructs a new IndQNERDataset with the specified dataset file.
     * 
     * @param file the path to the IndQNER dataset file
     */
    public IndQNERDataset(String file) {
        this.file = file;
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
        this.documents = loadDocuments(new File(file));
    }

    /**
     * Loads the documents from the IndQNER dataset file.
     * 
     * @param file the IndQNER dataset file
     * @return a list of documents parsed from the dataset file
     * @throws GerbilException if there is an error while loading the dataset
     */
    private List<Document> loadDocuments(File file) throws GerbilException {
        List<Document> docs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> documentTokens = new ArrayList<>();
            List<Marking> markings = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (!documentTokens.isEmpty()) {
                        String documentText = String.join(" ", documentTokens);
                        Document document = new DocumentImpl(documentText);
                        document.setMarkings(markings);
                        docs.add(document);
                    }
                    documentTokens.clear();
                    markings.clear();
                } else {
                    String[] parts = line.split("\t");
                    if (parts.length == 2) {
                        String token = parts[0];
                        String label = parts[1];

                        documentTokens.add(token);
                        processLabel(markings, token, label);
                    } else {
                        LOGGER.warn("Invalid line format in IndQNER dataset file: {}", line);
                    }
                }
            }
            // Add the last document if there are remaining tokens
            if (!documentTokens.isEmpty()) {
                String documentText = String.join(" ", documentTokens);
                Document document = new DocumentImpl(documentText);
                document.setMarkings(markings);
                docs.add(document);
            }
        } catch (IOException e) {
            throw new GerbilException("Error while reading the IndQNER dataset file.", e,
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        return docs;
    }

    /**
     * Processes the label and creates a NamedEntity marking if the label indicates the
     * beginning of a named entity.
     * 
     * @param markings     the list of markings for the current document
     * @param token        the current token
     * @param label        the label associated with the token
     */
    private void processLabel(List<Marking> markings, String token, String label) {
        if (label.startsWith("B-")) {
            String entityLabel = label.substring(2); // Remove the "B-" prefix to get the entity label
            int tokenLength = token.length();
            NamedEntity namedEntity = new NamedEntity(0, tokenLength, entityLabel); // Create a new NamedEntity marking
            markings.add(namedEntity);
        }
        // If the label does not indicate the beginning of a named entity, it is skipped
    }
}
