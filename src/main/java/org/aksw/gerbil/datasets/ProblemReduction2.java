package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class ProblemReduction2 {

	
	public static List<HashSet<Mention>> A2WToD2WMentionsInstance(
			List<HashSet<Annotation2>> annotations) {
		List<HashSet<Mention>> result = new Vector<HashSet<Mention>>();
		for (HashSet<Annotation2> as : annotations) {
			HashSet<Mention> resSet = new HashSet<Mention>();
			result.add(resSet);
			for (Annotation2 a : as)
				resSet.add(new Mention(a.getPosition(), a.getLength()));
		}
		return result;
	}
	
	public static HashSet<Tag2> A2WToC2W(HashSet<Annotation2> anns) {
		HashSet<Tag2> tags = new HashSet<Tag2>();
		for (Annotation2 a : anns)
			tags.add(new Tag2(a.getConcept()));
		return tags;
	}
	
	public static List<HashSet<Tag2>> A2WToC2WList(
			List<HashSet<Annotation2>> tagsList) {
		List<HashSet<Tag2>> anns = new Vector<HashSet<Tag2>>();
		for (HashSet<Annotation2> s : tagsList)
			anns.add(A2WToC2W(s));
		return anns;
	}
	
	
}
