package org.aksw.gerbil.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.index.Indexer;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.externalsorting.ExternalSort;
import com.google.common.collect.Lists;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.CollectorStreamRDF;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.util.FileUtils;

public class InitialIndexTool {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitialIndexTool.class);

	private static final String OUTPUT_FOLDER = "lucene_index";
	private static final String SPARQL_GET = "select distinct ?s ?o where {?s <http://www.w3.org/2002/07/owl#sameAs> ?o}";

	private static final String[] DOWNLOAD_SUFFIX = { "ttl.bz2", ".nt", ".ttl" };

	private static String service = "http://de.dbpedia.org/sparql";

	private static String owlSameAs = "<http://www.w3.org/2002/07/owl#sameAs>";

	public static void main(String[] args) throws GerbilException, IOException {
		Indexer index = new Indexer(OUTPUT_FOLDER);
		SimpleDateFormat format = new SimpleDateFormat();
		Date start = Calendar.getInstance().getTime();
		LOGGER.info("Start indexing at {}", format.format(start));
		if (args[0].equals("-ram")) {
			indexStreamMem(index, args[1]);
		} else if (args[0].equals("-sf")) {
			indexSortedFile(index, args[1]);
		} else {
			indexStream(index, args[0]);
		}

		index.close();
		Date end = Calendar.getInstance().getTime();
		LOGGER.info("Indexing finished at {}", format.format(end));
		LOGGER.info("Indexing took: " + DurationFormatUtils.formatDurationHMS(end.getTime() - start.getTime()));
	}

	private static void indexSortedFile(Indexer index, String file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			String old = null;
			long total = 0, size = 0, rounds = 0;
			Date start = Calendar.getInstance().getTime();
			Set<String> sameAsBlock = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				String[] split = line.split("\\s+");
				String node1 = split[0];
				String node2 = split[2];

				if (node1.equals(old)) {
					sameAsBlock.add(node2.toString());
				} else if (old != null) {
					// Enitity is finished
					index.index(old.toString(), sameAsBlock);
					total += sameAsBlock.size();

					sameAsBlock.clear();
					// Add Uri
					sameAsBlock.add(node2.toString());
					old = node1;
				} else {
					// First run
					sameAsBlock.add(node2.toString());
					old = node1;
				}
				size++;
				if (size % 1000000 == 0) {
					Date end = Calendar.getInstance().getTime();
					rounds++;
					String avgTime = DurationFormatUtils.formatDurationHMS((end.getTime() - start.getTime()) / rounds);
					LOGGER.info("Got 1000000 triples...(Sum: {}, AvgTime: {})", size, avgTime);
				}
			}
		}
	}

	public static void index(Indexer index) throws GerbilException {
		int offset = 0, limit = 10000;
		boolean test = true;

		Query q = QueryFactory.create(SPARQL_GET);
		q.setLimit(limit);

		// Create here!
		Set<String> sameAsBlock = new HashSet<String>();
		RDFNode old = null;
		int rounds = 0, size = 0;
		long total = 0;
		Date start = Calendar.getInstance().getTime();
		do {
			q.setOffset(offset);
			Date startQ = Calendar.getInstance().getTime();
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service, q);
			ResultSet res = qexec.execSelect();
			Date endQ = Calendar.getInstance().getTime();
			// get results
			size = 0;
			long sumI = 0;
			rounds++;
			// Go through all elements
			while (res.hasNext()) {
				size++;
				QuerySolution solution = res.next();
				RDFNode node1 = solution.get("s");
				RDFNode node2 = solution.get("o");
				if (node1.equals(old)) {
					sameAsBlock.add(node2.toString());
				} else if (old != null) {
					// Enitity is finished
					Date startI = Calendar.getInstance().getTime();
					index.index(old.toString(), sameAsBlock);
					Date endI = Calendar.getInstance().getTime();
					sumI += endI.getTime() - startI.getTime();
					total += sameAsBlock.size();

					sameAsBlock.clear();
					// Add Uri
					sameAsBlock.add(node2.toString());
					old = node1;
				} else {
					// First run
					sameAsBlock.add(node2.toString());
					old = node1;
				}
			}
			if (size < limit) {
				// No more results
				test = false;
			}
			// Set offset so it starts immediately after last results
			offset += limit;

			Date end = Calendar.getInstance().getTime();
			String avg = DurationFormatUtils.formatDurationHMS((end.getTime() - start.getTime()) / rounds);
			String avgQ = DurationFormatUtils.formatDurationHMS((endQ.getTime() - startQ.getTime()));
			String avgI = DurationFormatUtils.formatDurationHMS(sumI);
			sumI = 0;
			LOGGER.info("Got {} triples...(Sum: {}, AvgTime: {}, QueryTime: {}, IndexTime: {})", size,
					limit * (rounds - 1) + size, avg, avgQ, avgI);
		} while (test);
		// done
		if (!sameAsBlock.isEmpty()) {
			index.index(old.toString(), sameAsBlock);
			sameAsBlock.clear();
		}
		LOGGER.info("Successfully indexed {} triples", total);
	}

	public static void indexStream(Indexer index, String url) throws IOException, GerbilException {
		Set<String> downloads = getDownloadsOfUrl(url, DOWNLOAD_SUFFIX);
		String fileName = UUID.randomUUID().toString();
		SameAsCollectorStreamFile sink = new SameAsCollectorStreamFile(fileName);
		for (String download : downloads) {
			File current = null;
			try {
				LOGGER.info("Searching in {} ...", download);
				current = downloadUrl(new URL(download));
				try (InputStream fi = Files.newInputStream(current.toPath());
						InputStream bi = new BufferedInputStream(fi);
						InputStream bzip2is = new BZip2CompressorInputStream(bi)) {
					indexStream(index, bzip2is, sink);
					LOGGER.info("...finished");

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (current != null)
					current.delete();
			}
		}
		sink.close();
		// sort that file
		File sorted = new File(sink.getFile().getName() + "_sorted");
		ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(sink.getFile()), sorted);
		// then index that file
		indexSortedFile(index, sorted.getAbsolutePath());
		sink.getFile().delete();
	}

	private static File downloadUrl(URL url) throws IOException {
		File file = new File(UUID.randomUUID().toString());
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
		return file;
	}

	public static void indexStreamMem(Indexer index, String url) throws IOException {
		Set<String> downloads = getDownloadsOfUrl(url, DOWNLOAD_SUFFIX);
		SameAsCollectorStreamMem sink = new SameAsCollectorStreamMem();
		for (String download : downloads) {
			URL streamUrl = new URL(download);

			try (InputStream fi = streamUrl.openStream();
					InputStream bi = new BufferedInputStream(fi);
					InputStream bzip2is = new BZip2CompressorInputStream(bi)) {
				LOGGER.info("Searching in {} ...", download);
				indexStreamMem(index, bzip2is, sink);
				LOGGER.info("...finished");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void indexStreamMem(Indexer index, InputStream in, SameAsCollectorStreamMem sink) {
		RDFDataMgr.parse(sink, in, Lang.TURTLE);
		LOGGER.info("Found {} instances of owl:sameAs", sink.getMapping().size());
		for (String key : sink.getMapping().keySet()) {
			index.indexSameAs(key, sink.getMapping().get(key));
		}
	}

	private static void indexStream(Indexer index, InputStream in, SameAsCollectorStreamFile sink)
			throws IOException, GerbilException {
		// write everyhting to tmp file usiong SameAsCollectorStream

		RDFDataMgr.parse(sink, in, Lang.TURTLE);

	}

	private static Set<String> getDownloadsOfUrl(String url, String[] downloadSuffix) throws IOException {
		URL site = new URL(url);
		Set<String> downloads = new HashSet<String>();
		StringBuilder doc = new StringBuilder();
		URLConnection conn = site.openConnection();
		try (BufferedReader bis = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
			String line = "";
			while ((line = bis.readLine()) != null) {
				doc.append(line).append("\n");
			}
		}
		String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(doc);
		while (m.find()) {
			String link = m.group();
			for (String filter : downloadSuffix) {
				if (link.endsWith(filter)) {
					downloads.add(link);
					break;
				}
			}
		}
		regex = "href=\"[^\"]+\"";

		p = Pattern.compile(regex);
		m = p.matcher(doc);
		while (m.find()) {
			String link = url + m.group().replace("href=\"", "").replace("\"", "");
			if (downloads.contains(link)) {
				continue;
			}
			for (String filter : downloadSuffix) {
				if (link.endsWith(filter)) {
					downloads.add(link);
					break;
				}
			}
		}
		return downloads;
	}

	public static void indexFolder(Indexer index, String folder) throws GerbilException, IOException {
		File dir = new File(folder);

		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".nt"))
				index(index, f.getAbsolutePath());
		}
	}

	public static void index(Indexer index, String file) throws GerbilException, IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

		// Create here!
		Set<String> sameAsBlock = new HashSet<String>();

		long total = 0, size = 0, rounds = 0;
		String line = "";
		String old = null;
		Date start = Calendar.getInstance().getTime();
		while ((line = reader.readLine()) != null) {
			String[] split = line.split("\\s+");
			if (!split[1].equals(owlSameAs)) {
				continue;
			}
			String node1 = split[0].replace("<", "").replace(">", "");
			String node2 = split[2];
			node2 = node2.substring(node2.indexOf("<") + 1, node2.lastIndexOf(">")).trim();

			if (node1.equals(old)) {
				sameAsBlock.add(node2.toString());
			} else if (old != null) {
				// Enitity is finished
				index.index(old.toString(), sameAsBlock);
				total += sameAsBlock.size();

				sameAsBlock.clear();
				// Add Uri
				sameAsBlock.add(node2.toString());
				old = node1;
			} else {
				// First run
				sameAsBlock.add(node2.toString());
				old = node1;
			}
			size++;
			if (size % 100000 == 0) {
				Date end = Calendar.getInstance().getTime();
				rounds++;
				String avgTime = DurationFormatUtils.formatDurationHMS((end.getTime() - start.getTime()) / rounds);
				LOGGER.info("Got 100000 triples...(Sum: {}, AvgTime: {})", size, avgTime);
			}
		}

		// done
		if (!sameAsBlock.isEmpty()) {
			index.index(old.toString(), sameAsBlock);
			sameAsBlock.clear();
		}
		reader.close();
		LOGGER.info("Successfully indexed {} triples", total);
	}

}

class SameAsCollectorStreamFile extends StreamRDFBase {

	private static String owlSameAs = "http://www.w3.org/2002/07/owl#sameAs";
	private int count = 0;
	private int sameAsCount = 0;

	private File file;
	private PrintWriter pw;

	public SameAsCollectorStreamFile(String fileName) throws IOException {
		this.file = new File(fileName);
		this.file.createNewFile();
		this.pw = new PrintWriter(new FileOutputStream(this.file, true));
	}

	public File getFile() {
		return this.file;
	}

	@Override
	public void triple(Triple triple) {
		count++;
		if (triple.getPredicate().getURI().equals(owlSameAs)) {
			pw.println(triple);
			sameAsCount++;
		}
		if (count % 1000000 == 0) {
			System.out.println("Searched through " + count + " triples and found " + sameAsCount + " sameAs instances");
			System.out
					.println("RAM usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		}

	}

	public void close() {
		this.pw.flush();
		this.pw.close();
	}
}

class SameAsCollectorStreamMem extends StreamRDFBase {

	private static String owlSameAs = "http://www.w3.org/2002/07/owl#sameAs";
	private Map<String, Collection<String>> sameAsMapping = new HashMap<String, Collection<String>>();

	private int count = 0;

	@Override
	public void triple(Triple triple) {
		count++;
		if (triple.getPredicate().getURI().equals(owlSameAs)) {
			String subject = triple.getSubject().getURI();
			Collection<String> map = new HashSet<String>();
			if (sameAsMapping.containsKey(subject)) {
				map = sameAsMapping.get(subject);
			}
			map.add(triple.getObject().getURI());
			sameAsMapping.put(subject, map);
		}
		if (count % 1000000 == 0) {
			System.out.println(
					"Searched through " + count + " triples and found " + sameAsMapping.size() + " sameAs instances");
			System.out
					.println("RAM usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		}
	}

	public Map<String, Collection<String>> getMapping() {
		return this.sameAsMapping;
	}

}
