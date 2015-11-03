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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MSNBC_XMLHandler extends DefaultHandler implements MSNBC_Result {

    private static final Logger LOGGER = LoggerFactory.getLogger(MSNBC_XMLHandler.class);

    private static final String DOCUMENT_TAG_NAME = "ReferenceProblem";
    private static final String DOCUMENT_FILE_NAME_TAG_NAME = "ReferenceFileName";
    private static final String MARKING_TAG_NAME = "ReferenceInstance";
    private static final String MARKING_SURFACE_FORM_TAG_NAME = "SurfaceForm";
    private static final String MARKING_OFFSET_TAG_NAME = "Offset";
    private static final String MARKING_LENGH_TAG_NAME = "Length";
    private static final String MARKING_MEANING_TAG_NAME = "ChosenAnnotation";
    private static final String MARKING_NUMBER_OF_ANNOTATORS_TAG_NAME = "NumAnnotators";
    private static final String MARKING_ANNOTATOR_ID_TAG_NAME = "AnnotatorId";
    private static final String MARKING_ANNOTATION_TAG_NAME = "Annotation";

    protected String textFileName;
    protected List<MSNBC_NamedEntity> nes = new ArrayList<MSNBC_NamedEntity>();
    protected int state = 0;
    protected MSNBC_NamedEntity currentNE;
    protected StringBuilder buffer = new StringBuilder();

    public List<MSNBC_NamedEntity> getMarkings() {
        return nes;
    }

    public String getTextFileName() {
        return textFileName;
    }

    @Override
    public void startDocument() throws SAXException {
        state = 0;
        textFileName = null;
        nes.clear();
        currentNE = null;
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
        case DOCUMENT_FILE_NAME_TAG_NAME: {
            state = 1;
            buffer.setLength(0);
            break;
        }
        case MARKING_TAG_NAME: {
            currentNE = new MSNBC_NamedEntity();
            break;
        }
        case MARKING_SURFACE_FORM_TAG_NAME: {
            state = 2;
            buffer.setLength(0);
            break;
        }
        case MARKING_OFFSET_TAG_NAME: {
            state = 3;
            buffer.setLength(0);
            break;
        }
        case MARKING_LENGH_TAG_NAME: {
            state = 4;
            buffer.setLength(0);
            break;
        }
        case MARKING_MEANING_TAG_NAME: {
            state = 5;
            buffer.setLength(0);
            break;
        }
        case DOCUMENT_TAG_NAME: // falls through
        case MARKING_NUMBER_OF_ANNOTATORS_TAG_NAME:
        case MARKING_ANNOTATOR_ID_TAG_NAME:
        case MARKING_ANNOTATION_TAG_NAME: {
            state = 0;
            break;
        }
        default: {
            LOGGER.warn("Found an unknown XML tag name \"" + localName + "\". It will be ignored.");
            break;
        }
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (state > 0) {
            buffer.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
        case DOCUMENT_FILE_NAME_TAG_NAME: {
            textFileName = buffer.toString().trim();
            break;
        }
        case MARKING_TAG_NAME: {
            if (currentNE.isComplete()) {
                nes.add(currentNE);
            } else {
                LOGGER.warn("Got an incomplete named entity " + currentNE.toString() + ". It will be discarded.");
            }
            currentNE = null;
            break;
        }
        case MARKING_SURFACE_FORM_TAG_NAME: {
            if (currentNE != null) {
                currentNE.setSurfaceForm(buffer.toString().trim());
            } else {
                LOGGER.error("Found a \"" + MARKING_SURFACE_FORM_TAG_NAME + "\" tag outside of a \"" + MARKING_TAG_NAME
                        + "\" tag. It will be ignored.");
            }
            break;
        }
        case MARKING_OFFSET_TAG_NAME: {
            if (currentNE != null) {
                try {
                    int offset = Integer.parseInt(buffer.toString().trim());
                    currentNE.setStartPosition(offset);
                } catch (NumberFormatException e) {
                    LOGGER.error("Couldn't parse the start position of a named entity. buffer=\"" + buffer + "\"");
                }
            } else {
                LOGGER.error("Found a \"" + MARKING_OFFSET_TAG_NAME + "\" tag outside of a \"" + MARKING_TAG_NAME
                        + "\" tag. It will be ignored.");
            }
            break;
        }
        case MARKING_LENGH_TAG_NAME: {
            if (currentNE != null) {
                try {
                    int length = Integer.parseInt(buffer.toString().trim());
                    currentNE.setLength(length);
                } catch (NumberFormatException e) {
                    LOGGER.error("Couldn't parse the length of a named entity. buffer=\"" + buffer + "\"");
                }
            } else {
                LOGGER.error("Found a \"" + MARKING_LENGH_TAG_NAME + "\" tag outside of a \"" + MARKING_TAG_NAME
                        + "\" tag. It will be ignored.");
            }
            break;
        }
        case MARKING_MEANING_TAG_NAME: {
            if (currentNE != null) {
                currentNE.addUri(buffer.toString().trim());
            } else {
                LOGGER.error("Found a \"" + MARKING_MEANING_TAG_NAME + "\" tag outside of a \"" + MARKING_TAG_NAME
                        + "\" tag. It will be ignored.");
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
