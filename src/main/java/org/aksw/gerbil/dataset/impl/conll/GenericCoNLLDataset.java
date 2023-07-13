/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.dataset.impl.conll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dataset Adapter to load a dataset that follows the general structure of
 * CoNLL.
 */
public class GenericCoNLLDataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericCoNLLDataset.class);

    /**
     * Prefix of a value in the marking column that expresses the start of a
     * marking. TODO think about removing the '-' or make it configurable.
     */
    protected static final String MARKING_START = "B-";

    /**
     * Prefix of a value in the marking column that expresses the continuation of a
     * marking.
     */
    protected static final String MARKING_INSIDE = "I-";

    /**
     * The file from which the data will be loaded.
     */
    protected String file;
    /**
     * The list of documents loaded from the file.
     */
    protected List<Document> documents;
    /**
     * Id of the first document.
     */
    protected int firstDocId;
    /**
     * Id of the last document.
     */
    protected int lastDocId;
    /**
     * Class to map markings from the dataset to their type IRI.
     */
    protected CoNLLTypeRetriever typeRetriever;
    /**
     * Id of the column that contains the annotations.
     */
    protected int annotationColumn;
    /**
     * Id of the column that contains the entity's IRI. If there is no such column,
     * it is set to -1.
     */
    protected int uriColumn;
    
    

    public GenericCoNLLDataset(String file, int annotationColumn, int uriColumn, CoNLLTypeRetriever typeRetriever) {
        this(file, annotationColumn, uriColumn, typeRetriever, -1, -1);
    }

    public GenericCoNLLDataset(String file, int annotationColumn, int uriColumn, CoNLLTypeRetriever typeRetriever,
            String firstDocId, String lastDocId) {
        this(file, annotationColumn, uriColumn, typeRetriever, Integer.parseInt(firstDocId),
                Integer.parseInt(lastDocId));
    }

    public GenericCoNLLDataset(String file, int annotationColumn, int uriColumn, CoNLLTypeRetriever typeRetriever,
            int firstDocId, int lastDocId) {
        this.file = file;
        this.annotationColumn = annotationColumn;
        this.uriColumn = uriColumn;
        this.typeRetriever = typeRetriever;
        this.firstDocId = firstDocId;
        this.lastDocId = lastDocId;
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
        if ((firstDocId > 0) && (lastDocId > 0)) {
            this.documents = this.documents.subList(firstDocId - 1, lastDocId);
        }
    }

    /**
     * This method loads the CoNLL dataset from the given file.
     * 
     * @param file file from which the dataset will be loaded
     * @return list of {@link Document} instances that have been loaded.
     * @throws GerbilException if there is an IO error while reading the file.
     */
    protected List<Document> loadDocuments(File file) throws GerbilException {
        List<Document> documents = new ArrayList<Document>();
        // Create namespace for the documents of this dataset
        String documentUriPrefix = "http://" + getName() + "/";
        StringBuilder textOfCurrentDocument = new StringBuilder();
        // Flag to track if a whitespace should be inserted in front of a line
        boolean whiteSpaceInFront = true; 
        // Flag to track if a whitespace should be inserted behind a line
        boolean whiteSpaceBehind = true; 
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));) {
            String line = reader.readLine();
            // Id of the next document in this file
            int index = 0;
            List<String> linesOfCurrentDoc = new ArrayList<>();
            // Iterate through all lines until the complete file has been read.
            while (line != null) {
                // If there is an empty line, the previous document ended and will be added to
                // the list of documents
                if (line.trim().isEmpty()) {
                    // If there is a document that can be added
                    if (linesOfCurrentDoc.size() > 0) {
                        // Get Markings
                        List<Marking> markings = findMarkings(linesOfCurrentDoc, textOfCurrentDocument);
                        // Save the document
                        documents.add(new DocumentImpl(textOfCurrentDocument.toString(), documentUriPrefix + index,
                                markings));
                        // Reset local variables
                        textOfCurrentDocument.delete(0, textOfCurrentDocument.length());
                        linesOfCurrentDoc.clear();
                        // Increase the document ID
                        index++;
                    }
                } else {          	
                	 if ((textOfCurrentDocument.length() > 0) && (line.length() >= 1)) {
                         if (line.length() == 1) { // Check if the line has only one character
                             char ch = line.charAt(0); // Get the character
                             switch (ch) {
                                 case '?':
                                 case '!':
                                 case ',':
                                 case ')':
                                 case ']':
                                 case '}':
                                 case '.':
                                 case '፠': // ፠ section mark
                                 case '፡': // ፡ word separator
                                 case '።': // ። full stop (period)
                                 case '፣': // ፣ comma
                                 case '፤': // ፤ semicolon
                                 case '፥': // ፥ colon
                                 case '፦': // ፦ preface colon
                                 case '፧': // ፧ question mark
                                 case '፨': // ፨ paragraph separator
                                     // Set whiteSpaceInFront to false if the character is a punctuation mark
                                     // that does not require a whitespace in front
                                     whiteSpaceInFront = false; 
                                     break;
                                 case '"':
                                     // Toggle whiteSpaceBehind if the character is a quote mark
                                     whiteSpaceBehind = !whiteSpaceBehind;
                                     break;
                                 case '(':
                                 case '[':
                                 case '{':
                                     // Set whiteSpaceBehind to false if the character is an opening parenthesis or bracket
                                     whiteSpaceBehind = false;
                                     break;
                                 default:
                                     break;
                             }

                         }
                         else if (!Character.isLetterOrDigit(line.charAt(0))) { 
                        	 // Check if the first character of the line is not a letter or digit
                             whiteSpaceInFront = false; 
                             // Set whiteSpaceInFront to false if the line starts with a non-letter or non-digit character
                         }
	                     if (whiteSpaceInFront) {
	                         textOfCurrentDocument.append(' '); 
	                         // Append a whitespace to separate the current line from the previous content
	                     }
                	 }
                    // Add the current line to the list of lines of the current document
                    linesOfCurrentDoc.add(line);
                    // Append the current line to the text of the current document
                    textOfCurrentDocument.append(line); 
                }
                // Read the next line
                line = reader.readLine();
            }
            // check if there is a document left that should be added
            if (linesOfCurrentDoc.size() > 0) {
                // Get Markings
                List<Marking> markings = findMarkings(linesOfCurrentDoc, textOfCurrentDocument);
                // Save last document
                documents.add(new DocumentImpl(textOfCurrentDocument.toString(), documentUriPrefix + index, markings));
            }
        } catch (IOException e) {
            throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        return documents;
    }

    /**
     * Find markings of document and add document text to the given StringBuilder.
     * 
     * @param linesOfCurrentDoc the lines of the current document
     * @param currentText       StringBuilder to which the document text should be
     *                          added
     * @return The list of {@link Marking} instances found within the document
     */
    protected List<Marking> findMarkings(List<String> linesOfCurrentDoc, StringBuilder currentText) {
        List<Marking> markings = new ArrayList<Marking>();
        int i = 0;
        // Iterate over the document lines
        for (String tokenFull : linesOfCurrentDoc) {
            // split the columns
            String[] token = tokenFull.split("\t+");
            // If we can parse this line
            if (token.length > annotationColumn && token[annotationColumn].startsWith(MARKING_START)) {
                // Get the marking from this line (and maybe the next lines)
                markings.add(getWholeMarking(linesOfCurrentDoc, i, currentText));
            }
            // Add the token from this line to the document's text
            // TODO 1. make the whitespace configurable to allow other word separators 2.
            // Remove the previous word separator if we have a punctuation character.
            // (quotation, apostrophe)
            currentText.append(token[0] + " ");
            // Increase the line ID
            i++;
        }
        return markings;
    }

    protected Marking getWholeMarking(List<String> linesOfCurrentDoc, int pos, StringBuilder currentText) {
        String[] tokens = linesOfCurrentDoc.get(pos).split("\t");

        // get type of the marking TODO if the B- and I- are configurable, the
        // substring(2) has to be configurable as well.
        String type = typeRetriever.getTypeURI(tokens[annotationColumn].substring(2));

        // get uri of the marking if given in the dataset
        String uri = null;
        if (uriColumn != -1 && tokens[uriColumn].startsWith("http")) {
            uri = tokens[uriColumn];
        }

        // get surface form of the marking
        StringBuilder surfaceForm = new StringBuilder().append(tokens[0]);
        for (int i = pos + 1; i < linesOfCurrentDoc.size(); i++) {
            tokens = linesOfCurrentDoc.get(i).split("\t");
            if (tokens[annotationColumn].startsWith(MARKING_INSIDE)) {
                // TODO 1. make the whitespace configurable to allow other word separators 2.
                // Remove the previous word separator if we have a punctuation character.
                surfaceForm.append(" ").append(tokens[0]);
            } else {
                break;
            }
        }
        if (type != null) {
            if (uri != null) {
                return new TypedNamedEntity(currentText.length(), surfaceForm.length(), uri,
                        new HashSet<String>(Arrays.asList(type)));
            } else {
                return new TypedSpanImpl(currentText.length(), surfaceForm.length(),
                        new HashSet<String>(Arrays.asList(type)));
            }
        } else {
            if (uri != null) {
                return new NamedEntity(currentText.length(), surfaceForm.length(), uri);
            } else {
                LOGGER.warn(
                        "Found a marked piece of text without any further information: \"{}\". This is either an error in the dataset or this adapter is not correctly configured.",
                        surfaceForm);
                return new SpanImpl(currentText.length(), surfaceForm.length());
            }
        }
    }
}