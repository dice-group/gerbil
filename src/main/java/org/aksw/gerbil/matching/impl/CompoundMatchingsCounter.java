package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public class CompoundMatchingsCounter<T extends Marking> implements MatchingsSearcher<T> {

    public MatchingsSearcher<T> matchingsCounter[];

    public CompoundMatchingsCounter(@SuppressWarnings("unchecked") MatchingsSearcher<T>... matchingsCounter) {
        this.matchingsCounter = matchingsCounter;
    }

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        BitSet matchings;
        matchings = matchingsCounter[0].findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
        for (int i = 1; (i < matchingsCounter.length) && (!matchings.isEmpty()); i++) {
            matchings
                    .intersect(matchingsCounter[i].findMatchings(expectedElement, annotatorResult, alreadyUsedResults));
        }
        return matchings;
    }
}
