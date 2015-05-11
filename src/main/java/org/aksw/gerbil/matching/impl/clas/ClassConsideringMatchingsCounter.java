package org.aksw.gerbil.matching.impl.clas;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.BitSet;

public class ClassConsideringMatchingsCounter<T extends Marking> extends MatchingsCounterImpl<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassConsideringMatchingsCounter.class);

    protected MarkingClassifier<T> classifier;
    @SuppressWarnings("rawtypes")
    protected List classBasedCounts[];

    public ClassConsideringMatchingsCounter(MatchingsSearcher<T> searcher, MarkingClassifier<T> classifier) {
        super(searcher);
        classifier.getNumberOfClasses();
        classBasedCounts = new List[classifier.getNumberOfClasses()];
        for (int i = 0; i < classBasedCounts.length; ++i) {
            classBasedCounts[i] = new ArrayList<int[]>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        int documentCounts[] = new int[3];
        int classBasedDocCounts[][] = new int[classBasedCounts.length][3];
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        int classId;
        for (T expectedElement : goldStandard) {
            matchingElements = searcher.findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
            classId = classifier.getClass(expectedElement);
            if (!matchingElements.isEmpty()) {
                ++documentCounts[TRUE_POSITIVE_COUNT_ID];
                ++classBasedDocCounts[classId][TRUE_POSITIVE_COUNT_ID];
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
                LOGGER.debug("Found a true positive (" + expectedElement + ").");
            } else {
                ++documentCounts[FALSE_NEGATIVE_COUNT_ID];
                ++classBasedDocCounts[classId][FALSE_NEGATIVE_COUNT_ID];
                LOGGER.debug("Found a false negative (" + expectedElement + ").");
            }
        }
        // The remaining elements are false positives
        documentCounts[FALSE_POSITIVE_COUNT_ID] = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                classId = classifier.getClass(annotatorResult.get(i));
                ++classBasedDocCounts[classId][FALSE_POSITIVE_COUNT_ID];
            }
        }
        LOGGER.debug("Found " + documentCounts[FALSE_POSITIVE_COUNT_ID] + " false positives.");

        counts.add(documentCounts);
        for (int i = 0; i < classBasedCounts.length; ++i) {
            classBasedCounts[i].add(classBasedDocCounts[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public List<int[]> getCounts(int classId) {
        return classBasedCounts[classId];
    }
}
