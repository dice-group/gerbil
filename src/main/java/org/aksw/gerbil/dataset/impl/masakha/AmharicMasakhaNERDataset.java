package org.aksw.gerbil.dataset.impl.masakha;

import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;

/**
 * An extension of the {@link GenericCoNLLDataset} class that can handle
 * Amharic datasets of the MasakhaNER dataset collection.
 * 
 * @author Neha Pokharel
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class AmharicMasakhaNERDataset extends MasakhaNERDataset {

    public AmharicMasakhaNERDataset(String file) {
        super(file);
        setWhitespace(" ፡ ");
    }
}
