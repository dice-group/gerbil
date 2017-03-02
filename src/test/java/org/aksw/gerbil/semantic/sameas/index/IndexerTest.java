package org.aksw.gerbil.semantic.sameas.index;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.junit.Test;

public class IndexerTest {

	
	@Test
	public void testTerm() throws GerbilException, IOException{
		//Test if indexing and searching works
		//1. make some same as retrievals
		//2. index them
		File indexFolder = createTempDirectory();
		//Test if folder could be created
		assertTrue(indexFolder!=null);
		Indexer index = new Indexer(indexFolder.getAbsolutePath());
		index.index("http://dbpedia.org/resource/Scar", getList("http://dbpedia.org"));
		index.index("http://wikipedia.org/a", getList("http://wikipedia.org"));
		index.index("http://de.dbpedia.org/a", getList("http://de.dbpedia.org"));
		index.close();
		//3. search for one that exists
		Searcher search = new Searcher(indexFolder.getAbsolutePath());
		assertFalse(search.search("http://wikipedia.org/a").isEmpty());
		assertTrue(search.search("http://wikipedia.org/d").isEmpty());
		assertFalse(search.search("http://dbpedia.org/resource/Scar").isEmpty());
		//4. search for one that dont exist		
		assertTrue(search.search("http://wikipedia.org/ab").isEmpty());
		search.close();
	}

	
	public List<String> getList(String prefix){
		List<String> sameAs = new LinkedList<String>();
		sameAs.add(prefix+"/a");
		sameAs.add(prefix+"/b");
		sameAs.add(prefix+"/c");
		sameAs.add(prefix+"/d");
		return sameAs;
	}

    public static File createTempDirectory()
            throws IOException {
        File temp = File.createTempFile("temp_index", Long.toString(System.nanoTime()));
        if (temp.exists()) {
        	if(!(temp.delete())){
        		return null;
        	}
        }
        if (!(temp.mkdir())) {
            return null;
        }
        return temp;
}
	
}
