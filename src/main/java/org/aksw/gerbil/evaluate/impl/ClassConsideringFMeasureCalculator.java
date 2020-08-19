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

import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.matching.scored.ScoredEvaluationCountsArray;
import org.aksw.gerbil.matching.scored.ScoredMatchingsCounterImpl;
import org.aksw.gerbil.utils.filter.MarkingClassBasedMarkingFilter;
import org.aksw.gerbil.utils.filter.MarkingFilter;

public class ClassConsideringFMeasureCalculator<T extends ClassifiedMeaning> extends ConfidenceBasedFMeasureCalculator<T> {

    @Deprecated
    public static final String MACRO_ACCURACY_NAME = "Macro Accuracy";
    @Deprecated
    public static final String MICRO_ACCURACY_NAME = "Micro Accuracy";

    public static final String MACRO_F1_SCORE_NAME_APPENDIX = " Macro F1 score";
    public static final String MACRO_PRECISION_NAME_APPENDIX = " Macro Precision";
    public static final String MACRO_RECALL_NAME_APPENDIX = " Macro Recall";
    public static final String MICRO_F1_SCORE_NAME_APPENDIX = " Micro F1 score";
    public static final String MICRO_PRECISION_NAME_APPENDIX = " Micro Precision";
    public static final String MICRO_RECALL_NAME_APPENDIX = " Micro Recall";

    protected MarkingClasses markingClasses[];
    protected MarkingFilter<T> markingFilters[];

    @SuppressWarnings("unchecked")
    public ClassConsideringFMeasureCalculator(MatchingsCounter<T> matchingsCounter, MarkingClasses... markingClasses) {
        super(null);
        this.markingClasses = markingClasses;
        this.markingFilters = new MarkingFilter[markingClasses.length];
        for (int i = 0; i < markingClasses.length; ++i) {
            this.markingFilters[i] = new MarkingClassBasedMarkingFilter<T>(markingClasses[i]);
        }
        this.matchingsCounter = new ScoredMatchingsCounterImpl<T>(matchingsCounter);
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results, String language) {
        // the super class performs the matching counter calls
        ScoredEvaluationCountsArray counts = generateMatchingCounts(annotatorResults, goldStandard);
        double threshold = calculateMicroFMeasure(counts, results);
        calculateMacroFMeasure(counts, results, threshold);

        // calculate measures for the different classes
        String classLabel;
        for (int i = 0; i < markingClasses.length; ++i) {
            counts = generateMatchingCounts(markingFilters[i].filterListOfLists(annotatorResults),
                    markingFilters[i].filterListOfLists(goldStandard));
            if ((counts.truePositiveSums[0] + counts.falseNegativeSums[0] + counts.falsePositiveSums[0]) > 0) {
                classLabel = markingClasses[i].getLabel();
                calculateMicroFMeasure(counts, classLabel + MICRO_PRECISION_NAME_APPENDIX,
                        classLabel + MICRO_RECALL_NAME_APPENDIX, classLabel + MICRO_F1_SCORE_NAME_APPENDIX, threshold,
                        results);
                calculateMacroFMeasure(counts, classLabel + MACRO_PRECISION_NAME_APPENDIX,
                        classLabel + MACRO_RECALL_NAME_APPENDIX, classLabel + MACRO_F1_SCORE_NAME_APPENDIX, threshold,
                        results);
            }
        }

    }
}
