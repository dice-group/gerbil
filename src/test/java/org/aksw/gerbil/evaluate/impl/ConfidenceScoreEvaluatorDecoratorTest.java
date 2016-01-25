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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings("deprecation")
public class ConfidenceScoreEvaluatorDecoratorTest implements Evaluator<NamedEntity>, Comparator<EvaluationResult> {

    public static final String CORRECT_MARKING = "correct";
    public static final String WRONG_MARKING = "wrong";
    public static final String EVALUTION_RESULT_NAME = "score";

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new NamedEntity[0][0], 0, null });
        testConfigs.add(new Object[] { new NamedEntity[2][0], 0, null });
        testConfigs.add(new Object[] { new NamedEntity[][] {
                { new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.1), new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.2),
                        new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.3) } },
                1.0, new Double(0.1) });
        testConfigs.add(new Object[] { new NamedEntity[][] { { new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.1) },
                { new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.2),
                        new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.3) } },
                1.0, new Double(0.1) });
        testConfigs.add(new Object[] { new NamedEntity[][] { { new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.1),
                new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.2), new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.3) } },
                1.0 / 3.0, new Double(0) });
        testConfigs.add(new Object[] {
                new NamedEntity[][] { { new ScoredNamedEntity(0, 1, CORRECT_MARKING, 0.1),
                        new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.2), new NamedEntity(0, 1, WRONG_MARKING) } },
                1.0 / 3.0, new Double(0) });
        testConfigs.add(new Object[] {
                new NamedEntity[][] { { new ScoredNamedEntity(0, 1, WRONG_MARKING, 0.1),
                        new NamedEntity(0, 1, CORRECT_MARKING), new NamedEntity(0, 1, CORRECT_MARKING) } },
                1.0, new Double(0.1) });
        return testConfigs;
    }

    private List<List<NamedEntity>> annotatorResults;
    private double expectedScore;
    private Double expectedThreshold;

    public ConfidenceScoreEvaluatorDecoratorTest(NamedEntity annotatorResults[][], double expectedScore,
            Double expectedThreshold) {
        this.annotatorResults = new ArrayList<List<NamedEntity>>(annotatorResults.length);
        for (int i = 0; i < annotatorResults.length; ++i) {
            this.annotatorResults.add(Arrays.asList(annotatorResults[i]));
        }
        this.expectedScore = expectedScore;
        this.expectedThreshold = expectedThreshold;
    }

    @Test
    public void test() {
        ConfidenceScoreEvaluatorDecorator<NamedEntity> decorator = new ConfidenceScoreEvaluatorDecorator<NamedEntity>(
                this, EVALUTION_RESULT_NAME, this);
        EvaluationResultContainer results = new EvaluationResultContainer();
        decorator.evaluate(annotatorResults, new ArrayList<List<NamedEntity>>(), results);
        boolean evalationResultFound = false;
        boolean scoreThresholdFound = false;
        for (EvaluationResult result : results.getResults()) {
            if (EVALUTION_RESULT_NAME.equals(result.getName())) {
                evalationResultFound = true;
                Assert.assertEquals(expectedScore, result.getValue());
            }
            if (ConfidenceScoreEvaluatorDecorator.CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME.equals(result.getName())) {
                scoreThresholdFound = true;
                Assert.assertEquals(expectedThreshold, result.getValue());
            }
        }
        Assert.assertTrue(evalationResultFound);
        if (expectedThreshold != null) {
            Assert.assertTrue(scoreThresholdFound);
        }
    }

    @Override
    public void evaluate(List<List<NamedEntity>> annotatorResults, List<List<NamedEntity>> goldStandard,
            EvaluationResultContainer results) {
        // all gold standards in this test are empty
        Assert.assertEquals(0, goldStandard.size());
        // simply count all correct named entities
        int score = 0, sum = 0;
        for (List<NamedEntity> nes : annotatorResults) {
            for (NamedEntity ne : nes) {
                if (ne.containsUri(CORRECT_MARKING)) {
                    ++score;
                }
                ++sum;
            }
        }
        if (sum == 0) {
            results.addResult(new DoubleEvaluationResult(EVALUTION_RESULT_NAME, 0));
        } else {
            results.addResult(new DoubleEvaluationResult(EVALUTION_RESULT_NAME, (double) score / (double) sum));
        }
    }

    @Override
    public int compare(EvaluationResult result1, EvaluationResult result2) {
        return Double.compare(((DoubleEvaluationResult) result1).getValueAsDouble(),
                ((DoubleEvaluationResult) result2).getValueAsDouble());
    }
}
