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

import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

public class EqualsBasedMatchingsSearcherTest extends AbstractMatchingsCounterTest<Span> {

    @SuppressWarnings("rawtypes")
    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<Span>(new Span[] {}, new Span[] {}, new int[] { 0, 0, 0 }),
            // test case with empty gold standard
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10) }, new Span[] {}, new int[] { 0, 1, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<Span>(new Span[] {}, new Span[] { new SpanImpl(0, 10) }, new int[] { 0, 0, 1 }),
            // test case with single exact matching Spans
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(1, 10) }, new Span[] { new SpanImpl(1, 10) },
                    new int[] { 1, 0, 0 }),
            // test case with several exact matching Spans
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new Span[] {
                    new SpanImpl(0, 10), new SpanImpl(20, 10) }, new int[] { 2, 0, 0 }),
            // test case with one matching pair and another not matching pair
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(60, 10), new SpanImpl(20, 10) }, new Span[] {
                    new SpanImpl(0, 10), new SpanImpl(20, 10) }, new int[] { 1, 1, 1 }),
            // test case with partly overlapping spans
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10), new SpanImpl(40, 10),
                    new SpanImpl(62, 3) }, new Span[] { new SpanImpl(2, 10), new SpanImpl(16, 10), new SpanImpl(42, 4),
                    new SpanImpl(60, 10) }, new int[] { 0, 4, 4 }),
            // test case with overlapping spans in the annotator result matching
            // a single span of the gold standard
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(6, 10) },
                    new Span[] { new SpanImpl(2, 10) }, new int[] { 0, 2, 1 }),
            // test case with the same element several times in the system response
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10) },
                    new Span[] { new SpanImpl(0, 10), new SpanImpl(0, 10) }, new int[] { 1, 0, 1 }),
            // test case with the same element several times in the gold standard
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(0, 10) },
                    new Span[] { new SpanImpl(0, 10) }, new int[] { 1, 1, 0 }),
            // test case with the same element several times in the system response
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(0, 10) },
                    new Span[] { new SpanImpl(0, 10), new SpanImpl(0, 10) }, new int[] { 2, 0, 0 }) };

    @SuppressWarnings("unchecked")
    public EqualsBasedMatchingsSearcherTest() {
        super(new EqualsBasedMatchingsSearcher<Span>(), EXAMPLES);
    }
}
