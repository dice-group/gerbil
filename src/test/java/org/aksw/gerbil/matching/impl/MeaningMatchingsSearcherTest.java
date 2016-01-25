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

import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;

public class MeaningMatchingsSearcherTest extends AbstractMatchingsCounterTest<Meaning> {

    private static final UriKBClassifier CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier("http://kb/");

    @SuppressWarnings("rawtypes")
    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<Meaning>(new Meaning[] {}, new Meaning[] {}, new int[] { 0, 0, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<Meaning>(new Meaning[] { new Annotation("http://kb/1") }, new Meaning[] {},
                    new int[] { 0, 1, 0 }),
            // test case with empty gold standard
            new MatchingTestExample<Meaning>(new Meaning[] {}, new Meaning[] { new Annotation("http://kb/1") },
                    new int[] { 0, 0, 1 }),
            // test case with single exact matching Meanings
            new MatchingTestExample<Meaning>(new Meaning[] { new Annotation("http://kb/1") },
                    new Meaning[] { new Annotation("http://kb/1") }, new int[] { 1, 0, 0 }),
            // test case with several exact matching Meanings
            new MatchingTestExample<Meaning>(
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                            new Annotation("http://kb/3") },
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                            new Annotation("http://kb/3") },
                    new int[] { 3, 0, 0 }),
            // test case with several exact matching Meanings with a different
            // order
            new MatchingTestExample<Meaning>(
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                            new Annotation("http://kb/3") },
                    new Meaning[] { new Annotation("http://kb/2"), new Annotation("http://kb/3"),
                            new Annotation("http://kb/1") },
                    new int[] { 3, 0, 0 }),
            // test case with several exact matching Meanings with the same URIs
            new MatchingTestExample<Meaning>(
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/1"),
                            new Annotation("http://kb/1") },
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/1"),
                            new Annotation("http://kb/1") },
                    new int[] { 3, 0, 0 }),
            // test case with several exact matching Meanings with two of them
            // that couldn't be mapped to the KB
            new MatchingTestExample<Meaning>(
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://ukb/2"),
                            new Annotation("http://ukb/3") },
                    new Meaning[] { new Annotation("http://aukb/2"), new Annotation("http://aukb/3"),
                            new Annotation("http://kb/1") },
                    new int[] { 3, 0, 0 }),
            // test case with one exact matching Meanings, one wrong matching
            // and a missing matching
            new MatchingTestExample<Meaning>(
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://ukb/2") },
                    new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                            new Annotation("http://kb/3") },
                    new int[] { 1, 1, 2 }) };

    @SuppressWarnings("unchecked")
    public MeaningMatchingsSearcherTest() {
        super(new ClassifierBasedMeaningMatchingsSearcher<Meaning>(CLASSIFIER), EXAMPLES);
    }

}
