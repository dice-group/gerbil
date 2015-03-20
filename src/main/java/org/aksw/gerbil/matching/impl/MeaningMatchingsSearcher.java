package org.aksw.gerbil.matching.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Meaning;

import com.carrotsearch.hppc.BitSet;

public class MeaningMatchingsSearcher<T extends Meaning> implements MatchingsSearcher<T> {

    private SameAsRetriever sameAsRetriever;
    private UriKBClassifier uriKBClassifier;

    public MeaningMatchingsSearcher() {
    }

    public MeaningMatchingsSearcher(SameAsRetriever sameAsRetriever) {
        this.sameAsRetriever = sameAsRetriever;
    }

    public MeaningMatchingsSearcher(UriKBClassifier uriKBClassifier) {
        this.uriKBClassifier = uriKBClassifier;
    }

    public MeaningMatchingsSearcher(SameAsRetriever sameAsRetriever, UriKBClassifier uriKBClassifier) {
        this.sameAsRetriever = sameAsRetriever;
        this.uriKBClassifier = uriKBClassifier;
    }

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResults, BitSet alreadyUsedResults) {
        String expectedUri = expectedElement.getUri();
        BitSet matching = new BitSet(alreadyUsedResults.size());
        Set<String> extendedUris = null, extendedAnnotResult = null;
        boolean expectingKBUri, annotatorHasKBUri;

        // Extend the expected result
        if (sameAsRetriever != null) {
            extendedUris = sameAsRetriever.retrieveSameURIs(expectedUri);
        }
        if (extendedUris == null) {
            extendedUris = new HashSet<String>();
            extendedUris.add(expectedUri);
        }
        // Check whether a link to a known KB is expected
        expectingKBUri = (uriKBClassifier == null) ? true : uriKBClassifier.containsKBUri(extendedUris);

        T annotatorResult;
        for (int i = 0; i < annotatorResults.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                annotatorResult = annotatorResults.get(i);
                // extend the annotator result, if possible
                if (sameAsRetriever == null) {
                    extendedAnnotResult = null;
                } else {
                    extendedAnnotResult = sameAsRetriever.retrieveSameURIs(annotatorResult.getUri());
                }
                // if the result couldn't be extended -> use the single URI for
                // comparing
                if (extendedAnnotResult == null) {
                    annotatorHasKBUri = (uriKBClassifier == null) ? true : uriKBClassifier.isKBUri(annotatorResult
                            .getUri());
                    // if both can be mapped to a KB and the URIs equal
                    if ((annotatorHasKBUri) && (expectingKBUri) && (annotatorResult.getUri().equals(expectedUri))) {
                        matching.set(i);
                        // else if both are not mapped to a KB
                    } else if ((!annotatorHasKBUri) && (!expectingKBUri)) {
                        matching.set(i);
                    }
                } else {
                    extendedAnnotResult.add(annotatorResult.getUri());
                    annotatorHasKBUri = (uriKBClassifier == null) ? true : uriKBClassifier
                            .containsKBUri(extendedAnnotResult);
                    // if both can be mapped to a KB
                    if ((annotatorHasKBUri) && (expectingKBUri)) {
                        // if the sets are intersecting
                        extendedAnnotResult.retainAll(extendedUris);
                        if (extendedAnnotResult.size() > 0) {
                            matching.set(i);
                        }
                        // else if both are not mapped to a KB
                    } else if ((!annotatorHasKBUri) && (!expectingKBUri)) {
                        matching.set(i);
                    }
                }
            }
        }
        return matching;
    }
}
