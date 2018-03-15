package org.aksw.gerbil.dataset.impl.generic;

import java.util.HashMap;
import java.util.Map;

public class XML_DS_TagDef {
	public static final int DOC_LABEL_CODE = 1;
	public static final int TEXT_LABEL_CODE = 11;
	public static final int ID_LABEL_CODE = 12;
	public static final int MARK_COL_LABEL_CODE = 13;
	public static final int MARK_LABEL_CODE = 131;
	public static final int STRT_LABEL_CODE = 1311;
	public static final int LEN_LABEL_CODE = 1312;
	public static final int ENT_NM_LABEL_CODE = 1313;
	public static final int ENT_URI_LABEL_CODE = 1314;
	
	private String doc_label;
	private String text_label;
	private String id_label;
	private String mark_col_label;
	private String mark_label;
	private String strt_label;
	private String len_label;
	private String entity_name_label;
	private String entity_uri_label;
	
	private Map<String,Integer> codeMap;

	
	public XML_DS_TagDef(String doc_label, String text_label, String id_label, String mark_col_label, String mark_label,
			String strt_label, String len_label, String entity_name_label, String entity_uri_label) {
		
		this.codeMap = new HashMap<>();
		
		this.doc_label = doc_label;
		if(this.doc_label!=null)
			this.codeMap.put(this.doc_label, DOC_LABEL_CODE);
		this.text_label = text_label;
		if(this.text_label!=null)
			this.codeMap.put(this.text_label, TEXT_LABEL_CODE);
		this.id_label = id_label;
		if(this.id_label!=null)
			this.codeMap.put(this.id_label, ID_LABEL_CODE);
		this.mark_col_label = mark_col_label;
		if(this.mark_col_label!=null)
			this.codeMap.put(this.mark_col_label, MARK_COL_LABEL_CODE);
		this.mark_label = mark_label;
		if(this.mark_label!=null)
			this.codeMap.put(this.mark_label, MARK_LABEL_CODE);
		this.strt_label = strt_label;
		if(this.strt_label!=null)
			this.codeMap.put(this.strt_label, STRT_LABEL_CODE);
		this.len_label = len_label;
		if(this.len_label!=null)
			this.codeMap.put(this.len_label, LEN_LABEL_CODE);
		this.entity_name_label = entity_name_label;
		if(this.entity_name_label!=null)
			this.codeMap.put(this.entity_name_label, ENT_NM_LABEL_CODE);
		this.entity_uri_label = entity_uri_label;
		if(this.entity_uri_label!=null)
			this.codeMap.put(this.entity_uri_label, ENT_URI_LABEL_CODE);
		
		
	}
	public String getDoc_label() {
		return doc_label;
	}
	public void setDoc_label(String doc_label) {
		this.doc_label = doc_label;
	}
	public String getText_label() {
		return text_label;
	}
	public void setText_label(String text_label) {
		this.text_label = text_label;
	}
	public String getMark_col_label() {
		return mark_col_label;
	}
	public void setMark_col_label(String mark_col_label) {
		this.mark_col_label = mark_col_label;
	}
	public String getMark_label() {
		return mark_label;
	}
	public void setMark_label(String mark_label) {
		this.mark_label = mark_label;
	}
	public String getStrt_label() {
		return strt_label;
	}
	public void setStrt_label(String strt_label) {
		this.strt_label = strt_label;
	}
	public String getLen_label() {
		return len_label;
	}
	public void setLen_label(String len_label) {
		this.len_label = len_label;
	}
	public String getEntity_name_label() {
		return entity_name_label;
	}
	public void setEntity_name_label(String entity_name_label) {
		this.entity_name_label = entity_name_label;
	}
	public String getEntity_uri_label() {
		return entity_uri_label;
	}
	public void setEntity_uri_label(String entity_uri_label) {
		this.entity_uri_label = entity_uri_label;
	}
	
	public String getId_label() {
		return id_label;
	}

	public void setId_label(String id_label) {
		this.id_label = id_label;
	}

	public Integer getMatchCode(String qName) {
		return codeMap.get(qName);
	}
	
}
