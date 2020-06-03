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
package org.aksw.gerbil.dataset.impl.xml;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler class that parses the XML Documents from a dataset into NIF Document
 * 
 * @author Nikit
 *
 */
public class CommonXMLHandler extends DefaultHandler implements GenericResult {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonXMLHandler.class);
	// To be provided by user
	private CommonXMLTagDef tagDef;
	private String dsName;
	// fetched from XML
	private String curDocId;
	private String curDocText;
	private List<Document> documents;
	protected List<XMLNamedEntity> nes;
	protected boolean readSw = false;
	protected XMLNamedEntity currentNE;
	protected StringBuilder buffer = new StringBuilder();

	@Override
	public List<Document> getDocuments() {
		return this.documents;
	}

	public CommonXMLHandler(String dsName, CommonXMLTagDef tagDef) {
		this.dsName = dsName;
		this.tagDef = tagDef;
		this.documents = new ArrayList<>();
	}

	@Override
	public void startDocument() throws SAXException {
		currentNE = null;
		nes = new ArrayList<XMLNamedEntity>();
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		readSw = false;
		if (qName.equals(tagDef.getMarkColLabel())) {
			nes = new ArrayList<XMLNamedEntity>();
		} else if (qName.equals(tagDef.getMarkLabel())) {
			currentNE = new XMLNamedEntity();
		} else if (tagDef.getMatchCode(qName) != null) {
			buffer.setLength(0);
			readSw = true;
		}
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		if (readSw)
			buffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		Integer res = tagDef.getMatchCode(qName);
		if (res == null)
			return;
		switch (res) {
		case CommonXMLTagDef.DOC_LABEL_CODE: {
			Document curDoc = XMLDataUtil.createDocument(dsName, curDocId, curDocText, nes);
			// Add document to list and reset values
			documents.add(curDoc);
			curDocId = null;
			curDocText = null;
			break;
		}
		case CommonXMLTagDef.ID_LABEL_CODE: {
			curDocId = buffer.toString().trim();
			break;
		}
		case CommonXMLTagDef.TEXT_LABEL_CODE: {
			curDocText = buffer.toString().trim();
			break;
		}
		case CommonXMLTagDef.MARK_LABEL_CODE: {
			if (currentNE.isComplete()) {
				nes.add(currentNE);
			} else {
				LOGGER.warn("Got an incomplete named entity " + currentNE.toString() + ". It will be discarded.");
			}
			currentNE = null;
			break;
		}
		case CommonXMLTagDef.ENT_NM_LABEL_CODE: {
			if (currentNE != null) {
				currentNE.setSurfaceForm(buffer.toString().trim());
				currentNE.setLength(currentNE.getSurfaceForm().length());
			} else {
				LOGGER.error("Found a \"" + tagDef.getEntityNameLabel() + "\" tag outside of a \""
						+ tagDef.getMarkLabel() + "\" tag. It will be ignored.");
			}
			break;
		}
		case CommonXMLTagDef.STRT_LABEL_CODE: {
			if (currentNE != null) {
				try {
					int offset = Integer.parseInt(buffer.toString().trim());
					currentNE.setStartPosition(offset);
				} catch (NumberFormatException e) {
					LOGGER.error("Couldn't parse the start position of a named entity. buffer=\"" + buffer + "\"");
				}
			} else {
				LOGGER.error("Found a \"" + tagDef.getStrtLabel() + "\" tag outside of a \"" + tagDef.getMarkLabel()
						+ "\" tag. It will be ignored.");
			}
			break;
		}
		case CommonXMLTagDef.LEN_LABEL_CODE: {
			if (currentNE != null) {
				try {
					int length = Integer.parseInt(buffer.toString().trim());
					currentNE.setLength(length);
				} catch (NumberFormatException e) {
					LOGGER.error("Couldn't parse the length of a named entity. buffer=\"" + buffer + "\"");
				}
			} else {
				LOGGER.error("Found a \"" + tagDef.getLenLabel() + "\" tag outside of a \"" + tagDef.getMarkLabel()
						+ "\" tag. It will be ignored.");
			}
			break;
		}
		case CommonXMLTagDef.ENT_URI_LABEL_CODE: {
			if (currentNE != null) {
				currentNE.addUri(buffer.toString().trim());
			} else {
				LOGGER.error("Found a \"" + tagDef.getEntityUriLabel() + "\" tag outside of a \""
						+ tagDef.getMarkLabel() + "\" tag. It will be ignored.");
			}
			break;
		}
		default: {
			LOGGER.warn("Found an unknown XML tag name \"" + localName + "\". It will be ignored.");
			break;
		}
		}
		super.endElement(uri, localName, qName);
	}

}
