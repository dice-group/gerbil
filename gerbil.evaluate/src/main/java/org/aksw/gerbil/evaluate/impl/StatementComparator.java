package org.aksw.gerbil.evaluate.impl;

import java.util.Comparator;

import org.apache.jena.rdf.model.Statement;

public class StatementComparator implements Comparator<Statement> {

	@Override
	public int compare(Statement annotator, Statement goldStd) {
		Double tValue1 = annotator.getLiteral().getDouble();
		Double tValue2 = goldStd.getLiteral().getDouble();
		return tValue1.compareTo(tValue2);
	}

}
