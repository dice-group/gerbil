package org.aksw.gerbil.tools;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.index.Indexer;
import org.aksw.gerbil.semantic.sameas.index.LuceneConstants.IndexingStrategy;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class InitialIndexTool {

	private static final String OUTPUT_FOLDER="lucene_index";

	private static final String SPARQL_GET = "select distinct ?s ?o where {?s owl:sameAs ?o}";
	
	private static final IndexingStrategy STRATEGY = IndexingStrategy.TermQuery;

	private static String service = "http://dbpedia.org/sparql";
	
	public static void main(String[] args) throws GerbilException {
		Indexer index = new Indexer(OUTPUT_FOLDER, STRATEGY);
		index(index);
	}
	
	public static void index(Indexer index){
		int offset=0, limit=20000;
		boolean test=true;

		Query q = QueryFactory.create(SPARQL_GET);
		q.setLimit(limit);

		//Create here! 
		Set<String> sameAsBlock = new HashSet<String>();
		RDFNode old = null;
		
		do{
			q.setOffset(offset);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service , q);
			ResultSet res = qexec.execSelect();
			//get results
			int size=0;
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
					index.index(sameAsBlock);
					sameAsBlock.clear();
					sameAsBlock.add(node1.toString());
				}
				else{
					sameAsBlock.add(node1.toString());
				}
			}
			if(size<limit){
				//No more results
				test=false;
			}
			//Set offset so it starts immediately after last results
			offset+=limit;
		}while(test);
		//done
		if(!sameAsBlock.isEmpty()){
			index.index(sameAsBlock);
			sameAsBlock.clear();
		}
	}

}
