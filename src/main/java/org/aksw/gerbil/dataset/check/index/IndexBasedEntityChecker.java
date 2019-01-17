package org.aksw.gerbil.dataset.check.index;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.aksw.gerbil.dataset.check.EntityChecker;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexBasedEntityChecker implements EntityChecker, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexBasedEntityChecker.class);

    public static final String URI_FIELD_NAME = "URI";

    public static IndexBasedEntityChecker create(String indexDirPath) {
        Directory indexDirectory = null;
        File directoryPath = new File(indexDirPath);
        if (directoryPath.exists() && directoryPath.isDirectory() && (directoryPath.list().length > 0)) {
            try {
                indexDirectory = FSDirectory.open(directoryPath.toPath());
                IndexReader indexReader = DirectoryReader.open(indexDirectory);
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                return new IndexBasedEntityChecker(indexSearcher, indexDirectory, indexReader);
            } catch (IOException e) {
                LOGGER.error("Exception while trying to open index for entity checking. Returning null.", e);
                IOUtils.closeQuietly(indexDirectory);
                return null;
            }
        } else {
            LOGGER.warn(
                    "The configured path to the entity checking index (\"{}\") does not exist,  is not a directory or is an empty directory. Returning null.",
                    directoryPath.toString());
            return null;
        }
    }

    private IndexSearcher indexSearcher;
    private Directory indexDirectory;
    private IndexReader indexReader;

    protected IndexBasedEntityChecker(IndexSearcher indexSearcher, Directory indexDirectory, IndexReader indexReader) {
        this.indexSearcher = indexSearcher;
        this.indexDirectory = indexDirectory;
        this.indexReader = indexReader;
    }

    @Override
    public boolean entityExists(String uri) {
        TopDocs docs = null;
        try {
            TermQuery query = new TermQuery(new Term(URI_FIELD_NAME, uri));
            docs = indexSearcher.search(query, 1);
        } catch (IOException e) {
            LOGGER.error("Got an exception while searching for \"" + uri + "\" in the index. Returning false.", e);
        }
        return (docs != null) && (docs.totalHits > 0);
    }

    public void close() throws IOException {
        IOUtils.closeQuietly(indexReader);
        IOUtils.closeQuietly(indexDirectory);
    }

}
