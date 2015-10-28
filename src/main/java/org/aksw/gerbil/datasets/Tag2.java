package org.aksw.gerbil.datasets;

/**
 * (C) Copyright 2012-2013 A-cube lab - Università di Pisa - Dipartimento di Informatica. 
 * BAT-Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * BAT-Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with BAT-Framework.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.Serializable;

/**
 * An annotation is a concept associated to a text.
 *
 */
public class Tag2 implements Comparable<Tag2>, Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	private String concept; //the wikipedia article to which the annotation is bound

	public String getConcept(){
		return this.concept;
	}

	public void setWikipediaArticle(String wid){
		this.concept = wid;
	}

	public Tag2(String wikipediaArticle){
		this.concept = wikipediaArticle;
	}


	@Override public boolean equals(Object t){
		Tag2 tag = (Tag2) t;
		return this.concept == tag.concept;
	}

	@Override public int hashCode() {
		return this.concept.hashCode();
	}

	@Override public Object clone(){
		Tag2 cloned = new Tag2(this.concept);
		return cloned;
	}

	@Override
	public int compareTo(Tag2 arg0) {
		return this.concept.compareTo(arg0.getConcept());
	}
}
