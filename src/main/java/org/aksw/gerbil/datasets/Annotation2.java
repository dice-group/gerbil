package org.aksw.gerbil.datasets;

/**
 * (C) Copyright 2012-2013 A-cube lab - Università di Pisa - Dipartimento di Informatica. 
 * BAT-Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * BAT-Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with BAT-Framework.  If not, see <http://www.gnu.org/licenses/>.
 */


import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.utils.AnnotationException;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * An annotation is an association between a mention in a text and a concept.
 *
 */
public class Annotation2 extends Tag2 implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private Mention m;
	
	public Annotation2(int position, int length, String wikipediaArticle) throws AnnotationException{
		super(wikipediaArticle);
		this.m = new Mention(position, length);
		if (position < 0) throw new AnnotationException("Annotation with negative position.");
	}

	public int getPosition(){
		return m.getPosition();
	}

	public int getLength() {
		return m.getLength();
	}

	@Override public boolean equals(Object a){
		Annotation2 ann = (Annotation2) a;
		return (m.equals(ann.m) && this.getConcept() == ann.getConcept());
	}

	@Override public int hashCode() {
		return (this.getConcept().hashCode()^m.hashCode());
	}
	
	public static <E extends Tag> void prefetchRedirectList(List<HashSet<E>> annotations, WikipediaApiInterface api) throws IOException{
		/** Prefetch redirect values */
		List<Integer> widsToCheck = new Vector<Integer>();
		for (HashSet<E> s : annotations)
			for (E a : s)
				widsToCheck.add(a.getConcept());
		
		try {
			api.prefetchWids(widsToCheck);
		} catch (Exception e){
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public int compareTo(Tag2 o) {
		if (o instanceof Annotation2)
			return this.getPosition() - ((Annotation2)o).getPosition();
		return super.compareTo(o);
	}

	@Override public Object clone(){
		return new Annotation2(this.getPosition(), this.getLength(), this.getConcept());
	}
	
	public boolean overlaps(Annotation2 t) {
		return m.overlaps(t.m);
	}
	

	public boolean overlaps(Mention men) {
		return m.overlaps(men);
	}
	
	public static <T extends Annotation2> HashSet<T> deleteOverlappingAnnotations(HashSet<T> anns) {
		Vector<T> annsList = new Vector<T>(anns);
		HashSet<T> res = new HashSet<T>();
		Collections.sort(annsList);
		for (int i=0; i<annsList.size(); i++){
			T bestCandidate = annsList.get(i);
			/* find conflicting annotations*/
			int j=i+1;
			while (j<annsList.size() && bestCandidate.overlaps(annsList.get(j))){
				//System.out.printf("Dataset is malformed: tag with position,length,wid [ %d, %d, %d] overlaps with tag [ %d, %d, %d]. Discarding tag with smallest length.%n", bestCandidate.position, bestCandidate.length, bestCandidate.getWikipediaArticle(), annsList.get(j).position, annsList.get(j).length, annsList.get(j).getWikipediaArticle());
				if (bestCandidate.getLength() < annsList.get(j).getLength())
					bestCandidate = annsList.get(j);
				j++;
			}
			i=j-1;
			res.add(bestCandidate);
		}
		return res;
	}
}

