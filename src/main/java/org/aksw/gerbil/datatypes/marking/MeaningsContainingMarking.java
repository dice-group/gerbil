package org.aksw.gerbil.datatypes.marking;

import java.util.Collection;

import org.aksw.gerbil.transfer.nif.Meaning;

public interface MeaningsContainingMarking {

    public Collection<? extends Meaning> getMeanings();
}
