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
package org.aksw.gerbil.dataset.impl.generic;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Generic_XMLHandler extends DefaultHandler implements Generic_Result {

	private static final Logger LOGGER = LoggerFactory.getLogger(Generic_XMLHandler.class);
	// To be provided by user
	private XML_DS_TagDef tagDef;
	private String dsName;
	// fetched from XML
	private String curDocId;
	private String curDocText;
	private List<Document> documents;
	private Document curDoc;
	protected List<Generic_NamedEntity> nes;
	protected boolean readSw = false;
	protected Generic_NamedEntity currentNE;
	protected StringBuilder buffer = new StringBuilder();

	@Override
	public List<Document> getDocuments() {
		return this.documents;
	}

	public Generic_XMLHandler(String dsName, XML_DS_TagDef tagDef) {
		this.dsName = dsName;
		this.tagDef = tagDef;
		this.documents = new ArrayList<>();
	}

	@Override
	public void startDocument() throws SAXException {
		// state = 0;
		// nes.clear();
		currentNE = null;
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		readSw = false;
		if (tagDef.getMark_col_label().equals(qName)) {
			nes = new ArrayList<Generic_NamedEntity>();
		} else if (tagDef.getMark_label().equals(qName)) {
			currentNE = new Generic_NamedEntity();
		} else if (tagDef.getMatchCode(qName) != null) {
			buffer.setLength(0);
			readSw = true;
		}
		// LOGGER.warn("Found an unknown XML tag name \"" + localName + "\". It will be
		// ignored.");
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
		case XML_DS_TagDef.DOC_LABEL_CODE: {
			curDoc = Generic_Utils.createDocument(dsName, curDocId, curDocText, nes);
			// Add document to list and reset values
			documents.add(curDoc);
			curDocId = null;
			curDoc = null;
			curDocText = null;
			break;
		}
		case XML_DS_TagDef.ID_LABEL_CODE: {
			curDocId = buffer.toString().trim();
			break;
		}
		case XML_DS_TagDef.TEXT_LABEL_CODE: {
			curDocText = buffer.toString().trim();
			break;
		}
		case XML_DS_TagDef.MARK_LABEL_CODE: {
			if (currentNE.isComplete()) {
				nes.add(currentNE);
			} else {
				LOGGER.warn("Got an incomplete named entity " + currentNE.toString() + ". It will be discarded.");
			}
			currentNE = null;
			break;
		}
		case XML_DS_TagDef.ENT_NM_LABEL_CODE: {
			if (currentNE != null) {
				currentNE.setSurfaceForm(buffer.toString().trim());
				currentNE.setLength(currentNE.getSurfaceForm().length());
			} else {
				LOGGER.error("Found a \"" + tagDef.getEntity_name_label() + "\" tag outside of a \""
						+ tagDef.getMark_label() + "\" tag. It will be ignored.");
			}
			break;
		}
		case XML_DS_TagDef.STRT_LABEL_CODE: {
			if (currentNE != null) {
				try {
					int offset = Integer.parseInt(buffer.toString().trim());
					currentNE.setStartPosition(offset);
				} catch (NumberFormatException e) {
					LOGGER.error("Couldn't parse the start position of a named entity. buffer=\"" + buffer + "\"");
				}
			} else {
				LOGGER.error("Found a \"" + tagDef.getStrt_label() + "\" tag outside of a \"" + tagDef.getMark_label()
						+ "\" tag. It will be ignored.");
			}
			break;
		}
		case XML_DS_TagDef.LEN_LABEL_CODE: {
			if (currentNE != null) {
				try {
					int length = Integer.parseInt(buffer.toString().trim());
					currentNE.setLength(length);
				} catch (NumberFormatException e) {
					LOGGER.error("Couldn't parse the length of a named entity. buffer=\"" + buffer + "\"");
				}
			} else {
				LOGGER.error("Found a \"" + tagDef.getLen_label() + "\" tag outside of a \"" + tagDef.getMark_label()
						+ "\" tag. It will be ignored.");
			}
			break;
		}
		case XML_DS_TagDef.ENT_URI_LABEL_CODE: {
			if (currentNE != null) {
				currentNE.addUri(buffer.toString().trim());
			} else {
				LOGGER.error("Found a \"" + tagDef.getEntity_uri_label() + "\" tag outside of a \""
						+ tagDef.getMark_label() + "\" tag. It will be ignored.");
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
