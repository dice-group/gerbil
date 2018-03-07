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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.subclass.ClassNode;
import org.aksw.gerbil.semantic.subclass.ClassSet;
import org.aksw.gerbil.semantic.subclass.ClassifiedClassNode;
import org.aksw.gerbil.semantic.subclass.ClassifyingClassNodeFactory;
import org.aksw.gerbil.semantic.subclass.SimpleClassSet;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.BitSet;

public class HierarchicalMatchingsCounter<T extends TypedMarking> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalMatchingsCounter.class);

    private static final int EXPECTED_CLASSES_CLASS_ID = 0;
    private static final int ANNOTATOR_CLASSES_CLASS_ID = 1;

    /**
     * This matchings counter needs a {@link MatchingsSearcher} that can create
     * pairs of named entities for which the types should be matched to each
     * other.
     */
    protected MatchingsSearcher<T> matchingsSearcher;
    protected List<List<int[]>> counts = new ArrayList<List<int[]>>();
    protected SubClassInferencer inferencer;
    private UriKBClassifier uriKBClassifier;

    public HierarchicalMatchingsCounter(MatchingsSearcher<T> matchingsSearcher, UriKBClassifier uriKBClassifier,
            SubClassInferencer inferencer) {
        this.matchingsSearcher = matchingsSearcher;
        this.uriKBClassifier = uriKBClassifier;
        this.inferencer = inferencer;
    }

    public List<EvaluationCounts> countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        EvaluationCounts documentCounts;
        List<EvaluationCounts> localCounts = new ArrayList<EvaluationCounts>();
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        T matchedResult;
        int matchedResultId;
        ClassSet classes;
        ClassifyingClassNodeFactory expectedClassesFactory = new ClassifyingClassNodeFactory(EXPECTED_CLASSES_CLASS_ID);
        ClassifyingClassNodeFactory annotatorClassesFactory = new ClassifyingClassNodeFactory(
                ANNOTATOR_CLASSES_CLASS_ID);
        Set<String> types;
        for (T expectedElement : goldStandard) {
            matchingElements = matchingsSearcher.findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                // We use the first matching as solution for the typing task
                matchedResultId = matchingElements.nextSetBit(0);
                matchedResult = annotatorResult.get(matchedResultId);
                alreadyUsedResults.set(matchedResultId);

                // Derive the classes and sub classes for the types given by the
                // dataset
                classes = new SimpleClassSet();
                types = expectedElement.getTypes();
                for (String typeURI : types) {
                    inferencer.inferSubClasses(typeURI, classes, expectedClassesFactory);
                }
                // Derive the classes and sub classes for the types returned by
                // the annotator
                types = matchedResult.getTypes();
                for (String typeURI : types) {
                    inferencer.inferSubClasses(typeURI, classes, annotatorClassesFactory);
                }
                // Count the matchings
                documentCounts = countMatchings(classes);
                LOGGER.debug("Type matching found {} (classes={}).", documentCounts, classes);

                // If the annotator did not return a type of a known KB and the
                // gold standard did not contain a type of a known KB
                if ((documentCounts.truePositives == 0) && (documentCounts.falseNegatives == 0)
                        && (documentCounts.falsePositives == 0)) {
                    documentCounts.truePositives = 1;
                    LOGGER.info("Got an entity with a type that is not inside a known KB in the annotator and in the dataset.");
                }
            } else {
                documentCounts = new EvaluationCounts();
                documentCounts.falseNegatives = 1;
                documentCounts.falsePositives = 0;
            }
            localCounts.add(documentCounts);
        }
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if(!alreadyUsedResults.get(i)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("found a false positive. {}", annotatorResult.get(i));
                }
                localCounts.add(new EvaluationCounts(0, 1, 0));
            }
        }
        return localCounts;
    }

    private EvaluationCounts countMatchings(ClassSet classes) {
        EvaluationCounts documentCounts = new EvaluationCounts();
        Iterator<ClassNode> iterator = classes.iterator();
        ClassifiedClassNode node;
        while (iterator.hasNext()) {
            // At this point, every ClassNode should be a ClassifiedClassNode
            node = (ClassifiedClassNode) iterator.next();
            if (uriKBClassifier.containsKBUri(node.getUris())) {
                if (node.getClassIds().contains(EXPECTED_CLASSES_CLASS_ID)) {
                    if (node.getClassIds().contains(ANNOTATOR_CLASSES_CLASS_ID)) {
                        ++documentCounts.truePositives;
                    } else {
                        ++documentCounts.falseNegatives;
                    }
                } else if (node.getClassIds().contains(ANNOTATOR_CLASSES_CLASS_ID)) {
                    ++documentCounts.falsePositives;
                }
            }
        }
        return documentCounts;
    }

    public static int getIntersectionSize(Set<String> set1, Set<String> set2) {
        Set<String> smallSet, largeSet;
        if (set1.size() > set2.size()) {
            smallSet = set2;
            largeSet = set1;
        } else {
            smallSet = set1;
            largeSet = set2;
        }
        int count = 0;
        for (String e : smallSet) {
            if (largeSet.contains(e)) {
                ++count;
            }
        }
        return count;
    }
}
