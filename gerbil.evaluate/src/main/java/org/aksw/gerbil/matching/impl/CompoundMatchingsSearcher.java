/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public class CompoundMatchingsSearcher<T extends Marking> implements MatchingsSearcher<T> {

    public MatchingsSearcher<T> matchingsCounter[];

    public CompoundMatchingsSearcher(@SuppressWarnings("unchecked") MatchingsSearcher<T>... matchingsCounter) {
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
