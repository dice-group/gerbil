/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.matching.impl;

import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

public class WeakSpanMatchingTest extends AbstractMatchingsCounterTest<Span> {

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
                    new SpanImpl(60, 10) }, new int[] { 4, 0, 0 }),
            // test case with overlapping spans in the annotator result matching
            // a single span of the gold standard
            new MatchingTestExample<Span>(new Span[] { new SpanImpl(0, 10), new SpanImpl(6, 10) },
                    new Span[] { new SpanImpl(2, 10) }, new int[] { 1, 1, 0 }) };

    @SuppressWarnings("unchecked")
    public WeakSpanMatchingTest() {
        super(new WeakSpanMatchingsCounter<Span>(), EXAMPLES);
    }
}
