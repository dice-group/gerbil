package org.aksw.gerbil.semantic.sameas.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer extends LuceneConstants {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HTTPBasedSameAsRetriever.class);

	private IndexWriter writer;

	public Indexer(String path, IndexingStrategy strategy)
			throws GerbilException {
		try {
			Directory dir = FSDirectory.open(new File(path));
			writer = new IndexWriter(dir, new StandardAnalyzer(
					Version.LUCENE_CURRENT), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (IOException e) {
			LOGGER.error("Error occured during accesing file " + path, e);
			throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		this.strategy = strategy;
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}

	public void index(Collection<String> uris) {
		switch (strategy) {
		case WildCard:
			indexSameAs(uris);
			break;
		case TermQuery:
			indexSameAs2(uris);
			break;
		}
	}

	public void indexSameAs(Collection<String> uris) {
		String entity = listToStr(uris);
		Document doc = convertString("\"" + entity + "\"");
		try {
			writer.addDocument(doc);
			writer.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String listToStr(Collection<String> uris) {
		String entity = "";
		for (String uri : uris) {
			entity += uri + " ";
		}
		return entity;
	}

	public void indexSameAs2(Collection<String> uris) {
		Map<String, String> map = new HashMap<String, String>();
		for (String uri : uris) {
			List<String> uris_copy = new ArrayList<String>(uris);
			uris_copy.remove(uri);
			map.put(uri, listToStr(uris_copy));
			uris_copy.clear();
		}
		for (Document doc : convertMap(map)) {
			try {
				writer.addDocument(doc);
				writer.commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private List<Document> convertMap(Map<String, String> map) {
		List<Document> documents = new ArrayList<Document>();
		for (String key : map.keySet()) {
			Document document = new Document();
			Field contentField = new Field(CONTENTS, key, Field.Store.YES,
					Field.Index.NOT_ANALYZED);
			Field sameAsField = new Field(SAMEAS, map.get(key),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			document.add(contentField);
			document.add(sameAsField);
			documents.add(document);
		}
		return documents;
	}

	private Document convertString(String str) {
		Document document = new Document();
		Field contentField = new Field(CONTENTS, str, Field.Store.YES,
				Field.Index.NOT_ANALYZED);
		document.add(contentField);

		return document;
	}

}
