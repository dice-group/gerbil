package org.aksw.gerbil.semantic.sameas.index;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Collection;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.index.LuceneConstants.IndexingStrategy;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.index.Term;
//import org.apache.lucene.queryParser.ParseException;
//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

public class Searcher extends LuceneConstants {

	IndexSearcher indexSearcher;
//	QueryParser queryParser;
	Query query;

	public Searcher(String indexDirectoryPath, IndexingStrategy strategy) throws GerbilException {

		try {
			Directory indexDirectory = MMapDirectory.open(new File(
					indexDirectoryPath).toPath());
			indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
		} catch (IOException e) {
			throw new GerbilException("Could not initialize Searcher", ErrorTypes.UNEXPECTED_EXCEPTION);
		}
//		queryParser = new QueryParser(Version.LUCENE_CURRENT, CONTENTS, new StandardAnalyzer(
//				Version.LUCENE_CURRENT));
//		queryParser.setAllowLeadingWildcard(true);
		this.strategy=strategy;
	}

	public TopDocs searchTops(String searchQuery) throws IOException {
//		switch(strategy){
//		case WildCard: 
//			return searchWildcards(searchQuery);
//		case TermQuery:
			return searchTerm(searchQuery);
//		default:
//			return searchWildcards(searchQuery);
//		}
	}
	
//	private TopDocs searchWildcards(String searchQuery) throws ParseException, IOException{
//		query = queryParser.parse(escapeQuery(searchQuery));
//		return indexSearcher.search(query, MAX_SEARCH);
//	}
	
	private TopDocs searchTerm(String searchQuery) throws IOException{
		TermQuery query = new TermQuery(new Term(CONTENTS, searchQuery));
		return indexSearcher.search(query, MAX_SEARCH);
	}

	private String escapeQuery(String query){
		//TODO all escape chars 
		query = query.replace("\\", "\\\\").replace(":", "\\:")
				.replace("+", "\\+").replace("-","\\-").replace("~", "\\~");
		return query;
	}
	
	public Document getDocument(ScoreDoc scoreDoc)
			throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

//	public void close() throws IOException {
//		indexSearcher.close();
//	}

	public Collection<String> search(String uri) throws GerbilException{
//		switch(strategy){
//		case WildCard:
//			return searchSameAsWildcard(uri);
//		case TermQuery:
			return searchSameAsTerm(uri);
//		default:
//			return searchSameAsWildcard(uri);
//		}
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
	
//	public Collection<String> searchSameAsWildcard(String uri) throws GerbilException{
//		TopDocs docs;
//		try {
//			docs = searchTops("*"+uri+"*");
//		} catch (IOException | ParseException e1) {
//			throw new GerbilException("Could not parse index files", ErrorTypes.UNEXPECTED_EXCEPTION);
//		}
//		Collection<String> uris = new HashSet<String>();
//		for (ScoreDoc scoreDoc : docs.scoreDocs) {
//			Document doc;
//			try {
//				doc = getDocument(scoreDoc);
//			} catch (IOException e) {
//				throw new GerbilException("Could not load Hits", ErrorTypes.UNEXPECTED_EXCEPTION);
//			}
//			String content = doc.get(CONTENTS);
//			for (String uriStr : content.split(" "))
//				uris.add(uriStr);
//		}
//		return uris;
//	}

}
