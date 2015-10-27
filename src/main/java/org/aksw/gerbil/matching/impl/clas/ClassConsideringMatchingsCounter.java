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
package org.aksw.gerbil.matching.impl.clas;

import java.util.List;

import org.aksw.gerbil.matching.ClassifiedEvaluationCounts;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.BitSet;

public class ClassConsideringMatchingsCounter<T extends Marking> extends MatchingsCounterImpl<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassConsideringMatchingsCounter.class);

    protected MarkingClassifier<T> classifier;

    public ClassConsideringMatchingsCounter(MatchingsSearcher<T> searcher, MarkingClassifier<T> classifier) {
        super(searcher);
        this.classifier = classifier;
    }

    @Override
    public EvaluationCounts countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        ClassifiedEvaluationCounts documentCounts = new ClassifiedEvaluationCounts(classifier.getNumberOfClasses());
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        int classId;
        for (T expectedElement : goldStandard) {
            matchingElements = searcher.findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
            classId = classifier.getClass(expectedElement);
            if (!matchingElements.isEmpty()) {
                ++documentCounts.truePositives;
                ++documentCounts.classifiedCounts[classId].truePositives;
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
                LOGGER.debug("Found a true positive (" + expectedElement + ").");
            } else {
                ++documentCounts.falseNegatives;
                ++documentCounts.classifiedCounts[classId].falseNegatives;
                LOGGER.debug("Found a false negative (" + expectedElement + ").");
            }
        }
        // The remaining elements are false positives
        documentCounts.falsePositives = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                classId = classifier.getClass(annotatorResult.get(i));
                ++documentCounts.classifiedCounts[classId].falsePositives;
            }
        }
        LOGGER.debug("Found " + documentCounts.falsePositives + " false positives.");
        return documentCounts;
    }

}
