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
package org.aksw.gerbil.matching.impl.clas;

import java.util.List;

import org.aksw.gerbil.matching.ClassifiedEvaluationCounts;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
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
                // LOGGER.debug("Found a true positive ({}).", expectedElement);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("1.2.1|" + getUri((NamedEntity) expectedElement) + "|"
                            + getUri((NamedEntity) expectedElement) + "|tp");
                }
            } else {
                ++documentCounts.falseNegatives;
                ++documentCounts.classifiedCounts[classId].falseNegatives;
                // LOGGER.debug("Found a false negative ({}).",
                // expectedElement);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("1.2.1|" + getUri((NamedEntity) expectedElement) + "||||fn");
                }
            }
        }
        // The remaining elements are false positives
        if (LOGGER.isDebugEnabled()) {
            int id = 0;
            for (T element : annotatorResult) {
                if (!alreadyUsedResults.get(id)) {
                    LOGGER.debug("1.2.1||||" + getUri((NamedEntity) element) + "|fp");
                }
                ++id;
            }
        }
        documentCounts.falsePositives = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                classId = classifier.getClass(annotatorResult.get(i));
                ++documentCounts.classifiedCounts[classId].falsePositives;
            }
        }
        // LOGGER.debug("Found {} false positives.",
        // documentCounts.falsePositives);
        return documentCounts;
    }

    protected String getUri(NamedEntity ne) {
        if (ne.getUris().size() == 0) {
            return ne.getStartPosition() + "|" + ne.getLength() + "|null";
        }
        for (String uri : ne.getUris()) {
            if (uri.startsWith("http://dbpedia.org")) {
                return ne.getStartPosition() + "|" + ne.getLength() + "|" + uri;
            }
        }
        return ne.getStartPosition() + "|" + ne.getLength() + "|" + ne.getUris().iterator().next();
    }

}
