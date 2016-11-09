package org.aksw.gerbil.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.aksw.gerbil.dataset.check.index.Indexer;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.impl.UriEncodingHandlingSameAsRetriever;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tool can be used to create the Lucene index that can be used for entity
 * checking. A file can be used as source for the data, e.g., the mapping from
 * DBpedia resource to Wikipedia ID.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DBpediaEntityCheckIndexTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBpediaEntityCheckIndexTool.class);

    private static final String INPUT_FOLDER = "C:/Daten/DBpedia";
    private static final String OUTPUT_FOLDER = "indexes/dbpedia_check";

    public static void main(String[] args) throws GerbilException, IOException {
        Indexer index = Indexer.create(OUTPUT_FOLDER);
        SimpleDateFormat format = new SimpleDateFormat();
        Date start = Calendar.getInstance().getTime();
        LOGGER.info("Start indexing at {}", format.format(start));
        indexFolder(index, INPUT_FOLDER);
        index.close();
        Date end = Calendar.getInstance().getTime();
        LOGGER.info("Indexing finished at {}", format.format(end));
        LOGGER.info("Indexing took: " + DurationFormatUtils.formatDurationHMS(end.getTime() - start.getTime()));
    }

    public static void indexFolder(Indexer index, String folder) {
        File dir = new File(folder);
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".ttl")) {
                index(index, f.getAbsolutePath());
            }
        }
    }

    public static void index(Indexer indexer, String file) {
        UriEncodingHandlingSameAsRetriever retriever = new UriEncodingHandlingSameAsRetriever();
        LineIterator iterator = null;
        long size = 0, rounds = 0;
        try {
            iterator = FileUtils.lineIterator(new File(file), "UTF-8");
            String uri = null;
            Set<String> uris;
            String old = null;
            Date start = Calendar.getInstance().getTime();
            // iterate over the lines
            while (iterator.hasNext()) {
                String[] split = iterator.next().split("\\s+");
                if (split.length > 2) {
                    // get the subject of the triple
                    uri = split[0];
                    if (uri.startsWith("<")) {
                        uri = uri.substring(1);
                    }
                    if (uri.endsWith(">")) {
                        uri = uri.substring(0, uri.length() - 1);
                    }

                    // if this subject is new
                    if (!uri.equals(old)) {
                        // retrieve other writings of this URI
                        uris = retriever.retrieveSameURIs(uri);
                        if (uris != null) {
                            for (String u : uris) {
                                indexer.index(u);
                            }
                        } else {
                            indexer.index(uri);
                        }
                    }
                    size++;
                    if (size % 100000 == 0) {
                        Date end = Calendar.getInstance().getTime();
                        rounds++;
                        String avgTime = DurationFormatUtils
                                .formatDurationHMS((end.getTime() - start.getTime()) / rounds);
                        LOGGER.info("Got 100000 entities...(Sum: {}, AvgTime: {})", size, avgTime);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception while reading file. It will be ignored.", e);
        } finally {
            LineIterator.closeQuietly(iterator);
        }
        LOGGER.info("Successfully indexed {} triples", size);
    }

}
