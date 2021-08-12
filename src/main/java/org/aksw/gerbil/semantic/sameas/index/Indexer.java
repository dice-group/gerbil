package org.aksw.gerbil.semantic.sameas.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer extends LuceneConstants {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Indexer.class);

	public IndexWriter writer;
	private Directory dir;
	

	public Indexer(String path)
			throws GerbilException {
		try {
			dir = FSDirectory.open(new File(path).toPath());
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(dir, config);
		} catch (IOException e) {
			LOGGER.error("Error occured during accesing file " + path, e);
			throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
		}
	}

	public void close() {
		try {
			writer.commit();
			writer.close();
			dir.close();
		} catch (IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}

	public void index(String uri, Collection<String> uris) {
			indexSameAs(uri, uris);
	}

	private String listToStr(Collection<String> uris) {
		String entity = "";
		for (String uri : uris) {
			entity += uri + " ";
		}
		return entity;
	}

	public void indexSameAs(String uri, Collection<String> uris) {
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
		Field contentField = new StringField(CONTENTS, uri, Field.Store.YES);
		Field sameAsField = new StringField(SAMEAS, sameAs, Field.Store.YES);
		document.add(contentField);
		document.add(sameAsField);

		return document;
	}

}
