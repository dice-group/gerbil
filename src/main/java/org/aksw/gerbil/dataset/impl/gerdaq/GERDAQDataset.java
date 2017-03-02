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
package org.aksw.gerbil.dataset.impl.gerdaq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GERDAQDataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(GERDAQDataset.class);

    private static final String WIKIPEDIA_URI = "http://en.wikipedia.org/wiki/";
    private static final String DBPEDIA_URI = "http://dbpedia.org/resource/";
    private static final String ANNOTATION_TAG = "annotation";
    private static final String DOCUMENT_TAG = "instance";

    private String file;
    private List<Document> documents;

    public GERDAQDataset(String file) {
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

    protected static String generateDocumentUri(String datasetName, String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(datasetName.replace(' ', '_'));
        builder.append('/');
        builder.append(fileName);
        builder.append('_');
        return builder.toString();
    }

    private List<Document> loadDocuments(File filePath) throws GerbilException {
        List<Document> docs = new ArrayList<>();
        if (!filePath.exists()) {
            throw new GerbilException("The given file (" + filePath.getAbsolutePath() + ") is not existing.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }

        if (filePath.isDirectory()) {

            String directoryPath = filePath.getAbsolutePath();
            if (!directoryPath.endsWith(File.separator)) {
                directoryPath = directoryPath + File.separator;
            }

            for (File tmpFile : new File(directoryPath).listFiles()) {
                docs.addAll(createDocument(tmpFile));
            }

        } else {
            docs.addAll(createDocument(filePath));
        }

        return docs;

    }

    private List<Document> createDocument(File file) throws GerbilException {
        List<Document> documents = new ArrayList<Document>();
        String documentUriStart = generateDocumentUri(name, file.getName());
        InputStream inputStream = null;
        InputSource is = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            is = new InputSource(inputStream);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(is, new DefaultHandler() {

                private StringBuilder text = new StringBuilder();
                private int markingStart;
                private String markingTitle;
                private List<Marking> markings;

                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                }

                @Override
                public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
                        throws SAXException {

                    if (qName.equals(ANNOTATION_TAG)) {
                        markingTitle = atts.getValue("rank_0_title");
                        if (markingTitle != null) {
                            markingStart = text.length();
                        } else {
                            LOGGER.error("Found a marking without the necessary \"rank_0_title\" attribute.");
                        }
                        markingTitle = markingTitle.replace(' ', '_');
                    } else if (qName.equals(DOCUMENT_TAG)) {
                        this.markings = new ArrayList<>();
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    text.append(ch, start, length);
                }

                @Override
                public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                    if (qName.equals(DOCUMENT_TAG)) {
                        documents.add(new DocumentImpl(text.toString(), documentUriStart + documents.size(), markings));
                        text.delete(0, text.length());
                    } else if (qName.equals(ANNOTATION_TAG) && (markingTitle != null)) {
                        markings.add(new NamedEntity(markingStart, text.length() - markingStart, new HashSet<String>(
                                Arrays.asList(DBPEDIA_URI + markingTitle, WIKIPEDIA_URI + markingTitle))));
                    }
                }
            });
        } catch (Exception e) {
            throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return documents;
    }

}
