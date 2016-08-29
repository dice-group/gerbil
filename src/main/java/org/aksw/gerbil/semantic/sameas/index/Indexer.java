package org.aksw.gerbil.semantic.sameas.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class Indexer extends LuceneConstants{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HTTPBasedSameAsRetriever.class);

	

	private IndexWriter writer;

	public Indexer(String path) throws GerbilException {
		try {
			Directory dir = FSDirectory.open(new File(path));
			writer = new IndexWriter(dir, new StandardAnalyzer(
					Version.LUCENE_CURRENT), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (IOException e) {
			LOGGER.error("Error occured during accesing file " + path, e);
			throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.error("Error occured during closing Index Writer", e);
		}
	}


	public void indexSameAs(List<String> uris) {
		String entity="";
		for(String uri : uris){
			entity+=uri+" ";
		}
		Document doc = convertString("\""+entity+"\"");
		try {
			writer.addDocument(doc);
			writer.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	
	private Document convertString(String str){
		Document document = new Document();
		Field contentField = new Field(CONTENTS, str, Field.Store.YES, Field.Index.NOT_ANALYZED);
		document.add(contentField);

		return document;
		
	}

}
