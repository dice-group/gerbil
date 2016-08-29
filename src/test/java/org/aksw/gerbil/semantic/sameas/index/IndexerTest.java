package org.aksw.gerbil.semantic.sameas.index;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.junit.Test;

public class IndexerTest {

	
	@Test
	public void test() throws GerbilException{
		//Test if indexing and searching works
		//1. make some same as retrievals
		//2. index them
		Indexer index = new Indexer("test");
		index.indexSameAs(getList("http://dbpedia.org"));
		index.indexSameAs(getList("http://wikipedia.org"));
		index.indexSameAs(getList("http://de.dbpedia.org"));
		//3. search for one that exists
		Searcher search = new Searcher("test");
		assertFalse(search.searchSameAs("http://wikipedia.org/a").isEmpty());
		assertFalse(search.searchSameAs("http://dbpedia.org/a").isEmpty());
		//4. search for one that dont exist		
		assertTrue(search.searchSameAs("http://wikipedia.org/ab").isEmpty());
	}
	
	public List<String> getList(String prefix){
		List<String> sameAs = new LinkedList<String>();
		sameAs.add(prefix+"/a");
		sameAs.add(prefix+"/b");
		sameAs.add(prefix+"/c");
		sameAs.add(prefix+"/d");
		return sameAs;
	}
	
}
