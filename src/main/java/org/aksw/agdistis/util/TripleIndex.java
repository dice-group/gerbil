package org.aksw.agdistis.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TripleIndex implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripleIndex.class);

    private static final Version LUCENE44 = Version.LUCENE_44;
    public static final String FIELD_NAME_SUBJECT = "subject";
    public static final String FIELD_NAME_PREDICATE = "predicate";
    public static final String FIELD_NAME_OBJECT_URI = "object_uri";
    public static final String FIELD_NAME_OBJECT_LITERAL = "object_literal";

    public static TripleIndex createIndex(String indexDirectory) {
        Directory directory = null;
        DirectoryReader reader = null;
        try {
            directory = new MMapDirectory(new File(indexDirectory));
            reader = DirectoryReader.open(directory);
            return new TripleIndex(directory, reader);
        } catch (Exception e) {
            LOGGER.error("Couldn't open index. Returning null.", e);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(directory);
            return null;
        }
    }

    private int defaultMaxNumberOfDocsRetrievedFromIndex = 1000;

    private Directory directory;
    private IndexSearcher isearcher;
    private DirectoryReader ireader;
    private UrlValidator urlValidator;
    private Cache<BooleanQuery, List<Triple>> cache;

    protected TripleIndex(Directory directory, DirectoryReader reader) {
        this.directory = directory;
        this.ireader = reader;
        isearcher = new IndexSearcher(ireader);
        this.urlValidator = new UrlValidator();

        cache = CacheBuilder.newBuilder().maximumSize(50000).build();
    }

    public List<Triple> search(String subject, String predicate, String object) {
        return search(subject, predicate, object, defaultMaxNumberOfDocsRetrievedFromIndex);
    }

    public List<Triple> search(String subject, String predicate, String object, int maxNumberOfResults) {
        BooleanQuery bq = new BooleanQuery();
        List<Triple> triples = new ArrayList<Triple>();
        try {
            if (subject != null && subject.equals("http://aksw.org/notInWiki")) {
                LOGGER.error("A subject 'http://aksw.org/notInWiki' is searched in the index. That is strange and should not happen");
            }
            if (subject != null) {
                TermQuery tq = new TermQuery(new Term(FIELD_NAME_SUBJECT, subject));
                bq.add(tq, BooleanClause.Occur.MUST);
            }
            if (predicate != null) {
                TermQuery tq = new TermQuery(new Term(FIELD_NAME_PREDICATE, predicate));
                bq.add(tq, BooleanClause.Occur.MUST);
            }
            if (object != null) {
                Query q = null;
                if (urlValidator.isValid(object)) {
                    q = new TermQuery(new Term(FIELD_NAME_OBJECT_URI, object));
                } else {
                    Analyzer analyzer = new LiteralAnalyzer(LUCENE44);
                    QueryParser parser = new QueryParser(LUCENE44, FIELD_NAME_OBJECT_LITERAL, analyzer);
                    parser.setDefaultOperator(QueryParser.Operator.OR);
                    q = parser.parse(QueryParserBase.escape(object));
                }
                bq.add(q, BooleanClause.Occur.MUST);
            }
            // use the cache
            if (null == (triples = cache.getIfPresent(bq))) {
                triples = getFromIndex(maxNumberOfResults, bq);
                cache.put(bq, triples);
            }

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage() + " -> " + subject);
        }
        return triples;
    }

    private List<Triple> getFromIndex(int maxNumberOfResults, BooleanQuery bq) throws IOException {
        LOGGER.debug("\t start asking index...");
        TopScoreDocCollector collector = TopScoreDocCollector.create(maxNumberOfResults, true);
        isearcher.search(bq, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        List<Triple> triples = new ArrayList<Triple>();
        String s, p, o;
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            s = hitDoc.get(FIELD_NAME_SUBJECT);
            p = hitDoc.get(FIELD_NAME_PREDICATE);
            o = hitDoc.get(FIELD_NAME_OBJECT_URI);
            if (o == null) {
                o = hitDoc.get(FIELD_NAME_OBJECT_LITERAL);
            }
            Triple triple = new Triple(s, p, o);
            triples.add(triple);
        }
        LOGGER.debug("\t finished asking index...");
        return triples;
    }

    public void close() throws IOException {
        ireader.close();
        directory.close();
    }

    public DirectoryReader getIreader() {
        return ireader;
    }

}
