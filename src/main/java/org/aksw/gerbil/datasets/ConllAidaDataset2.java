package org.aksw.gerbil.datasets;

/**
 * (C) Copyright 2012-2013 A-cube lab - Università di Pisa - Dipartimento di Informatica. 
 * BAT-Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * BAT-Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with BAT-Framework.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import it.unimi.dsi.lang.MutableString;
import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.A2WDataset;
import it.unipi.di.acube.batframework.utils.AnnotationException;
import it.unipi.di.acube.batframework.utils.ProblemReduction;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

public class ConllAidaDataset2 implements A2WDataset2{
	private List<HashSet<Annotation2>> annotations = new Vector<HashSet<Annotation2>>();
	private List<MutableString> documents = new Vector<MutableString>();
	private Pattern wikiUrlPattern = Pattern.compile("http://en.wikipedia.org/wiki/(.*)");
	private Pattern mentionPattern = Pattern.compile("^(.*?)\t([BI]?)\t(.*?)\t(.*?)\t(.*?)(?:\t(.*))?$");
	private Pattern nmePattern = Pattern.compile("^(.*)\t([BI])\t(.*)\t(.*)--NME--$");
	private Pattern punctuationPattern = Pattern.compile("^\\W.*$");


	public ConllAidaDataset2 (String file, WikipediaApiInterface api) throws IOException, AnnotationException, XPathExpressionException, ParserConfigurationException, SAXException{
		List<HashSet<AidaAnnotation>> aidaAnns = new Vector<HashSet<AidaAnnotation>>();
		List<String> titlesToPrefetch = new Vector<String>();
		BufferedReader r = new BufferedReader( new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		String line;
		MutableString currentDoc = null;
		HashSet<AidaAnnotation> currentAnns = null;
		int currentPos = -1, currentLen = 0;
		String currentTitle = null;
		while ((line = r.readLine()) != null){
			Matcher m = mentionPattern.matcher(line);
			Matcher mneMatch = nmePattern.matcher(line);
			MutableString append = new MutableString();
			if ((!m.matches() || m.matches() && m.group(2).equals("B")) && currentPos != -1){ //if any, store the last tag
				currentAnns.add(new AidaAnnotation(currentPos, currentLen, currentTitle));
				currentPos = -1;
				currentLen = 0;
				currentTitle = null;
			}
			if (line.startsWith("-DOCSTART-")){ // a new document
				currentDoc = new MutableString();
				documents.add(currentDoc);
				currentAnns = new HashSet<AidaAnnotation>();
				aidaAnns.add(currentAnns);
			}
			else if (line.equals("")){ // the end of a sentence
				append.replace("\n");
			}
			else if (!m.matches() && !mneMatch.matches()){ // a word not part of a mention
				append.replace(line + " ");
			}
			else if (mneMatch.matches()){ // a word part of a non-recognized mention
				append.replace(mneMatch.group(1) + " ");
			}
			else{ // a word with a recognized mention.
				if (m.group(2).equals("B")){
					Matcher m2 = wikiUrlPattern.matcher(m.group(5));
					if (m2.matches()){
						currentTitle = m2.group(1);
						currentPos = currentDoc.length();
						currentLen = m.group(1).length();
						titlesToPrefetch.add(currentTitle);
					}
					else{
						r.close();
						throw new AnnotationException("Dataset is malformed: string "+m.group(5)+ " should be a wikipedia URL. Line=["+line+"]");
					}
				}
				else {
					if (!m.group(2).equals("B") && !m.group(2).equals("I")){
						r.close();
						throw new AnnotationException("Dataset is malformed: all mention should be marked as B or I. Bad mention: "+line);
					}// found mention is a continuation
					currentLen += m.group(1).length()+1;
				}
				append.replace(m.group(1) + " ");
			}

			//* Should the last whitespace be removed? */
			Matcher punctuationMatch = punctuationPattern.matcher(append);
			if (punctuationMatch.matches())
				currentDoc.trimRight();
			currentDoc.append(append);

		}
		r.close();

		/** Prefetch titles */
		api.prefetchTitles(titlesToPrefetch);

		/** Create annotation list */
		for (HashSet<AidaAnnotation> s : aidaAnns){
			HashSet<Annotation2> sA = new HashSet<Annotation2>();
			for (AidaAnnotation aA: s){
//				int wid = api.getIdByTitle(aA.title);
//				if (wid == -1)
//					System.out.println("ERROR: Dataset is malformed: Wikipedia API could not find page "+aA.title);
//				else
					sA.add(new Annotation2(aA.position, aA.length, aA.title));
			}
			HashSet<Annotation2> sANonOverlapping = Annotation2.deleteOverlappingAnnotations(sA);
			annotations.add(sANonOverlapping);
			
		}
	}

	@Override
	public int getSize() {
		return annotations.size();
	}

	@Override
	public int getTagsCount() {
		int count = 0;
		for (HashSet<Annotation2> s : annotations)
			count += s.size();
		return count;
	}

	@Override
	public List<HashSet<Tag2>> getC2WGoldStandardList() {
		return ProblemReduction2.A2WToC2WList(annotations);
	}

	@Override
	public List<HashSet<Annotation2>> getA2WGoldStandardList() {
		return annotations;
	}

	@Override
	public List<HashSet<Annotation2>> getD2WGoldStandardList() {
		return getA2WGoldStandardList();
	}

	@Override
	public List<String> getTextInstanceList() {
		List<String> stringDocuments = new Vector<String>();
		for (MutableString s : documents){
			stringDocuments.add(s.toString());
		}
		return stringDocuments;
	}
	
	@Override
	public List<HashSet<Mention>> getMentionsInstanceList() {
		return ProblemReduction2.A2WToD2WMentionsInstance(getA2WGoldStandardList());
	}

	@Override
	public String getName() {
		return "AIDA/CO-NLL";
	}

	private class AidaAnnotation{
		public AidaAnnotation(int pos, int len, String title) {
			this.length = len;
			this.position = pos;
			this.title = title;
		}
		public int length, position;
		public String title;
	}
}
