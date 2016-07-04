package org.aksw.gerbil.dataset.impl.senseval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;

public class SensevalDataset extends AbstractDataset implements InitializableDataset {

	protected List<Document> documents;
	private String wordsFile;
	
	public SensevalDataset(String wordsFile){
		this.wordsFile = wordsFile;
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
			saxParser = factory.newSAXParser();
			saxParser.parse(file, new SensevalSAXHandler(documents));
		
		} catch (Exception e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		}
		
		
		return documents;
	}

	
}
