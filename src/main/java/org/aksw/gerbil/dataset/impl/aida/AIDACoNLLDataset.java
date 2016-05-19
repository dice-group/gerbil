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
package org.aksw.gerbil.dataset.impl.aida;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.utils.WikipediaHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * AIDA/CoNLL Dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class AIDACoNLLDataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIDACoNLLDataset.class);

    private static final char SEPARATION_CHAR = '\t';
    private static final char QUOTATION_CHAR = '\0';
    private static final int TEXT_INDEX = 0;
    private static final int NE_TYPE_INDEX = 1;
    private static final int ANNOTATION_SURFACE_FORM_INDEX = 2;
    private static final int ANNOTATION_TITLE_INDEX = 3;
    private static final int ANNOTATION_URI_INDEX = 4;

    private static final String DOCUMENT_START_TAG = "-DOCSTART-";
    private static final String ANNOTATION_FIRST_WORD_TAG = "B";
    // private static final String ANNOTATION_NEXT_WORD_TAG = "I";
    private static final String ANNOTATION_NOT_IN_WIKI_TAG = "--NME--";
    private static final String WIKIPEDIA_URI_START = "http://en.wikipedia.org/wiki/";

    private String file;
    private List<Document> documents;
    private int firstDocId;
    private int lastDocId;

    public AIDACoNLLDataset(String file) {
        this(file, -1, -1);
    }

    public AIDACoNLLDataset(String file, String firstDocId, String lastDocId) {
        this(file, Integer.parseInt(firstDocId), Integer.parseInt(lastDocId));
    }

    public AIDACoNLLDataset(String file, int firstDocId, int lastDocId) {
        this.file = file;
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

    protected List<Document> loadDocuments(File file) throws GerbilException {
        String documentUriPrefix = "http://" + getName() + "/";
        BufferedReader bReader = null;
        CSVReader reader = null;
        List<Document> documents = new ArrayList<Document>();
        try {
            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
            reader = new CSVReader(bReader, SEPARATION_CHAR, QUOTATION_CHAR);
            String line[];
            Document currentDoc = null;
            StringBuilder textBuilder = new StringBuilder();
            List<Marking> markings = null;
            NamedEntity lastNE = null;
            Set<String> uris;
            line = reader.readNext();
            boolean quoteCharSeenBefore = false;
            boolean whiteSpaceInFront, whiteSpaceBehind = true;
            while (line != null) {
                if (line.length > TEXT_INDEX) {
                    // If a new document starts
                    if (line[TEXT_INDEX].startsWith(DOCUMENT_START_TAG)) {
                        if (currentDoc != null) {
                            currentDoc.setText(textBuilder.toString().trim());
                            textBuilder.setLength(0);
                            quoteCharSeenBefore = false;
                        }
                        markings = new ArrayList<Marking>();
                        currentDoc = new DocumentImpl(null, documentUriPrefix + documents.size(), markings);
                        documents.add(currentDoc);
                    } else {
                        if (!line[TEXT_INDEX].isEmpty()) {
                            // if we should insert a whitespace
                            whiteSpaceInFront = whiteSpaceBehind;
                            whiteSpaceBehind = true;
                            if ((textBuilder.length() > 0) && (line[TEXT_INDEX].length() >= 1)) {
                                if (line[TEXT_INDEX].length() == 1) {
                                    switch (line[TEXT_INDEX].charAt(0)) {
                                    case '?': // falls through
                                    case '!':
                                    case ',':
                                    case ')':
                                    case ']':
                                    case '}':
                                    case '.': {
                                        whiteSpaceInFront = false;
                                        break;
                                    }
                                    case '"': {
                                        // If we have seen another quote char
                                        // before
                                        if (!quoteCharSeenBefore) {
                                            whiteSpaceBehind = false;
                                        } else {
                                            whiteSpaceInFront = false;
                                        }
                                        quoteCharSeenBefore = !quoteCharSeenBefore;
                                        break;
                                    }
                                    case '(': // falls through
                                    case '[':
                                    case '{': {
                                        whiteSpaceBehind = false;
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                    }
                                } else if (!Character.isLetterOrDigit(line[TEXT_INDEX].charAt(0))) {
                                    whiteSpaceInFront = false;
                                }
                                if (whiteSpaceInFront) {
                                    textBuilder.append(' ');
                                }
                            }
                            // If there is a named entity
                            if ((line.length > NE_TYPE_INDEX) && !line[NE_TYPE_INDEX].isEmpty()) {
                                // If this is a new named entity
                                if (line[NE_TYPE_INDEX].equals(ANNOTATION_FIRST_WORD_TAG)) {
                                    if (line[ANNOTATION_TITLE_INDEX].equals(ANNOTATION_NOT_IN_WIKI_TAG)) {
                                        uris = generateArtificialUri(documentUriPrefix,
                                                line[ANNOTATION_SURFACE_FORM_INDEX]);
                                    } else {
                                        // Add the DBpdia URI if this is a wiki
                                        // URI
                                        if (line[ANNOTATION_URI_INDEX].startsWith(WIKIPEDIA_URI_START)) {
                                            uris = WikipediaHelper.generateUriSet(
                                                    line[ANNOTATION_URI_INDEX].substring(WIKIPEDIA_URI_START.length()));
                                        } else {
                                            LOGGER.warn(
                                                    "Found a URI that is not part of the English Wikipedia \"{}\". This was not expected.",
                                                    line[ANNOTATION_URI_INDEX]);
                                            uris = new HashSet<String>();
                                        }
                                        uris.add(line[ANNOTATION_URI_INDEX]);
                                    }
                                    lastNE = new NamedEntity(textBuilder.length(), 0, uris);
                                    markings.add(lastNE);
                                }
                            } else {
                                lastNE = null;
                            }
                            textBuilder.append(line[TEXT_INDEX]);
                            if (lastNE != null) {
                                lastNE.setLength(textBuilder.length() - lastNE.getStartPosition());
                            }
                        }
                    }
                }
                line = reader.readNext();
            }
            // set the text of the last document
            if (currentDoc != null) {
                currentDoc.setText(textBuilder.toString().trim());
                textBuilder.setLength(0);
            }
        } catch (IOException e) {
            throw new GerbilException("Couldn't read dataset file.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(bReader);
        }
        return documents;
    }

    protected Set<String> generateArtificialUri(String uriPrefix, String surfaceForm) throws GerbilException {
        StringBuilder builder = new StringBuilder();
        builder.append(uriPrefix);
        builder.append("notInWiki/");
        try {
            builder.append(URLEncoder.encode(surfaceForm, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Couldn't encode surface form data.", e);
            throw new GerbilException("Couldn't encode surface form data.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        Set<String> uris = new HashSet<String>(2);
        uris.add(builder.toString());
        return uris;
    }

}
