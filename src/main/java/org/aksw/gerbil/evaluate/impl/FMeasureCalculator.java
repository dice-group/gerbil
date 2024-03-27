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

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.*;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.matching.impl.QAMatchingsCounter;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.utils.AnswersLogger;
import org.aksw.gerbil.utils.AnswersLoggerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FMeasureCalculator<T extends Marking> implements Evaluator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMeasureCalculator.class);

    public static final String MACRO_F1_SCORE_NAME = "Macro F1 score";
    public static final String MACRO_PRECISION_NAME = "Macro Precision";
    public static final String MACRO_RECALL_NAME = "Macro Recall";
    public static final String MICRO_F1_SCORE_NAME = "Micro F1 score";
    public static final String MICRO_PRECISION_NAME = "Micro Precision";
    public static final String MICRO_RECALL_NAME = "Micro Recall";
    public static final String CONTINGENCY_MATRIX_NAME = "Contingency Matrix";
    public static final String MACRO_F1_2_SCORE_NAME = "Macro F1 QALD";
	private static final String PRINT_ANSWERS_TO_LOG_KEY = "org.aksw.gerbil.qa.matching.printAnswers";

    protected MatchingsCounter<T> matchingsCounter;

    private boolean printAnswers = false;
    private AnswersLogger<T> alog = null;
    
    public FMeasureCalculator(MatchingsCounter<T> matchingsCounter) {
        super();
        this.matchingsCounter = matchingsCounter;
    	this.printAnswers = Boolean.valueOf(GerbilConfiguration.getInstance().getString(PRINT_ANSWERS_TO_LOG_KEY));   	
    	if(this.printAnswers){
    		if(matchingsCounter instanceof QAMatchingsCounter) {
    			alog = new AnswersLogger<T>(matchingsCounter.getClass().getSimpleName());
    			AnswersLoggerContainer.addAnswersLogger(this.hashCode(), alog);
    		}else{
    			this.printAnswers=false;
    		}
    	}
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        EvaluationCounts counts[] = generateMatchingCounts(annotatorResults, goldStandard);
        results.addResults(calculateMicroFMeasure(counts));
        results.addResults(calculateMacroFMeasure(counts));
        results.addResults(getContingencyMatrix(counts));
    
        if(printAnswers){
        	try{
        		alog.close();
        	}catch(Exception e){
        		LOGGER.warn("Could not close answer logger");
        	}
        }
    }

    protected EvaluationCounts[] generateMatchingCounts(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        EvaluationCounts counts[] = new EvaluationCounts[annotatorResults.size()];
        for (int i = 0; i < counts.length; ++i) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("doc " + i + "|||||||||");
            }
            counts[i] = matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
            
            //Debug Answers
            if(printAnswers){
            	try{
            		double[] measure = calculateMeasures(counts[i]);
            		alog.printLine(i, annotatorResults.get(i), goldStandard.get(i), counts[i], measure);
            	}catch(Exception e){
            		LOGGER.warn("Will not print answer log for question "+i);
            	}
            }
        }
        return counts;
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[]) {
        return calculateMicroFMeasure(counts, MICRO_PRECISION_NAME, MICRO_RECALL_NAME, MICRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName) {
        EvaluationCounts sums = new EvaluationCounts();
        for (int i = 0; i < counts.length; ++i) {
            sums.add(counts[i]);
        }
        double measures[] = calculateMeasures(sums);
        if(printAnswers){
        	try{
        		alog.printMicro(sums, measures);
        	}catch(Exception e){
        		LOGGER.warn("Will not print answer log for micro ");
        	}
        }
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, measures[0]),
                new DoubleEvaluationResult(recallName, measures[1]),
                new DoubleEvaluationResult(f1ScoreName, measures[2]) };
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[]) {
        return calculateMacroFMeasure(counts, MACRO_PRECISION_NAME, MACRO_RECALL_NAME, MACRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName) {
        double avgs[] = new double[3];
        double measures[];
        for (int i = 0; i < counts.length; ++i) {
            measures = calculateMeasures(counts[i]);
            avgs[0] += measures[0];
            avgs[1] += measures[1];
            avgs[2] += measures[2];
        }
        avgs[0] /= counts.length;
        avgs[1] /= counts.length;
        avgs[2] /= counts.length;

        double F1_scoreDef = calculateF1QALD(counts);
        
        if(printAnswers){
        	try{
        		alog.printMacro(avgs);
        	}catch(Exception e){
        		LOGGER.warn("Will not print answer log for micro ");
        	}
        }
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, avgs[0]),
                new DoubleEvaluationResult(recallName, avgs[1]), new DoubleEvaluationResult(f1ScoreName, avgs[2]), new DoubleEvaluationResult(MACRO_F1_2_SCORE_NAME, F1_scoreDef) };
    }
    
    private double calculateF1QALD(EvaluationCounts counts[]) {
    	 double avgs[] = new double[3];
         double measures[];
    	 for (int i = 0; i < counts.length; ++i) {
             measures = calculateMeasuresQALD(counts[i]);
             avgs[0] += measures[0];
             avgs[1] += measures[1];
             avgs[2] += measures[2];
         }
    	 avgs[0] /= counts.length;
         avgs[1] /= counts.length;
         avgs[2] /= counts.length;
         return (2 * avgs[0] * avgs[1]) / (avgs[0] + avgs[1]);
         
    }

    private double[] calculateMeasuresQALD(EvaluationCounts counts) {
        double precision, recall, F1_score;
        if (counts.truePositives == 0) {
            if ((counts.falsePositives == 0) && (counts.falseNegatives == 0)) {
                // If there haven't been something to find and nothing has been
                // found --> everything is great
                precision = 1.0;
                recall = 1.0;
                F1_score = 1.0;
            } 
            else if(counts.falsePositives == 0) {
            	//Annotator has no answer 
            	 precision = 1.0;
                 recall = 0.0;
                 F1_score = 0.0;
            }
            else {
                // The annotator found no correct ones, but made some mistake
                // --> that is bad
                precision = 0.0;
                recall = 0.0;
                F1_score = 0.0;
            }
        } else {
            precision = (double) counts.truePositives / (double) (counts.truePositives + counts.falsePositives);
            recall = (double) counts.truePositives / (double) (counts.truePositives + counts.falseNegatives);
            F1_score = (2 * precision * recall) / (precision + recall);
        }
        return new double[] { precision, recall, F1_score };
    }
    
    private double[] calculateMeasures(EvaluationCounts counts) {
        double precision, recall, F1_score;
        if (counts.truePositives == 0) {
            if ((counts.falsePositives == 0) && (counts.falseNegatives == 0)) {
                // If there haven't been something to find and nothing has been
                // found --> everything is great
                precision = 1.0;
                recall = 1.0;
                F1_score = 1.0;
            } else {
                // The annotator found no correct ones, but made some mistake
                // --> that is bad
                precision = 0.0;
                recall = 0.0;
                F1_score = 0.0;
            }
        } else {
            precision = (double) counts.truePositives / (double) (counts.truePositives + counts.falsePositives);
            recall = (double) counts.truePositives / (double) (counts.truePositives + counts.falseNegatives);
            F1_score = (2 * precision * recall) / (precision + recall);
        }
        return new double[] { precision, recall, F1_score };
    }

    private EvaluationResult[] getContingencyMatrix(EvaluationCounts counts[]){
        return new EvaluationResult[] { new ObjectEvaluationResult(CONTINGENCY_MATRIX_NAME, counts)};
    }
}
