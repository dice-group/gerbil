package org.aksw.gerbil.dataset.converter.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.dataset.converter.AbstractLiteral2Resource;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexBasedLiteral2Resource extends AbstractLiteral2Resource {

	private static final String CONTENTS = "content";
	private static final int MAX_SEARCH = 100;
	private static final String RESOURCE = "res";
	private IndexSearcher indexSearcher;
	private Directory indexDirectory;
	private IndexReader indexReader;

	public IndexBasedLiteral2Resource(String folder, String domain)
			throws GerbilException {
		try {
			indexDirectory = FSDirectory.open(new File(folder).toPath());
			indexReader = DirectoryReader.open(indexDirectory);
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			throw new GerbilException("Could not initialize Searcher",
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
	}

	@Override
	public Set<String> getResourcesForLiteral(String literal, String qLang) {
		TopDocs docs;
		Set<String> uris = new HashSet<String>();
		try {
			// TODO @qLang
			docs = searchTops(literal);
		} catch (IOException e1) {
			// TODO Logger
			// throw new GerbilException("Could not parse index files",
			// ErrorTypes.UNEXPECTED_EXCEPTION);
			uris.add(literal);
			return uris;
		}
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document doc;
			try {
				doc = getDocument(scoreDoc);
			} catch (IOException e) {
				// TODO Logger
				// throw new GerbilException("Could not load Hits",
				// ErrorTypes.UNEXPECTED_EXCEPTION);
				uris.add(literal);
				return uris;
			}
			String content = doc.get(CONTENTS);
			String res = doc.get(RESOURCE);
			for (String uriStr : res.split(" "))
				uris.add(uriStr);
		}

		return uris;
	}

	public TopDocs searchTops(String searchQuery) throws IOException {
		return searchTerm(searchQuery);
	}

	private TopDocs searchTerm(String searchQuery) throws IOException {
		TermQuery query = new TermQuery(new Term(CONTENTS, searchQuery));
		return indexSearcher.search(query, MAX_SEARCH);
	}

	public Document getDocument(ScoreDoc scoreDoc)
			throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(indexReader);
		IOUtils.closeQuietly(indexDirectory);
	}

}
