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
import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class StrongSpanMatchingsSearcher<T extends Span> implements MatchingsSearcher<T> {

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eLength = expectedElement.getLength();
        BitSet matching = new BitSet(alreadyUsedResults.size());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if ((!alreadyUsedResults.get(i)) && (eStart == annotatorResult.get(i).getStartPosition())
                    && (eLength == annotatorResult.get(i).getLength())) {
                matching.set(i);
                // yes, we have found a matching position, but note, that we
                // have to find all matching positions!
            }
        }
        return matching;
    }

}
