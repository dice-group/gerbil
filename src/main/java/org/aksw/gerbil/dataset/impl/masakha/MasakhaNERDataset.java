package org.aksw.gerbil.dataset.impl.masakha;

import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;
import org.aksw.gerbil.dataset.impl.conll.CoNLLTypeRetriever;

/**
 * An extension of the {@link GenericCoNLLDataset} class that can handle
 * datasets of the MasakhaNER dataset collection.
 * 
 * @author Neha Pokharel
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class MasakhaNERDataset extends GenericCoNLLDataset {

    private static final int ANNOTATION_COLUMN = 1;
    private static final int URI_COLUMN = -1;
    private static final CoNLLTypeRetriever TYPE_TAGS = new CoNLLTypeRetriever("LOC", null, null, null, "DATE", "PER",
            null, null, null, "ORG");

    public MasakhaNERDataset(String file) {
        super(file, ANNOTATION_COLUMN, URI_COLUMN, TYPE_TAGS);
        setColumnSeparator(" ");
    }
}
