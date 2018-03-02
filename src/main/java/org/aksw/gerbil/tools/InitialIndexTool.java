package org.aksw.gerbil.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.index.Indexer;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

public class InitialIndexTool {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InitialIndexTool.class);

	private static final String OUTPUT_FOLDER = "lucene_index";
	private static final String SPARQL_GET = "select distinct ?s ?o where {?s <http://www.w3.org/2002/07/owl#sameAs> ?o}";

	private static String service = "http://de.dbpedia.org/sparql";

	private static Object owlSameAs="<http://www.w3.org/2002/07/owl#sameAs>";

	public static void main(String[] args) throws GerbilException, IOException {
		Indexer index = new Indexer(OUTPUT_FOLDER);
		SimpleDateFormat format = new SimpleDateFormat();
		Date start = Calendar.getInstance().getTime();
		LOGGER.info("Start indexing at {}", format.format(start));
		indexFolder(index, args[0]);
		index.close();
		Date end = Calendar.getInstance().getTime();
		LOGGER.info("Indexing finished at {}", format.format(end));
		LOGGER.info("Indexing took: "
				+ DurationFormatUtils.formatDurationHMS(end.getTime()
						- start.getTime()));
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
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service,
					q);
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
			String avg = DurationFormatUtils
					.formatDurationHMS((end.getTime() - start.getTime())
							/ rounds);
			String avgQ = DurationFormatUtils
					.formatDurationHMS((endQ.getTime() - startQ.getTime()));
			String avgI = DurationFormatUtils.formatDurationHMS(sumI);
			sumI = 0;
			LOGGER.info(
					"Got {} triples...(Sum: {}, AvgTime: {}, QueryTime: {}, IndexTime: {})",
					size, limit * (rounds - 1) + size, avg, avgQ, avgI);
		} while (test);
		// done
		if (!sameAsBlock.isEmpty()) {
			index.index(old.toString(), sameAsBlock);
			sameAsBlock.clear();
		}
		LOGGER.info("Successfully indexed {} triples", total);
	}
	


	public static void indexFolder(Indexer index, String folder) throws GerbilException, IOException{
		File dir = new File(folder);
		
		for(File f : dir.listFiles()){
			if(f.getName().endsWith(".nt"))
				index(index, f.getAbsolutePath());
		}
	}
	
	public static void index(Indexer index, String file)
			throws GerbilException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), Charset.forName("UTF-8")));

		// Create here!
		Set<String> sameAsBlock = new HashSet<String>();

		long total = 0, size=0, rounds=0;
		String line = "";
		String old = null;
		Date start = Calendar.getInstance().getTime();
		while ((line = reader.readLine()) != null) {
			String[] split = line.split("\\s+");
			if(!split[1].equals(owlSameAs)) {
				continue;
			}
			String node1 = split[0].replace("<", "").replace(">", "");
			String node2 = split[2];
			node2 = node2.substring(node2.indexOf("<")+1, node2.lastIndexOf(">")).trim();
			
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
			if(size%100000==0){
				Date end = Calendar.getInstance().getTime();
				rounds++;
				String avgTime  =DurationFormatUtils.formatDurationHMS((end.getTime()
						- start.getTime())/rounds);
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
