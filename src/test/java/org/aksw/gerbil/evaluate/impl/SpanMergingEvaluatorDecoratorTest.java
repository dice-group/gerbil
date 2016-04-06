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
package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SpanMergingEvaluatorDecoratorTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { Arrays.asList(new Span[0]), new Span[0] });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(0, 5), new SpanImpl(2, 2)),
                new Span[] { new SpanImpl(0, 5) } });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(0, 5), new SpanImpl(0, 3)),
                new Span[] { new SpanImpl(0, 5) } });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(0, 5), new SpanImpl(2, 3)),
                new Span[] { new SpanImpl(0, 5) } });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(0, 5), new SpanImpl(2, 6)),
                new Span[] { new SpanImpl(0, 5), new SpanImpl(2, 6) } });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(0, 5), new SpanImpl(2, 6), new SpanImpl(1, 3)),
                new Span[] { new SpanImpl(0, 5), new SpanImpl(2, 6) } });
        testConfigs.add(new Object[] { Arrays.asList(new SpanImpl(2, 3), new SpanImpl(0, 5), new SpanImpl(1, 3)),
                new Span[] { new SpanImpl(0, 5) } });
        testConfigs.add(new Object[] {
                Arrays.asList(new TypedSpanImpl(2, 3, new HashSet<String>(Arrays.asList("T1"))), new SpanImpl(0, 5),
                        new SpanImpl(1, 3)),
                new Span[] { new TypedSpanImpl(0, 5, new HashSet<String>(Arrays.asList("T1"))) } });
        testConfigs.add(new Object[] {
                Arrays.asList(new TypedSpanImpl(2, 3, new HashSet<String>(Arrays.asList("T1", "T3"))), new SpanImpl(0,
                        5), new TypedSpanImpl(1, 3, new HashSet<String>(Arrays.asList("T2", "T3")))),
                new Span[] { new TypedSpanImpl(0, 5, new HashSet<String>(Arrays.asList("T1", "T2", "T3"))) } });
        testConfigs.add(new Object[] {
                Arrays.asList(new NamedEntity(2, 3, new HashSet<String>(Arrays.asList("E1", "E3"))),
                        new SpanImpl(0, 5), new NamedEntity(1, 3, new HashSet<String>(Arrays.asList("E2", "E3")))),
                new Span[] { new NamedEntity(0, 5, new HashSet<String>(Arrays.asList("E1", "E2", "E3"))) } });
        testConfigs.add(new Object[] {
                Arrays.asList(new TypedSpanImpl(2, 3, new HashSet<String>(Arrays.asList("T1"))), new SpanImpl(0, 5),
                        new NamedEntity(1, 3, "E1")),
                new Span[] { new TypedNamedEntity(0, 5, "E1", new HashSet<String>(Arrays.asList("T1"))) } });
        return testConfigs;
    }

    private List<Span> markings;
    private Span expectedMarkings[];

    public SpanMergingEvaluatorDecoratorTest(List<Span> markings, Span[] expectedMarkings) {
        this.markings = markings;
        this.expectedMarkings = expectedMarkings;
    }

    @Test
    public void test() {
        SpanMergingEvaluatorDecorator<Span> merger = new SpanMergingEvaluatorDecorator<>(null);
        markings = merger.merge(markings);
        Span results[] = markings.toArray(new Span[markings.size()]);

        StartPosBasedComparator comparator = new StartPosBasedComparator();
        Arrays.sort(results, comparator);
        Arrays.sort(expectedMarkings, comparator);
        Assert.assertArrayEquals(expectedMarkings, results);
    }
}
