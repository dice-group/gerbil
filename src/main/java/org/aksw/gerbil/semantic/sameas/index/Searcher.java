package org.aksw.gerbil.semantic.sameas.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
//import org.apache.lucene.queryParser.ParseException;
//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher extends LuceneConstants {

    private IndexSearcher indexSearcher;
	private Directory indexDirectory;
	private IndexReader indexReader;

	public Searcher(String indexDirectoryPath) throws GerbilException {

		try {
			indexDirectory = FSDirectory.open(new File(
					indexDirectoryPath).toPath());
			indexReader = DirectoryReader.open(indexDirectory);
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			throw new GerbilException("Could not initialize Searcher", ErrorTypes.UNEXPECTED_EXCEPTION);
		}
	}

	public TopDocs searchTops(String searchQuery) throws IOException {
			return searchTerm(searchQuery);
	}
		
	private TopDocs searchTerm(String searchQuery) throws IOException{
		TermQuery query = new TermQuery(new Term(CONTENTS, searchQuery));
		return indexSearcher.search(query, MAX_SEARCH);
	}

	
	public Document getDocument(ScoreDoc scoreDoc)
			throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

    public void close() throws IOException {
        IOUtils.closeQuietly(indexReader);
        IOUtils.closeQuietly(indexDirectory);
    }

	public Collection<String> search(String uri) throws GerbilException{

			return searchSameAsTerm(uri);
	}
	
	public Collection<String> searchSameAsTerm(String uri) throws GerbilException{
		TopDocs docs;
		try {
			docs = searchTops(uri);
		} catch (IOException e1) {
			throw new GerbilException("Could not parse index files", ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		Collection<String> uris = new HashSet<String>();
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document doc;
			try {
				doc = getDocument(scoreDoc);
			} catch (IOException e) {
				throw new GerbilException("Could not load Hits", ErrorTypes.UNEXPECTED_EXCEPTION);
			}
			String content = doc.get(CONTENTS);
			uris.add(content);
			String sameAs  = doc.get(SAMEAS);
			for (String uriStr : sameAs.split(" "))
				uris.add(uriStr);
		}
		return uris;
	}

}
