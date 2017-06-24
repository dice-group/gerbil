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
	private String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?res WHERE {?res rdfs:label ";
	
	public SPARQLBasedLiteral2Resource(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public Set<String> getResourcesForLiteral(String literal, String qLang) {
		Set<String> ret = new HashSet<String>();
		if(qLang==null) {
		    //Get language from literal
		    qLang="en";
		    if(literal.lastIndexOf("@")!=-1){
			    qLang = literal.substring(literal.lastIndexOf("@")+1, literal.length());
			    literal = literal.substring(0, literal.lastIndexOf("@"));
		    }
		    
		}
		StringBuilder queryString = new StringBuilder(this.queryString).
				append("\"").append(literal).append("\"@").append(qLang).append("}");
		Query q = QueryFactory.create(queryString.toString());
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
