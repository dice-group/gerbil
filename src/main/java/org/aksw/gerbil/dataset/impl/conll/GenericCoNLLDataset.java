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
import org.aksw.gerbil.transfer.nif.Span;
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
    /**
     * The character used to separate columns.
     */
    protected String columnSeparator = "\t";
    /**
     * String used to insert white spaces between tokens.
     */
    protected String whitespace = " ";
    /**
     * String inserted between tokens if no white space should be inserted.
     */
    protected String nonWhitespace = "";

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
                        StringBuilder currentText = new StringBuilder();
                        List<Marking> markings = processSingleDocument(linesOfCurrentDoc, currentText);
                        // Save the document
                        documents.add(new DocumentImpl(currentText.toString(), documentUriPrefix + index, markings));
                        // Increase the document ID
                        index++;
                    }
                    // Clear the lines for the next document
                    linesOfCurrentDoc.clear();
                } else {
                    // Add the current line to the list of lines of the current document
                    linesOfCurrentDoc.add(line);
                }
                // Read the next line
                line = reader.readLine();
            }
            // check if there is a document left that should be added
            if (linesOfCurrentDoc.size() > 0) {
                // Get Markings
                StringBuilder currentText = new StringBuilder();
                List<Marking> markings = processSingleDocument(linesOfCurrentDoc, currentText);
                // Save last document
                documents.add(new DocumentImpl(currentText.toString(), documentUriPrefix + index, markings));
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
    protected List<Marking> processSingleDocument(List<String> linesOfCurrentDoc, StringBuilder currentText) {
        List<Marking> markings = new ArrayList<Marking>();
        Span currentMarking = null;
        // Flags to track if a whitespace should be inserted in front of and behind a
        // line
        boolean whiteSpaceInFront = false;
        boolean whiteSpaceBehindPreviousToken = false;
        boolean whiteSpaceBehindCurrentToken = false;
        boolean sawQuoteBefore = false;
        // Iterate over the document lines
        for (String tokenFull : linesOfCurrentDoc) {
            whiteSpaceBehindPreviousToken = whiteSpaceBehindCurrentToken;
            // split the columns
            String[] token = tokenFull.split(columnSeparator);
            // Check if the line has only one character
            if (token[0].length() == 1) {
                char ch = token[0].charAt(0); // Get the character
                switch (ch) {
                case '?': // falls through
                case '!':
                case ',':
                case ')':
                case ']':
                case '}':
                case ':':
                case ';':
                case '.':
                case '#':
                case '-':
                case '=':
                case '፠': // ፠ section mark // falls through
                case '።': // ። full stop (period)
                case '፣': // ፣ comma
                case '፤': // ፤ semicolon
                case '፥': // ፥ colon
                case '፦': // ፦ preface colon
                case '፧': // ፧ question mark
                case '፨': // ፨ paragraph separator
                {
                    whiteSpaceInFront = false;
                    whiteSpaceBehindCurrentToken = true;
                    break;
                }
                // General quotation characters (can be start or end)
                // According to https://www.overleaf.com/learn/latex/Typesetting_quotations
                case '"': // falls through
                case '»': // Start in Danish, end in French, Russian, etc.
                case '«': // Start in French, Russian, etc.; end in Danish
                case '“': // Start in English, end in German, Lithuanian, Polish
                {
                    // Toggle whiteSpaceBehind if the character is a quote mark
                    whiteSpaceInFront = !sawQuoteBefore;
                    whiteSpaceBehindCurrentToken = sawQuoteBefore;
                    sawQuoteBefore = !sawQuoteBefore;
                    break;
                }
                // English, UK ‘…’
                // Start quotation characters
                case '„': // Start in German, Lithuanian, Polish
                case '‚': // Start in English
                {
                    whiteSpaceInFront = true;
                    whiteSpaceBehindCurrentToken = false;
                    sawQuoteBefore = true;
                }
                // End quotation characters
                case '”': // End in a lot of languages
                case '‘': // End in English
                {
                    whiteSpaceInFront = false;
                    whiteSpaceBehindCurrentToken = true;
                    sawQuoteBefore = false;
                }
                case '(': // falls through
                case '[':
                case '{': {
                    whiteSpaceInFront = true;
                    whiteSpaceBehindCurrentToken = false;
                    break;
                }
                case '፡': // ፡ word separator
                default: {
                    whiteSpaceInFront = true;
                    whiteSpaceBehindCurrentToken = true;
                    break;
                }
                }
//                } else if (!Character.isLetterOrDigit(token[0].charAt(0))) {
//                    // Check if the first character of the line is not a letter or digit
//                    whiteSpaceInFront = false;
//                    // Set whiteSpaceInFront to false if the line starts with a non-letter or
//                    // non-digit character
            } else {
                whiteSpaceInFront = true;
                whiteSpaceBehindCurrentToken = true;
            }
            // Remove leading/trailing whitespaces and normalize spaces within the token
            String normalizedToken = token[0].trim().replaceAll("\\s+", " ");

            // If the current marking is not null AND there is no annotation column or there
            // is no MARKING_INSIDE annotation --> The previous marking ended
            if (currentMarking != null
                    && (token.length <= annotationColumn || !token[annotationColumn].startsWith(MARKING_INSIDE))) {
                currentMarking.setLength(currentText.length() - currentMarking.getStartPosition());
                currentMarking = null;
            }

            // Add the token from this line to the document's text
            if (whiteSpaceInFront && whiteSpaceBehindPreviousToken) {
                currentText.append(whitespace);
            } else {
                currentText.append(nonWhitespace);
            }
            // If this line contains the start of a marking, we should keep track of it
            if (token.length > annotationColumn && token[annotationColumn].startsWith(MARKING_START)) {
                // Create new marking
                currentMarking = createNewMarking(token, currentText.length());
                markings.add(currentMarking);
            }
            // TODO 1. make the whitespace configurable to allow other word separators 2.
            // Remove the previous word separator if we have a punctuation character.
            // (quotation, apostrophe)
            currentText.append(normalizedToken);
        }
        // If there is an unfinished marking, finalize it
        if (currentMarking != null) {
            currentMarking.setLength(currentText.length() - currentMarking.getStartPosition());
        }
        return markings;
    }

    /**
     * Generates a {@link Marking} that is at least an implementation of the
     * {@link Span} interface or even more, depending on the data available in the
     * given. Note that the {@link Span} instances created by this method have the
     * length 0. Their final length is set outside of this method.
     * 
     * @param line     the current line of the CoNLL file
     * @param startPos the start position of the {@link Span}, i.e., the position of
     *                 this line within the text
     * @return A {@link Span} instance which already contains nearly all information
     *         about the {@link Marking}, except its length
     */
    protected Span createNewMarking(String[] line, int startPos) {
        // get type of the marking TODO if the B- and I- are configurable, the
        // substring(2) has to be configurable as well.
        String type = typeRetriever.getTypeURI(line[annotationColumn].substring(2));

        // get uri of the marking if given in the dataset
        String uri = null;
        if (uriColumn != -1 && line[uriColumn].startsWith("http")) {
            uri = line[uriColumn];
        }
        // We set the length of the newly created marking to 0, because we have to
        // override it, anyway. If we ever see a 0 outside of this class, we know that
        // something went wrong.
        if (type != null) {
            if (uri != null) {
                return new TypedNamedEntity(startPos, 0, uri, new HashSet<String>(Arrays.asList(type)));
            } else {
                return new TypedSpanImpl(startPos, 0, new HashSet<String>(Arrays.asList(type)));
            }
        } else {
            if (uri != null) {
                return new NamedEntity(startPos, 0, uri);
            } else {
                LOGGER.warn(
                        "Found a marked piece of text without any further information: \"{}\". This is either an error in the dataset or this adapter is not correctly configured.",
                        Arrays.toString(line));
                return new SpanImpl(startPos, 0);
            }
        }
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the firstDocId
     */
    public int getFirstDocId() {
        return firstDocId;
    }

    /**
     * @param firstDocId the firstDocId to set
     */
    public void setFirstDocId(int firstDocId) {
        this.firstDocId = firstDocId;
    }

    /**
     * @return the lastDocId
     */
    public int getLastDocId() {
        return lastDocId;
    }

    /**
     * @param lastDocId the lastDocId to set
     */
    public void setLastDocId(int lastDocId) {
        this.lastDocId = lastDocId;
    }

    /**
     * @return the typeRetriever
     */
    public CoNLLTypeRetriever getTypeRetriever() {
        return typeRetriever;
    }

    /**
     * @param typeRetriever the typeRetriever to set
     */
    public void setTypeRetriever(CoNLLTypeRetriever typeRetriever) {
        this.typeRetriever = typeRetriever;
    }

    /**
     * @return the annotationColumn
     */
    public int getAnnotationColumn() {
        return annotationColumn;
    }

    /**
     * @param annotationColumn the annotationColumn to set
     */
    public void setAnnotationColumn(int annotationColumn) {
        this.annotationColumn = annotationColumn;
    }

    /**
     * @return the uriColumn
     */
    public int getUriColumn() {
        return uriColumn;
    }

    /**
     * @param uriColumn the uriColumn to set
     */
    public void setUriColumn(int uriColumn) {
        this.uriColumn = uriColumn;
    }

    /**
     * @return the whitespace
     */
    public String getWhitespace() {
        return whitespace;
    }

    /**
     * @param whitespace the whitespace to set
     */
    public void setWhitespace(String whitespace) {
        this.whitespace = whitespace;
    }

    /**
     * @return the nonWhitespace
     */
    public String getNonWhitespace() {
        return nonWhitespace;
    }

    /**
     * @param nonWhitespace the nonWhitespace to set
     */
    public void setNonWhitespace(String nonWhitespace) {
        this.nonWhitespace = nonWhitespace;
    }

    /**
     * @return the columnSeparator
     */
    public String getColumnSeparator() {
        return columnSeparator;
    }

    /**
     * @param columnSeparator the columnSeparator to set
     */
    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }
}