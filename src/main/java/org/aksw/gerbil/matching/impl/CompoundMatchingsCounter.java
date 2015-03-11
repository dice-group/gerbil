package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public class CompoundMatchingsCounter<T extends Marking> extends AbstractMatchingsCounter<T> {

    public AbstractMatchingsCounter<T> matchingsCounter[];

    public CompoundMatchingsCounter(@SuppressWarnings("unchecked") AbstractMatchingsCounter<T>... matchingsCounter) {
        this.matchingsCounter = matchingsCounter;
    }

    @Override
    protected BitSet findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        BitSet matchings;
        matchings = matchingsCounter[0].findMatching(expectedElement, annotatorResult, alreadyUsedResults);
        for (int i = 1; (i < matchingsCounter.length) && (!matchings.isEmpty()); i++) {
            matchings.intersect(matchingsCounter[i].findMatching(expectedElement, annotatorResult, alreadyUsedResults));
        }
        return matchings;
    }
}
