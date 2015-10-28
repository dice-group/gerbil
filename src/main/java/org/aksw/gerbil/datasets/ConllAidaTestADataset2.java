package org.aksw.gerbil.datasets;


import it.unipi.di.acube.batframework.data.*;
import it.unipi.di.acube.batframework.utils.*;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class ConllAidaTestADataset2 extends ConllAidaDataset2{
	private static final int FIRST_DOC_ID = 947;
	private static final int LAST_DOC_ID = 1162;

	public ConllAidaTestADataset2(String file, WikipediaApiInterface api) throws IOException, AnnotationException, XPathExpressionException, ParserConfigurationException, SAXException {
		super(file, api);
	}

	@Override
	public int getSize() {
		return LAST_DOC_ID-FIRST_DOC_ID+1;
	}

	@Override
	public int getTagsCount() {
		int count = 0;
		for (HashSet<Annotation2> s : getA2WGoldStandardList())
			count += s.size();
		return count;
	}

	@Override
	public List<HashSet<Tag2>> getC2WGoldStandardList() {
		return ProblemReduction2.A2WToC2WList(getA2WGoldStandardList());
	}

	@Override
	public List<HashSet<Annotation2>> getA2WGoldStandardList() {
		return super.getA2WGoldStandardList().subList(FIRST_DOC_ID-1, LAST_DOC_ID);
	}

	@Override
	public List<HashSet<Annotation2>> getD2WGoldStandardList() {
		return getA2WGoldStandardList();
	}

	@Override
	public List<String> getTextInstanceList() {
		return super.getTextInstanceList().subList(FIRST_DOC_ID-1, LAST_DOC_ID);
	}
	
	@Override
	public List<HashSet<Mention>> getMentionsInstanceList() {
		return ProblemReduction2.A2WToD2WMentionsInstance(getA2WGoldStandardList());
	}

	@Override
	public String getName() {
		return "AIDA/CO-NLL-TestB";
	}

}
