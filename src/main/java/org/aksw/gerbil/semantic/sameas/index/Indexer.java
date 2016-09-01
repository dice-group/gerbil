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
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer extends LuceneConstants {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HTTPBasedSameAsRetriever.class);

	private IndexWriter writer;

	private String uri;

	public Indexer(String path, IndexingStrategy strategy)
			throws GerbilException {
		try {
			Directory dir = FSDirectory.open(new File(path).toPath());
			IndexWriterConfig config = new IndexWriterConfig();
			config.setRAMBufferSizeMB(2048);
			writer = new IndexWriter(dir, config);
		} catch (IOException e) {
			LOGGER.error("Error occured during accesing file " + path, e);
			throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		this.strategy = strategy;
	}

	public void close() {
		try {
			writer.commit();
			writer.close();
		} catch (IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}

	public void index(String uri, Collection<String> uris) {
		switch (strategy) {
		case WildCard:
			indexSameAs(uris);
			break;
		case TermQuery:
			indexSameAs2(uri, uris);
			break;
		}
	}

	public void indexSameAs(Collection<String> uris) {
		String entity = listToStr(uris);
		Document doc = convertString("\"" + entity + "\"");
		try {
			writer.addDocument(doc);
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

	public void indexSameAs2(String uri, Collection<String> uris) {
//		Map<String, String> map = new HashMap<String, String>();
//		for (String uri : uris) {
//			List<String> uris_copy = new ArrayList<String>(uris);
//			uris_copy.remove(uri);
//			map.put(uri, listToStr(uris_copy));
//			uris_copy.clear();
//		}
//		for (Document doc : convertMap(map)) {
		Document doc = convertTerm(uri, listToStr(uris));
			try {
				writer.addDocument(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private Document convertTerm(String uri, String sameAs) {
		Document document = new Document();
		FieldType type = new FieldType();
		type.setStored(true);
		FieldType type2 = new FieldType();
		type2.setStored(true);
		type2.setIndexOptions(IndexOptions.NONE);
		Field contentField = new Field(CONTENTS, uri, type);
		Field sameAsField = new Field(SAMEAS, sameAs, type2);
		document.add(contentField);
		document.add(sameAsField);

		return document;
	}

	private List<Document> convertMap(Map<String, String> map) {
		List<Document> documents = new ArrayList<Document>();
		FieldType type = new FieldType();
		type.setStored(true);
		for (String key : map.keySet()) {
			Document document = new Document();
			Field contentField = new Field(CONTENTS, key, type);
			Field sameAsField = new Field(SAMEAS, map.get(key),type);
			document.add(contentField);
			document.add(sameAsField);
			documents.add(document);
		}
		return documents;
	}

	private Document convertString(String str) {
		Document document = new Document();
		FieldType type = new FieldType();
		type.setStored(true);
		Field contentField = new Field(CONTENTS, str,type);
		document.add(contentField);

		return document;
	}

}
