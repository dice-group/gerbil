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
package org.aksw.gerbil.dataset.impl.iitb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IITBDataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(IITBDataset.class);

    protected List<Document> documents;
    protected String textsDirectory;
    protected String annotationsDirectory;
    protected int unknownEntitiesCount = 0;

    public IITBDataset(String textsDirectory, String annotationsDirectory) throws GerbilException {
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

    protected List<Document> loadDocuments(File textDir, File annoFile) throws GerbilException {
        if ((!textDir.exists()) || (!textDir.isDirectory())) {
            throw new GerbilException(
                    "The given text directory (" + textDir.getAbsolutePath() + ") is not existing or not a directory.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        String textDirPath = textDir.getAbsolutePath();
        if (!textDirPath.endsWith(File.separator)) {
            textDirPath = textDirPath + File.separator;
        }
        if (!annoFile.exists()) {
            throw new GerbilException("The given annotation file (" + annoFile.getAbsolutePath() + ") does not exist.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        Map<String, Set<IITB_Annotation>> documentAnnotationsMap = loadAnnotations(annoFile);
        String text;
        List<Document> documents = new ArrayList<Document>();
        for (String textFile : documentAnnotationsMap.keySet()) {
            // read the text file
            try {
                text = FileUtils.readFileToString(new File(textDirPath + textFile));
            } catch (IOException e) {
                throw new GerbilException("Couldn't read text file \"" + textDirPath + textFile + "\".", e,
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            // create document
            documents.add(createDocument(textFile, text, documentAnnotationsMap.get(textFile)));
        }
        return documents;
    }

    protected Map<String, Set<IITB_Annotation>> loadAnnotations(File annotationsFile) throws GerbilException {
        IITB_XMLParser parser = new IITB_XMLParser();
        try {
            return parser.parseAnnotationsFile(annotationsFile);
        } catch (Exception e) {
            throw new GerbilException(
                    "Couldn't parse given annotation file (\"" + annotationsFile.getAbsolutePath() + "\".", e,
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
    }

    protected Document createDocument(String fileName, String text, Set<IITB_Annotation> annotations) {
        String documentUri = generateDocumentUri(fileName);
        List<Marking> markings = new ArrayList<Marking>(annotations.size());
        int endPosition;
        Set<String> uris;
        for (IITB_Annotation annotation : annotations) {
            endPosition = annotation.offset + annotation.length;
            if ((annotation.offset > 0) && (Character.isAlphabetic(text.charAt(annotation.offset - 1)))) {
                LOGGER.warn("In document " + documentUri + ", the named entity \""
                        + text.substring(annotation.offset, annotation.offset + annotation.length)
                        + "\" has an alphabetic character in front of it (\"" + text.charAt(annotation.offset - 1)
                        + "\").");
            }
            if (Character.isWhitespace(text.charAt(annotation.offset))) {
                LOGGER.warn("In document " + documentUri + ", the named entity \""
                        + text.substring(annotation.offset, endPosition) + "\" starts with a whitespace.");
            }
            if ((endPosition < text.length()) && Character.isAlphabetic(text.charAt(endPosition))) {
                LOGGER.warn("In document " + documentUri + ", the named entity \""
                        + text.substring(annotation.offset, endPosition)
                        + "\" has an alphabetic character directly behind it (\"" + text.charAt(endPosition) + "\").");
            }
            if (Character.isWhitespace(text.charAt(endPosition - 1))) {
                LOGGER.warn("In document " + documentUri + ", the named entity \""
                        + text.substring(annotation.offset, annotation.offset + annotation.length)
                        + "\" ends with a whitespace.");
            }
            uris = WikipediaHelper.generateUriSet(annotation.wikiTitle);
            if (uris.size() == 0) {
                uris.add(generateEntityUri());
            }
            markings.add(new NamedEntity(annotation.offset, annotation.length, uris));
        }
        return new DocumentImpl(text, documentUri, markings);
    }

    private String generateEntityUri() {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append("/notInWiki/entity_");
        builder.append(unknownEntitiesCount);
        ++unknownEntitiesCount;
        return builder.toString();
    }

    protected String generateDocumentUri(String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(fileName);
        return builder.toString();
    }

}
