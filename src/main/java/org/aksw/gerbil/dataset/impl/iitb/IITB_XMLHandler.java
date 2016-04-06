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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IITB_XMLHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IITB_XMLHandler.class);

    private static final String ANNOTATIONS_LIST_TAG_NAME = "iitb.CSAW.entityAnnotations";
    private static final String ANNOTATION_TAG_NAME = "annotation";
    private static final String DOCUMENT_FILE_NAME_TAG_NAME = "docName";
    private static final String WIKI_TITLE_TAG_NAME = "wikiName";
    private static final String ANNOTATION_OFFSET_TAG_NAME = "offset";
    private static final String ANNOTATION_LENGH_TAG_NAME = "length";
    private static final String USER_ID_TAG_NAME = "userId";

    protected Map<String, Set<IITB_Annotation>> documentAnnotationsMap = new HashMap<String, Set<IITB_Annotation>>();
    protected IITB_Annotation currentAnnotation;
    protected StringBuilder buffer = new StringBuilder();

    public Map<String, Set<IITB_Annotation>> getDocumentAnnotationsMap() {
        return documentAnnotationsMap;
    }

    @Override
    public void startDocument() throws SAXException {
        currentAnnotation = null;
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
        case DOCUMENT_FILE_NAME_TAG_NAME: // falls through
        case WIKI_TITLE_TAG_NAME:
        case ANNOTATION_OFFSET_TAG_NAME:
        case ANNOTATION_LENGH_TAG_NAME: {
            buffer.setLength(0);
            break;
        }
        case ANNOTATION_TAG_NAME: {
            currentAnnotation = new IITB_Annotation();
            break;
        }
        case ANNOTATIONS_LIST_TAG_NAME: // falls through
        case USER_ID_TAG_NAME: {
            // nothing to do
            break;
        }
        default: {
            LOGGER.warn("Found an unknown XML tag name \"" + qName + "\". It will be ignored.");
            break;
        }
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (currentAnnotation != null) {
            buffer.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
        case ANNOTATION_TAG_NAME: {
            if (currentAnnotation.isComplete()) {
                Set<IITB_Annotation> annotations;
                if (documentAnnotationsMap.containsKey(currentAnnotation.documentName)) {
                    annotations = documentAnnotationsMap.get(currentAnnotation.documentName);
                } else {
                    annotations = new HashSet<IITB_Annotation>();
                    documentAnnotationsMap.put(currentAnnotation.documentName, annotations);
                }
                annotations.add(currentAnnotation);
            } else {
                LOGGER.warn(
                        "Got an incomplete named entity " + currentAnnotation.toString() + ". It will be discarded.");
            }
            currentAnnotation = null;
            break;
        }
        case DOCUMENT_FILE_NAME_TAG_NAME: {
            if (currentAnnotation != null) {
                currentAnnotation.documentName = buffer.toString().trim();
            } else {
                LOGGER.warn("Found a tag (\"" + DOCUMENT_FILE_NAME_TAG_NAME
                        + "\") without an open annotation. It will be ignored.");
            }
            break;
        }
        case WIKI_TITLE_TAG_NAME: {
            if (currentAnnotation != null) {
                currentAnnotation.wikiTitle = buffer.toString().trim();
            } else {
                LOGGER.warn("Found a tag (\"" + WIKI_TITLE_TAG_NAME
                        + "\") without an open annotation. It will be ignored.");
            }
            break;
        }
        case ANNOTATION_OFFSET_TAG_NAME: {
            if (currentAnnotation != null) {
                try {
                    currentAnnotation.offset = Integer.parseInt(buffer.toString().trim());
                } catch (NumberFormatException e) {
                    LOGGER.error("Couldn't parse the offset of an annotation. buffer=\"" + buffer + "\"", e);
                }
            } else {
                LOGGER.warn("Found a tag (\"" + ANNOTATION_OFFSET_TAG_NAME
                        + "\") without an open annotation. It will be ignored.");
            }
            break;
        }
        case ANNOTATION_LENGH_TAG_NAME: {
            if (currentAnnotation != null) {
                try {
                    currentAnnotation.length = Integer.parseInt(buffer.toString().trim());
                } catch (NumberFormatException e) {
                    LOGGER.error("Couldn't parse the length of an annotation. buffer=\"" + buffer + "\"", e);
                }
            } else {
                LOGGER.warn("Found a tag (\"" + ANNOTATION_LENGH_TAG_NAME
                        + "\") without an open annotation. It will be ignored.");
            }
            break;
        }
        default: {
            // nothing to do
        }
        }
        super.endElement(uri, localName, qName);
    }
}
