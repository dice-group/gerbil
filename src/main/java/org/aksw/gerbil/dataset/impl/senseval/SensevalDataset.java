package org.aksw.gerbil.dataset.impl.senseval;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.xml.sax.InputSource;

public class SensevalDataset extends AbstractDataset implements InitializableDataset {

	protected List<Document> documents;
	private String wordsFile;
	private Boolean senseval3;
	
	public SensevalDataset(String wordsFile){
		this(wordsFile, "false");
	}
	
	public SensevalDataset(String wordsFile, String senseval3){
		this.wordsFile = wordsFile;
		this.senseval3 = Boolean.valueOf(senseval3);
		documents = new ArrayList<Document>();
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
		this.documents = loadDocuments(new File(this.wordsFile));
	}

	private List<Document> loadDocuments(File file) throws GerbilException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser=null;
		
		try{
			InputSource is;
			if(senseval3){
				//FIXME: Better solution, its just one line where & is as content
				String content = org.apache.commons.io.FileUtils.readFileToString(new File(this.wordsFile), "UTF-8");
				content = content.replace("&", "&amp;").trim();
				is = new InputSource(new ByteArrayInputStream(content.getBytes()));
				is.setEncoding("UTF-8");
			}
			else{
				is  = new InputSource(new FileInputStream(file));
				is.setEncoding("UTF-8");
			}
			saxParser = factory.newSAXParser();
			saxParser.parse(is, new SensevalSAXHandler(documents));
		} catch (Exception e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		}
		
		
		return documents;
	}

	
}
