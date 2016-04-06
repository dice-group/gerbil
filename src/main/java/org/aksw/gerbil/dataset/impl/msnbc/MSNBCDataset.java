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
package org.aksw.gerbil.dataset.impl.msnbc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MSNBCDataset extends AbstractDataset implements InitializableDataset, Comparator<Span> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MSNBCDataset.class);

    protected List<Document> documents;
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
    public List<Document> getInstances() {
        return documents;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(textsDirectory), new File(annotationsDirectory));
    }

    protected List<Document> loadDocuments(File textDir, File annoDir) throws GerbilException {
        if ((!textDir.exists()) || (!textDir.isDirectory())) {
            throw new GerbilException(
                    "The given text directory (" + textDir.getAbsolutePath() + ") is not existing or not a directory.",
                    ErrorTypes.DATASET_LOADING_ERROR);
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
                throw new GerbilException(
                        "Couldn't parse given annotation file (\"" + annoFile.getAbsolutePath() + "\".", e,
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            if (parsedResult.getTextFileName() == null) {
                throw new GerbilException("The parsed annotation file (\"" + annoFile.getAbsolutePath()
                        + "\" did not define a text file name.", ErrorTypes.DATASET_LOADING_ERROR);
            }
            // read the text file
            try {
                text = FileUtils.readFileToString(new File(textDirPath + parsedResult.getTextFileName()));
            } catch (IOException e) {
                throw new GerbilException(
                        "Couldn't read text file \"" + textDirPath + parsedResult.getTextFileName()
                                + "\" mentioned in the annotations file \"" + annoFile.getAbsolutePath() + "\".",
                        e, ErrorTypes.DATASET_LOADING_ERROR);
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
        Document document = new DocumentImpl(text, documentUri, markings);
        mergeSubNamedEntity(document);
        return document;
    }

    /**
     * Merge {@link NamedEntity}s that are sub spans of another named entity and
     * that have the same URIs.
     * 
     * @param document
     */
    private void mergeSubNamedEntity(Document document) {
        List<NamedEntity> spanList = document.getMarkings(NamedEntity.class);
        NamedEntity nes[] = spanList.toArray(new NamedEntity[spanList.size()]);
        Arrays.sort(nes, this);
        Set<Marking> markingsToRemove = new HashSet<Marking>();
        boolean uriOverlapping;
        Iterator<String> uriIterator;
        for (int i = 0; i < nes.length; ++i) {
            uriOverlapping = false;
            for (int j = i + 1; (j < nes.length) && (!uriOverlapping); ++j) {
                // if nes[i] is a "sub span" of nes[j]
                if ((nes[i].getStartPosition() >= nes[j].getStartPosition()) && ((nes[i].getStartPosition()
                        + nes[i].getLength()) <= (nes[j].getStartPosition() + nes[j].getLength()))) {
                    uriOverlapping = false;
                    uriIterator = nes[i].getUris().iterator();
                    while ((!uriOverlapping) && (uriIterator.hasNext())) {
                        uriOverlapping = nes[j].containsUri(uriIterator.next());
                    }
                    if (uriOverlapping) {
                        nes[j].getUris().addAll(nes[j].getUris());
                        markingsToRemove.add(nes[i]);
                    } else {
                        LOGGER.debug("There are two overlapping named entities with different URI sets. {}, {}", nes[i],
                                nes[j]);
                    }
                }
            }
        }
        document.getMarkings().removeAll(markingsToRemove);
    }

    protected String generateDocumentUri(String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(fileName);
        return builder.toString();
    }

    /**
     * Adds DBpedia URIs by transforming Wikipeda URIs.
     * 
     * @param uris
     */
    protected static void addDBpediaUris(Set<String> uris) {
        List<String> dbpediaUris = new ArrayList<String>(uris.size());
        for (String uri : uris) {
            if (uri.contains("en.wikipedia.org/wiki")) {
                dbpediaUris.add(uri.replace("en.wikipedia.org/wiki", "dbpedia.org/resource"));
            } else {
                dbpediaUris.add(uri.replace("wikipedia.org/wiki", "dbpedia.org/resource"));
            }
        }
        uris.addAll(dbpediaUris);
    }

    @Override
    public int compare(Span s1, Span s2) {
        // sort them based on their length
        int diff = s1.getLength() - s2.getLength();
        if (diff == 0) {
            return 0;
        } else if (diff < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
