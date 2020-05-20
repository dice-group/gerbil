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

import java.util.HashMap;
import java.util.Map;

/**
 * Class to help define the structure of XML document in java. Its instance can
 * be used during the parsing of documents from a XML based dataset. It allows
 * the user to provide the name of XML tags containing the requisite information
 * 
 * @author Nikit
 *
 */
public class CommonXMLTagDef {
	public static final int DOC_LABEL_CODE = 1;
	public static final int TEXT_LABEL_CODE = 11;
	public static final int ID_LABEL_CODE = 12;
	public static final int MARK_COL_LABEL_CODE = 13;
	public static final int MARK_LABEL_CODE = 131;
	public static final int STRT_LABEL_CODE = 1311;
	public static final int LEN_LABEL_CODE = 1312;
	public static final int ENT_NM_LABEL_CODE = 1313;
	public static final int ENT_URI_LABEL_CODE = 1314;

	private String docLabel;
	private String textLabel;
	private String idLabel;
	private String markColLabel;
	private String markLabel;
	private String strtLabel;
	private String lenLabel;
	private String entityNameLabel;
	private String entityUriLabel;

	private Map<String, Integer> codeMap;

	public CommonXMLTagDef(String doc_label, String text_label, String id_label, String mark_col_label,
			String mark_label, String strt_label, String len_label, String entity_name_label, String entity_uri_label) {

		this.codeMap = new HashMap<>();

		this.docLabel = doc_label;
		if (this.docLabel != null)
			this.codeMap.put(this.docLabel, DOC_LABEL_CODE);
		this.textLabel = text_label;
		if (this.textLabel != null)
			this.codeMap.put(this.textLabel, TEXT_LABEL_CODE);
		this.idLabel = id_label;
		if (this.idLabel != null)
			this.codeMap.put(this.idLabel, ID_LABEL_CODE);
		this.markColLabel = mark_col_label;
		if (this.markColLabel != null)
			this.codeMap.put(this.markColLabel, MARK_COL_LABEL_CODE);
		this.markLabel = mark_label;
		if (this.markLabel != null)
			this.codeMap.put(this.markLabel, MARK_LABEL_CODE);
		this.strtLabel = strt_label;
		if (this.strtLabel != null)
			this.codeMap.put(this.strtLabel, STRT_LABEL_CODE);
		this.lenLabel = len_label;
		if (this.lenLabel != null)
			this.codeMap.put(this.lenLabel, LEN_LABEL_CODE);
		this.entityNameLabel = entity_name_label;
		if (this.entityNameLabel != null)
			this.codeMap.put(this.entityNameLabel, ENT_NM_LABEL_CODE);
		this.entityUriLabel = entity_uri_label;
		if (this.entityUriLabel != null)
			this.codeMap.put(this.entityUriLabel, ENT_URI_LABEL_CODE);

	}

	public String getDocLabel() {
		return docLabel;
	}

	public void setDocLabel(String docLabel) {
		this.docLabel = docLabel;
	}

	public String getTextLabel() {
		return textLabel;
	}

	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	public String getIdLabel() {
		return idLabel;
	}

	public void setIdLabel(String idLabel) {
		this.idLabel = idLabel;
	}

	public String getMarkColLabel() {
		return markColLabel;
	}

	public void setMarkColLabel(String markColLabel) {
		this.markColLabel = markColLabel;
	}

	public String getMarkLabel() {
		return markLabel;
	}

	public void setMarkLabel(String markLabel) {
		this.markLabel = markLabel;
	}

	public String getStrtLabel() {
		return strtLabel;
	}

	public void setStrtLabel(String strtLabel) {
		this.strtLabel = strtLabel;
	}

	public String getLenLabel() {
		return lenLabel;
	}

	public void setLenLabel(String lenLabel) {
		this.lenLabel = lenLabel;
	}

	public String getEntityNameLabel() {
		return entityNameLabel;
	}

	public void setEntityNameLabel(String entityNameLabel) {
		this.entityNameLabel = entityNameLabel;
	}

	public String getEntityUriLabel() {
		return entityUriLabel;
	}

	public void setEntityUriLabel(String entityUriLabel) {
		this.entityUriLabel = entityUriLabel;
	}

	public Map<String, Integer> getCodeMap() {
		return codeMap;
	}

	public void setCodeMap(Map<String, Integer> codeMap) {
		this.codeMap = codeMap;
	}

	public Integer getMatchCode(String qName) {
		return codeMap.get(qName);
	}

}
