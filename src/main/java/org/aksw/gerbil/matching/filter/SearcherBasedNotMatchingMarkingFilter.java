package org.aksw.gerbil.matching.filter;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

/**
 * This implementation of a marking filter removes every marking from the given
 * list that does not match the given gold standard list. For identifying those
 * markings a {@link MatchingsSearcher} is used.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class SearcherBasedNotMatchingMarkingFilter<T extends Marking> implements NotMatchingMarkingFilter<T> {

    protected MatchingsSearcher<T> searcher;

    public SearcherBasedNotMatchingMarkingFilter(MatchingsSearcher<T> searcher) {
        this.searcher = searcher;
    }

    @Override
    public List<T> filterMarkings(List<T> markings, List<T> goldStandard) {
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(goldStandard.size());
        List<T> filteredMarkings = new ArrayList<T>(markings.size());
        for (T marking : markings) {
            matchingElements = searcher.findMatchings(marking, goldStandard, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                filteredMarkings.add(marking);
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
            }
        }
        return filteredMarkings;
    }

}
