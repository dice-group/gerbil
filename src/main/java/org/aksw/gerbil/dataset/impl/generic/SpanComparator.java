package org.aksw.gerbil.dataset.impl.generic;

import java.util.Comparator;

import org.aksw.gerbil.transfer.nif.Span;

public class SpanComparator implements Comparator<Span>{
	  @Override
	    public int compare(Span s1, Span s2) {
	        // sort them based on their length
	        int diff = s1.getLength() - s2.getLength();
	        if (diff == 0) {
	            return 0;
	        } else if (diff < 0) {
	            return -1;
	        } else {
	            return 1;
	        }
	    }
}
