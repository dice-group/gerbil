package org.aksw.gerbil.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.index.Indexer;
import org.aksw.gerbil.semantic.sameas.index.LuceneConstants.IndexingStrategy;
import org.aksw.gerbil.semantic.sameas.index.Searcher;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class InitialIndexTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialIndexTool.class);

	private static final String OUTPUT_FOLDER="lucene_index";
	private static final String SPARQL_GET = "select distinct ?s ?o where {?s <http://www.w3.org/2002/07/owl#sameAs> ?o} ORDER BY ?s";
	
	private static final IndexingStrategy STRATEGY = IndexingStrategy.TermQuery;

	private static String service = "http://dbpedia.org/sparql";
	
	public static void main(String[] args) throws GerbilException{
		Indexer index = new Indexer(OUTPUT_FOLDER, STRATEGY);
		SimpleDateFormat format = new SimpleDateFormat();
		LOGGER.info("Start indexing at {}", format.format(Calendar.getInstance().getTime()));		
		index(index);
		LOGGER.info("Indexing finished at {}", format.format(Calendar.getInstance().getTime()));
	}
	
	public static void index(Indexer index) throws GerbilException{
		int offset=0, limit=10000;
		boolean test=true;

		Query q = QueryFactory.create(SPARQL_GET);
		q.setLimit(limit);

		//Create here! 
		Set<String> sameAsBlock = new HashSet<String>();
		RDFNode old = null;
		int rounds=0, size=0;
		long total=0;
		do{
			q.setOffset(offset);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service , q);
			ResultSet res = qexec.execSelect();
			//get results
			size=0;
			rounds++;
			//Go through all elements
			while(res.hasNext()){
				size++;
				QuerySolution solution = res.next();
				RDFNode node1 = solution.get("s");
				RDFNode node2 = solution.get("o");
				if(node1.equals(old)){
					sameAsBlock.add(node2.toString());
				}
				else if(old!=null){
					//Enitity is finished
					index.index(old.toString(), sameAsBlock);
					total+=sameAsBlock.size();

					sameAsBlock.clear();
					//Add Uri
					sameAsBlock.add(node2.toString());
					old=node1;
				}
				else{
					//First run
					sameAsBlock.add(node2.toString());
					old=node1;
				}
			}
			if(size<limit){
				//No more results
				test=false;
			}
			//Set offset so it starts immediately after last results
			offset+=limit;
			LOGGER.info("Got {} triples...(Sum: {})",size, limit*(rounds-1)+size);
		}while(test);
		//done
		if(!sameAsBlock.isEmpty()){
			index.index(old.toString(), sameAsBlock);
			sameAsBlock.clear();
		}
		LOGGER.info("Successfully indexed {} triples",total);
	}

}
