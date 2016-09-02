package org.aksw.gerbil.semantic.sameas.index;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.junit.Test;

public class IndexerTest {

	
//	@Test
	public void testTerm() throws GerbilException, IOException{
		//Test if indexing and searching works
		//1. make some same as retrievals
		//2. index them
		Indexer index = new Indexer("test");
		index.index("http://dbpedia.org/a", getList("http://dbpedia.org"));
		index.index("http://wikipedia.org/a", getList("http://wikipedia.org"));
		index.index("http://de.dbpedia.org/a", getList("http://de.dbpedia.org"));
		//3. search for one that exists
		Searcher search = new Searcher("test");
		assertFalse(search.search("http://wikipedia.org/a").isEmpty());
		assertFalse(search.search("http://wikipedia.org/d").isEmpty());
		assertFalse(search.search("http://dbpedia.org/a").isEmpty());
		//4. search for one that dont exist		
		assertTrue(search.search("http://wikipedia.org/ab").isEmpty());
		index.close();
	}
	
	@Test
	public void testWildcard() throws GerbilException, IOException{
		//Test if indexing and searching works
		//1. make some same as retrievals
		//2. index them
		Indexer index = new Indexer("test");
		index.indexSameAs("http://dbpedia.org/a", getList("http://dbpedia.org"));
		index.indexSameAs("http://wikipedia.org/a", getList("http://wikipedia.org"));
		index.indexSameAs("http://de.dbpedia.org/a", getList("http://de.dbpedia.org"));
		//3. search for one that exists
		Searcher search = new Searcher("test");
		assertFalse(search.search("http://wikipedia.org/a").isEmpty());
		assertFalse(search.search("http://dbpedia.org/a").isEmpty());
		//4. search for one that dont exist		
		assertTrue(search.search("http://wikipedia.org/ab").isEmpty());
		index.close();
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
