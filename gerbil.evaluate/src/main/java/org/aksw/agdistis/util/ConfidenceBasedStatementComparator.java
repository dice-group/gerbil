package org.aksw.agdistis.util;

import java.util.Comparator;

import org.aksw.gerbil.evaluate.impl.ConfidenceBasedStatement;

public class ConfidenceBasedStatementComparator implements Comparator<ConfidenceBasedStatement> {


	@Override
	public int compare(ConfidenceBasedStatement o1, ConfidenceBasedStatement o2) {
		return Double.valueOf(o1.getConfidence()).compareTo(Double.valueOf(o2.getConfidence()));
	}

}
