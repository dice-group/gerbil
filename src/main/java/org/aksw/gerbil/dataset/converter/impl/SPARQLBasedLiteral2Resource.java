package org.aksw.gerbil.dataset.converter.impl;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.dataset.converter.AbstractLiteral2Resource;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ResourceFactory;

public class SPARQLBasedLiteral2Resource extends AbstractLiteral2Resource {

	private String endpoint;
	private String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?res ";
	private String whereClause = " WHERE {?res rdfs:label ";
	private String defaultGraph = "";
	
	public SPARQLBasedLiteral2Resource(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public SPARQLBasedLiteral2Resource(String endpoint, String defaultGraph) {
		this.endpoint = endpoint;
		this.defaultGraph = defaultGraph;
	}

	@Override
	public Set<String> getResourcesForLiteral(String literal, String qLang) {
		Set<String> ret = new HashSet<String>();
		if(qLang==null) {
		    //Get language from literal
		    qLang="en";
		    if(literal.lastIndexOf("@")!=-1){
			    qLang = literal.substring(literal.lastIndexOf("@")+1, literal.length());
			    literal = literal.substring(1, literal.lastIndexOf("@")-1);
		    }
		}
		StringBuilder queryString = new StringBuilder(this.queryString).append(defaultGraph).append(whereClause).append("\"")
				.append(literal).append("\"").append("@").append(qLang).append("}");
		Query q;
		try {
			q = QueryFactory.create(queryString.toString());
		}catch(Exception e) {
			ret.add(literal);
			return ret;
		}
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, q);
		ResultSet res = exec.execSelect();
		while(res.hasNext()){
			ret.add(res.next().getResource("res").getURI());
		}
		if(ret.isEmpty()){
			ret.add(literal);
		}
		return ret;
	}

}
