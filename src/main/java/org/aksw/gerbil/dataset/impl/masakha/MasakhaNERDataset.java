package org.aksw.gerbil.dataset.impl.masakha;

import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;
import org.aksw.gerbil.dataset.impl.conll.CoNLLTypeRetriever;

public class MasakhaNERDataset extends GenericCoNLLDataset {
	
	private static final int ANNOTATION_COLUMN = 1;
    private static final int URI_COLUMN = -1;
    private static final CoNLLTypeRetriever TYPE_TAGS = new CoNLLTypeRetriever("LOC", null, null, null,
        "DATE", "PER", null, null, null, "ORG");

    public MasakhaNERDataset(String file) {
    	super(file, ANNOTATION_COLUMN, URI_COLUMN, TYPE_TAGS);
    }
}

