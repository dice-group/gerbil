package org.aksw.gerbil.dataset.check.index;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
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

public class Indexer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Indexer.class);

    public static Indexer create(String indexDirPath) {
        Directory indexDirectory = null;
        try {
            indexDirectory = FSDirectory.open(new File(indexDirPath).toPath());
            IndexWriterConfig config = new IndexWriterConfig();
            config.setOpenMode(OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, config);
            return new Indexer(indexDirectory, indexWriter);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to create index writer for entity checking. Returning null.", e);
            IOUtils.closeQuietly(indexDirectory);
            return null;
        }
    }

    private IndexWriter indexWriter;
    private Directory indexDirectory;

    protected Indexer(Directory dir, IndexWriter writer) {
        this.indexWriter = writer;
        this.indexDirectory = dir;
    }

    public void close() {
        try {
            indexWriter.commit();
        } catch (IOException e) {
            LOGGER.error("Error occured during final commit of Index Writer.", e);
        }
        IOUtils.closeQuietly(indexWriter);
        IOUtils.closeQuietly(indexDirectory);
    }

    public void index(String uri) {
        Document document = new Document();
        document.add(new StringField(IndexBasedEntityChecker.URI_FIELD_NAME, uri, Field.Store.NO));
        try {
            indexWriter.addDocument(document);
        } catch (IOException e) {
            LOGGER.error("Couldn't write uri to index.", e);
            e.printStackTrace();
        }
    }
}
