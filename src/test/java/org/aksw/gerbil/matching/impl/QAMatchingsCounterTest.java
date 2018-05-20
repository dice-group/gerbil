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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.agdistis.util.Triple;
import org.aksw.agdistis.util.TripleIndex;
import org.aksw.gerbil.dataset.converter.Literal2Resource;
import org.aksw.gerbil.dataset.converter.Literal2ResourceManager;
import org.aksw.gerbil.dataset.converter.impl.SPARQLBasedLiteral2Resource;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.ResourceAnswerSet;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

@SuppressWarnings("rawtypes")
public class QAMatchingsCounterTest {

    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<>(new AnswerSet[] {}, new AnswerSet[] {}, new int[] { 0, 0, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<AnswerSet>(
                    new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1")) }, new AnswerSet[] {},
                    new int[] { 0, 1, 0 }),
            // test case with empty gold standard
            new MatchingTestExample<AnswerSet>(new AnswerSet[] {}, new AnswerSet[] { new AnswerSet<String>(
                    Sets.newHashSet("http://kb.com/1")) }, new int[] { 0, 0, 1 }),
            // test case with single exact matching AnswerSets
            new MatchingTestExample<AnswerSet>(
                    new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1")) },
                    new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1")) }, new int[] { 1, 0, 0 }),
            // test case with several exact matching AnswerSets
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1",
                    "http://kb.com/2", "http://kb.com/3")) }, new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet(
                    "http://kb.com/1", "http://kb.com/2", "http://kb.com/3")) }, new int[] { 3, 0, 0 }),
            // test case with several exact matching AnswerSets with a different
            // order
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1",
                    "http://kb.com/2", "http://kb.com/3")) }, new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet(
                    "http://kb.com/2", "http://kb.com/3", "http://kb.com/1")) }, new int[] { 3, 0, 0 }),
            // the same test case with expected ResourceAnswerSets and given
            // String answer sets
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet(
                    "http://kb.org/2", "http://kb.org/3", "http://kb.org/1")) },
                    new AnswerSet[] { new ResourceAnswerSet(Sets.newHashSet(new Annotation("http://kb.org/1"),
                            new Annotation("http://kb.org/2"), new Annotation("http://kb.org/3"))) }, new int[] { 3, 0,
                            0 }),
            // the same test case with expected ResourceAnswerSets and given
            // String answer sets and URIs that are not valid for the given URL
            // validator but match the expected URIs
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/2",
                    "http://kb.com/3", "http://kb.com/1")) }, new AnswerSet[] { new ResourceAnswerSet(Sets.newHashSet(
                    new Annotation("http://kb.com/1"), new Annotation("http://kb.com/2"), new Annotation("http://kb.com/3"))) },
                    new int[] { 3, 0, 0 }),
            // test case with one exact matching AnswerSets, one wrong matching
            // and a missing matching
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1",
                    "http://ukb.com/2")) }, new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("http://kb.com/1",
                    "http://kb.com/2", "http://kb.com/3")) }, new int[] { 1, 1, 2 }),
            // The Annotator returned the label of the resource instead of the
            // correct resource
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet<String>(Sets.newHashSet("\"Paris\"@en")) },
                    new AnswerSet[] { new ResourceAnswerSet(Sets.newHashSet(new Annotation(
                            "http://dbpedia.org/resource/Paris"))) }, new int[] { 1, 1, 0 }) };

    private MatchingsCounter<AnswerSet> counter;
    private List<List<AnswerSet>> annotatorResult;
    private List<List<AnswerSet>> goldStandard;
    private List<EvaluationCounts> expectedCounts;

    @SuppressWarnings("unchecked")
    public QAMatchingsCounterTest() {
        Map<String, String[]> labelToUriMapping = new HashMap<>();
        labelToUriMapping.put("Paris", new String[] { "http://dbpedia.org/resource/Paris",
                "http://dbpedia.org/resource/Paris,_Arkansas" });
        Literal2ResourceManager emptyManager = new Literal2ResourceManager();
        Literal2Resource converter = new SPARQLBasedLiteral2Resource("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org");
        emptyManager.registerLiteral2Resource(converter);
        this.counter = new QAMatchingsCounter(new MockupIndex(labelToUriMapping), new UrlValidator(),
                new DummyUriKbClassifier(), emptyManager);
        // this.counter = new QAMatchingsCounter(RootConfig.createTripleIndex(),
        // new UrlValidator(),
        // new DummyUriKbClassifier());

        this.annotatorResult = new ArrayList<List<AnswerSet>>(EXAMPLES.length);
        this.goldStandard = new ArrayList<List<AnswerSet>>(EXAMPLES.length);
        this.expectedCounts = new ArrayList<EvaluationCounts>(EXAMPLES.length);
        for (int i = 0; i < EXAMPLES.length; i++) {
            annotatorResult.add(EXAMPLES[i].annotatorResult);
            goldStandard.add(EXAMPLES[i].goldStandard);
            expectedCounts.add(EXAMPLES[i].expectedCounts);
        }
    }

    @Test
    public void test() {
        EvaluationCounts counts[] = new EvaluationCounts[annotatorResult.size()];
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = counter.countMatchings(annotatorResult.get(i), goldStandard.get(i));
        }
        for (int i = 0; i < counts.length; ++i) {
            Assert.assertEquals("Counts of the element " + i + " are different.", expectedCounts.get(i), counts[i]);
        }
    }

    protected class MockupIndex extends TripleIndex {

        private Map<String, String[]> labelToUriMapping;

        protected MockupIndex(Map<String, String[]> labelToUriMapping) {
            super(null, null, null);
            this.labelToUriMapping = labelToUriMapping;
        }

        @Override
        public List<Triple> search(String subject, String predicate, String object, int maxNumberOfResults) {
            List<Triple> results = new ArrayList<>();
            if (labelToUriMapping.containsKey(object)) {
                for (String uri : labelToUriMapping.get(object)) {
                    results.add(new Triple(uri, "http://www.w3.org/2000/01/rdf-schema#label", object));
                }
            }
            return results;
        }
    }

    protected class DummyUriKbClassifier implements UriKBClassifier {

        @Override
        public boolean isKBUri(String uri) {
            return true;
        }

        @Override
        public boolean containsKBUri(Collection<String> uris) {
            return true;
        }

    }
}
