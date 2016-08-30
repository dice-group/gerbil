package org.aksw.gerbil.semantic.sameas.index;

public abstract class LuceneConstants {

	protected static final String CONTENTS = "contents";

	protected static final String SAMEAS = "sameAs";

	protected static final int MAX_SEARCH = 10;
	
	protected IndexingStrategy strategy;
	
	public enum IndexingStrategy {
		TermQuery, WildCard
	}
}
