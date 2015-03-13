package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public interface MatchingsSearcher<T extends Marking> {
    public abstract BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults);
}
