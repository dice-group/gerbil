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

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.transfer.nif.Marking;
import org.junit.Ignore;

@Ignore
public class MatchingTestExample<T extends Marking> {
    public List<T> annotatorResult;
    public List<T> goldStandard;
    public EvaluationCounts expectedCounts;

    public MatchingTestExample(T annotatorResult[], T goldStandard[], int[] expectedCounts) {
        this.annotatorResult = Arrays.asList(annotatorResult);
        this.goldStandard = Arrays.asList(goldStandard);
        this.expectedCounts = new EvaluationCounts(expectedCounts[0], expectedCounts[1], expectedCounts[2]);
    }

}
