package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    public HierarchicalMatchingsCounter(MatchingsSearcher<T> matchingsSearcher,
            UriKBClassifier uriKBClassifier, SubClassInferencer inferencer) {
        this.matchingsSearcher = matchingsSearcher;
        this.uriKBClassifier = uriKBClassifier;
        this.inferencer = inferencer;
    }

    public void countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        int documentCounts[];
        List<int[]> localCounts = new ArrayList<int[]>();
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
            matchingElements = matchingsSearcher.findMatchings(expectedElement, annotatorResult,
                    alreadyUsedResults);
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

                // If the annotator did not return a type of a known KB and the
                // gold standard did not contained a type of a known KB
                if ((documentCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] == 0)
                        && (documentCounts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID] == 0)
                        && (documentCounts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID] == 0)) {
                    documentCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] = 1;
                    LOGGER.info("Got an entity with a type that is not inside a known KB in the annotator and in the dataset.");
                }
            } else {
                documentCounts = new int[3];
                documentCounts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID] = 1;
                documentCounts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID] = 1;
            }
            localCounts.add(documentCounts);
        }
        counts.add(localCounts);
    }

    private int[] countMatchings(ClassSet classes) {
        int documentCounts[] = new int[3];
        Iterator<ClassNode> iterator = classes.iterator();
        ClassifiedClassNode node;
        while (iterator.hasNext()) {
            // At this point, every ClassNode should be a ClassifiedClassNode
            node = (ClassifiedClassNode) iterator.next();
            if (uriKBClassifier.containsKBUri(node.getUris())) {
                if (node.getClassIds().contains(EXPECTED_CLASSES_CLASS_ID)) {
                    if (node.getClassIds().contains(ANNOTATOR_CLASSES_CLASS_ID)) {
                        ++documentCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID];
                    } else {
                        ++documentCounts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID];
                    }
                } else if (node.getClassIds().contains(ANNOTATOR_CLASSES_CLASS_ID)) {
                    ++documentCounts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID];
                }
            }
        }
        return documentCounts;
    }

    public List<List<int[]>> getCounts() {
        return counts;
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
