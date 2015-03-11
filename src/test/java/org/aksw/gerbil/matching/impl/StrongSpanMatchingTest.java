package org.aksw.gerbil.matching.impl;

import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

public class StrongSpanMatchingTest extends AbstractMatchingsCounterTest<Span> {

    @SuppressWarnings("rawtypes")
    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<Span>(new Span[] {}, new Span[] {}, new int[] { 0, 0, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10) }, new Span[] {}, new int[] { 0, 1, 0 }),
            // test case with empty gold standard
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
                    new Span[] { new SpanImpl(2, 10) }, new int[] { 0, 2, 1 }) };

    @SuppressWarnings("unchecked")
    public StrongSpanMatchingTest() {
        super(new StrongSpanMatchingsCounter<Span>(), EXAMPLES);
    }
}
