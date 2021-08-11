package org.aksw.gerbil.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.aksw.gerbil.dataset.check.index.Indexer;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.impl.UriEncodingHandlingSameAsRetriever;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDFBase;
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

	private static final String OUTPUT_FOLDER = "indexes/dbpedia_check";

	private static final String DBPEDIA_DUMP = "http://downloads.dbpedia.org/current/core-i18n/en/";

	public static void main(String[] args) throws GerbilException, IOException {
		Indexer index = Indexer.create(OUTPUT_FOLDER);
		SimpleDateFormat format = new SimpleDateFormat();
		Date start = Calendar.getInstance().getTime();
		LOGGER.info("Start indexing at {}", format.format(start));
		indexStream(index, DBPEDIA_DUMP);
		index.close();
		Date end = Calendar.getInstance().getTime();
		LOGGER.info("Indexing finished at {}", format.format(end));
		LOGGER.info("Indexing took: " + DurationFormatUtils.formatDurationHMS(end.getTime() - start.getTime()));
	}

	private static void indexStream(Indexer index, String url) throws IOException {
		Set<String> downloads = InitialIndexTool.getDownloadsOfUrl(url, new String[] { ".ttl.bz2", ".ttl" });
		URIIndexerStream sink = new URIIndexerStream(index);
		for (String download : downloads) {
			URL streamUrl = new URL(download);
			if (streamUrl.toString().endsWith(".ttl.bz2")) {
				try (InputStream fi = streamUrl.openStream();
						InputStream bi = new BufferedInputStream(fi);
						InputStream bzip2is = new BZip2CompressorInputStream(bi)) {
					LOGGER.info("Searching in {} ...", download);
					RDFDataMgr.parse(sink, bzip2is, Lang.TURTLE);
					LOGGER.info("...finished");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(streamUrl.toString().endsWith(".ttl")) {
				try (InputStream fi = streamUrl.openStream();
						InputStream bi = new BufferedInputStream(fi)) {
					LOGGER.info("Searching in {} ...", download);
					RDFDataMgr.parse(sink, bi, Lang.TURTLE);
					LOGGER.info("...finished");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LOGGER.info("Indexed {} Uris", index.docs());
		}
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

class URIIndexerStream extends StreamRDFBase {

	UriEncodingHandlingSameAsRetriever retriever = new UriEncodingHandlingSameAsRetriever();

	Indexer indexer;

	public URIIndexerStream(Indexer indexer) {
		this.indexer = indexer;
	}

	String oldURI = null;

	@Override
	public void triple(Triple triple) {
		String newURI = triple.getSubject().getURI();
		if (!newURI.equals(oldURI)) {
			Set<String> uris = retriever.retrieveSameURIs(newURI);
			long seqNo;
			if (uris != null) {
				for (String u : uris) {
					seqNo = indexer.index(u);
					if(seqNo%10000000==0) {
						System.out.println("SeqNo "+seqNo);
					}
				}
			} else {
				seqNo = indexer.index(newURI);
				if(seqNo%10000000==0) {
					System.out.println("SeqNo "+seqNo);
				}
			}
			
		}
		oldURI=newURI;

	}

}