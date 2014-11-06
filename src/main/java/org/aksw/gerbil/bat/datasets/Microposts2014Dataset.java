package org.aksw.gerbil.bat.datasets;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;
import it.unimi.dsi.lang.MutableString;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * ...
 *
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class Microposts2014Dataset implements A2WDataset {

	private List<HashSet<Annotation>> annotations = new Vector<HashSet<Annotation>>();
	private List<MutableString> tweets = new Vector<MutableString>();
    private Pattern dbpediaUrlPattern = Pattern.compile("http://dbpedia.org/resource/(.*)");
    private Pattern recordPattern = Pattern.compile("([0-9]+)(\t\".+\")(\t)*(.*)");
    private Pattern textPattern = Pattern.compile("^\"(.+)\"$");
    
    public Microposts2014Dataset(String file, WikipediaApiInterface wikiApi) 
    		throws	IOException, 
    				XPathExpressionException, 
    				ParserConfigurationException, 
    				SAXException 
	{
		List<HashSet<Microposts2014Annotation>> mAnns = new Vector<HashSet<Microposts2014Annotation>>();
		List<String> titlesToPrefetch = new Vector<String>();
		BufferedReader r = new BufferedReader( new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		String line;
		
		HashSet<Microposts2014Annotation> currentAnns = null;
		String currentTitle = null;
		while ((line = r.readLine()) != null) 
		{
			int currentPos = 1;
			currentAnns = new HashSet<Microposts2014Annotation>();
			mAnns.add(currentAnns);
			
			Matcher mRecord = recordPattern.matcher(line);
			if(mRecord.matches())
			{
				String tweetQuoted = (mRecord.group(2)).trim();
				Matcher mTweet = textPattern.matcher(tweetQuoted);

				if(mTweet.matches())
				{
					//current tweet
					tweets.add(new MutableString( mTweet.group(1)) );
					
					String pairs = mRecord.group(4);
					if(pairs != null && !pairs.equals("")) 
					{
						String[] tAnn = pairs.split("\t");
						for(int i=0; i<tAnn.length;i=i+2) {
							// fetch the DBpedia name
							// TODO: naive assumption that all DBpedia resources have a corresponding Wikipedia ones
							//       better to be verified
							System.out.println(mRecord.group(1));
							Matcher m = dbpediaUrlPattern.matcher(tAnn[i+1]);
							if(m.matches()) {
								currentTitle = m.group(1);
								currentAnns.add(new Microposts2014Annotation(currentPos, currentTitle));
								titlesToPrefetch.add(currentTitle);
								currentPos++;
							}						
						
						}				
					}
				}
						    	
			}
			else {
				r.close();
				throw new AnnotationException("Dataset is malformed: each record should have "
						+ "1st=tweet_id, 2nd=tweet_text, 3rd=list of pairs <mentions,uri> seperated by tab. "
						+ "The 3 fields have to be separated by tabs as well");
			}
			
		}
		r.close();

		/** Prefetch titles */
		wikiApi.prefetchTitles(titlesToPrefetch);

		/** Create annotation list */
		for (HashSet<Microposts2014Annotation> s : mAnns){
			HashSet<Annotation> sA = new HashSet<Annotation>();
			for (Microposts2014Annotation aA: s){
				int wid = wikiApi.getIdByTitle(aA.title);
				if (wid == -1)
					System.out.println("ERROR: Dataset is malformed: Wikipedia API could not find page "+aA.title);
				else
					sA.add(new Annotation(aA.position, aA.title.length(), wid));
			}
			HashSet<Annotation> sANonOverlapping = Annotation.deleteOverlappingAnnotations(sA);
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
		for (HashSet<Annotation> s : annotations)
			count += s.size();
		return count;
	}

	@Override
	public List<HashSet<Tag>> getC2WGoldStandardList() {
		return ProblemReduction.A2WToC2WList(annotations);
	}

	@Override
	public List<HashSet<Annotation>> getA2WGoldStandardList() {
		return annotations;
	}

	@Override
	public List<HashSet<Annotation>> getD2WGoldStandardList() {
		return getA2WGoldStandardList();
	}

	@Override
	public List<String> getTextInstanceList() {
		List<String> stringDocuments = new Vector<String>();
		for (MutableString s : tweets){
			stringDocuments.add(s.toString());
		}
		return stringDocuments;
	}
	
	@Override
	public List<HashSet<Mention>> getMentionsInstanceList() {
		return ProblemReduction.A2WToD2WMentionsInstance(getA2WGoldStandardList());
	}

	@Override
	public String getName() {
		return "Microposts2014";
	}

	private class Microposts2014Annotation{
		public Microposts2014Annotation(int position, String title) {
			this.position = position;
			this.title = title;
		}
		public String title;
		public int position;
	}

}
