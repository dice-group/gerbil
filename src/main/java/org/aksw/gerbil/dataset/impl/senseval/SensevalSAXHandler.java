package org.aksw.gerbil.dataset.impl.senseval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SensevalSAXHandler extends DefaultHandler {

	public static final String SENTENCE_ELEMENT = "sentence";
	public static final String INSTANCE_ELEMENT = "instance";
	private static final String WF_ELEMENT = "wf";

	private StringBuilder sentence = new StringBuilder();
	private List<Marking> markings = new ArrayList<Marking>();
	private List<Document> documents;
	private int start = 0;
	private int length;
	private int i = 0;
	private String instanceUri;

	private byte field = -1;

	public SensevalSAXHandler(List<Document> documents) {
		this.documents = documents;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase(SENTENCE_ELEMENT)) {
			field = 0;
			markings = new ArrayList<Marking>();
		} else if (qName.equalsIgnoreCase(INSTANCE_ELEMENT)) {
			field = 1;
			length = 0;
			instanceUri = "";
		} else if (qName.equalsIgnoreCase(WF_ELEMENT)) {
			field = 2;
			length = 0;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase(SENTENCE_ELEMENT)) {
			i++;
			documents.add(new DocumentImpl(sentence.toString(),
					"http://senseval" + i, markings));
			sentence = new StringBuilder();
		} else if (qName.equalsIgnoreCase(INSTANCE_ELEMENT)) {
			markings.add(new NamedEntity(start, length, instanceUri));
			start = sentence.length();
		} else if (qName.equalsIgnoreCase(WF_ELEMENT)) {
			start = sentence.length();

		}
		this.field = 0;
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		switch (field) {
		case 0:
			break;
		case 1:
		case 2:
			this.length = length;
			String word = new String(Arrays.copyOfRange(ch, start, start
					+ length));
			if(word.equals("&")){
				word = word.replace("&", "&amp;");
			}
			this.start+= addWordToSentence(word);
		}
		this.field = 0;
		
	}

	public List<Document> getDocuments() {
		return documents;
	}

	private int addWordToSentence(String word) {
		if (sentence.length() == 0) {
			sentence.append(word);
			return 0;
		}

		if (word.matches("(,|\\.|;|:|!|\\?)")) {
			sentence.append(word);
			return 0;
		} 
		else {
			sentence.append(" ").append(word);
			return 1;
		}
	}
}
