package org.aksw.gerbil.matching.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Meaning;

import com.carrotsearch.hppc.BitSet;

public class MeaningMatchingsCounter<T extends Meaning> implements MatchingsSearcher<T> {

    private SameAsRetriever sameAsRetriever;

    public MeaningMatchingsCounter() {
    }

    public MeaningMatchingsCounter(SameAsRetriever sameAsRetriever) {
        this.sameAsRetriever = sameAsRetriever;
    }

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        String expectedUri = expectedElement.getUri();
        BitSet matching = new BitSet(alreadyUsedResults.size());
        Set<String> extendedUris = null, extendedAnnotResult = null;
        if (sameAsRetriever != null) {
            extendedUris = sameAsRetriever.retrieveSameURIs(expectedUri);
            if (extendedUris == null) {
                extendedUris = new HashSet<String>();
                extendedUris.add(expectedUri);
            }
        }
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                if (sameAsRetriever == null) {
                    if ((annotatorResult.get(i).getUri().equals(expectedUri))) {
                        matching.set(i);
                    }
                } else {
                    extendedAnnotResult = sameAsRetriever.retrieveSameURIs(annotatorResult.get(i).getUri());
                    if (extendedAnnotResult == null) {
                        if (extendedUris.contains(annotatorResult.get(i).getUri())) {
                            matching.set(i);
                        }
                    } else {
                        extendedAnnotResult.retainAll(extendedUris);
                        if (extendedAnnotResult.size() > 0) {
                            matching.set(i);
                        }
                    }
                }
            }
        }
        return matching;
    }
}
