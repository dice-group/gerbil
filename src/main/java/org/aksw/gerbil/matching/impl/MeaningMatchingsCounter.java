package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Meaning;

import com.carrotsearch.hppc.BitSet;

public class MeaningMatchingsCounter<T extends Meaning> extends AbstractMatchingsCounter<T> {

    @Override
    protected BitSet findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        String expectedUri = expectedElement.getUri();
        BitSet matching = new BitSet(alreadyUsedResults.size());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if ((!alreadyUsedResults.get(i)) && (annotatorResult.get(i).getUri().equals(expectedUri))) {
                matching.set(i);
                return matching;
            }
        }
        return matching;
    }

}
