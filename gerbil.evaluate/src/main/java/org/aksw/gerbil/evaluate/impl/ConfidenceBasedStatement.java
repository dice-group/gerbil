package org.aksw.gerbil.evaluate.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

public class ConfidenceBasedStatement {

	private double confidence=1;
	private Set<Statement> stmts;

	public ConfidenceBasedStatement(Set<Statement> stmts, Property confidenceProperty) {
		this.stmts = stmts;
		//check for confidence otherwise 1
		for(Statement stmt : stmts) {
			if(stmt.getPredicate().equals(confidenceProperty)) {
				this.confidence = stmt.getDouble();
			}
		}
	}
	
	public Set<Statement> getStatements(){
		return this.stmts;
	}

	public Set<Statement> getStatements(Property... props){
		Set<Statement> reduced = new HashSet<Statement>();
		for(Property p : props) {
			for(Statement stmt : stmts) {
				if(stmt.getPredicate().equals(p)){
					reduced.add(stmt);
				}
			}
		}
		return reduced;
	}
	
	public double getConfidence() {
		return confidence;
	}

}
