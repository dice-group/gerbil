package org.aksw.gerbil.annotator.impl.qa;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.matching.impl.EqualsBasedMatchingsSearcher;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.junit.Assert;
import org.junit.Test;


public class QanaryWebServiceFmeasureTest {

    @Test
    public void test() {
		FMeasureCalculator<Annotation> calculator=new FMeasureCalculator<>(new MatchingsCounterImpl<>(new EqualsBasedMatchingsSearcher<>()));
    	String question1=" What is the time zone of Salt Lake City?";
    	String question2="What were the names of the three ships by Columbus?";
    	String question3="What were the original 13 British colonies?";
    	double[] expectedResults = {0.0909, 0.2, 0.0588,0.3333,0.3333,0.3333}; 
    	List<List<Annotation>> annotatorResponse = new ArrayList<List<Annotation>>();
    	List<Annotation> firstResponse = new ArrayList<>();
    	firstResponse.add(new Annotation("http://dbpedia.org/resource/Mountain_Time_Zone"));
    	
    	
    	
    	List<Annotation> secondResponse =  new ArrayList<>();
    	secondResponse.add(new Annotation("http://dbpedia.org/resource/Bruce_Peddie"));
    	
    	
    	
    	
    	List<Annotation> thirdResponse = new ArrayList<>();
    	thirdResponse.add(new Annotation("http://dbpedia.org/resource/British_Bull_Dog_revolver"));
    	thirdResponse.add(new Annotation("http://dbpedia.org/resource/Enfield_revolver"));
    	thirdResponse.add(new Annotation("http://dbpedia.org/resource/Webley_Revolver"));
    	
    	
    	annotatorResponse.add(firstResponse);
    	annotatorResponse.add(secondResponse);
    	annotatorResponse.add(thirdResponse);
    

    	List<List<Annotation>> goldStandard = new ArrayList<List<Annotation>>();
    	
    	List<Annotation> firstGoldenResponse =new ArrayList<>();
    	firstGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Mountain_Time_Zone"));
    	
    	
    	List<Annotation> secondGoldenResponse = new ArrayList<>();
    	secondGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Santa_María_(ship)"));
    	secondGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Pinta_(ship)"));
    	secondGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Niña"));
    	
    	
    	
    	List<Annotation>  thirdGoldenResponse =  new ArrayList<>();
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Province_of_South_Carolina"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Chesapeake_Colonies"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/New_England_Colonies"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Province_of_Georgia"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Colony_of_Virginia"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Middle_Colonies"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Province_of_New_Hampshire"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Province_of_Pennsylvania"));
    	thirdGoldenResponse.add(new Annotation("http://dbpedia.org/resource/Southern_Colonies"));
    	thirdGoldenResponse.add(new Annotation( "http://dbpedia.org/resource/Province_of_Massachusetts_Bay"));
    	thirdGoldenResponse.add(new Annotation( "http://dbpedia.org/resource/Connecticut_Colony"));
    	thirdGoldenResponse.add(new Annotation( "http://dbpedia.org/resource/Delaware_Colony"));
    	thirdGoldenResponse.add(new Annotation( "http://dbpedia.org/resource/Province_of_New_Jersey"));
    	
    	goldStandard.add(firstGoldenResponse);
    	goldStandard.add(secondGoldenResponse);
    	goldStandard.add(thirdGoldenResponse);
    	
    	
        EvaluationResultContainer results = new EvaluationResultContainer();
        calculator.evaluate(null, annotatorResponse, goldStandard,results);
        List<EvaluationResult> singleResults = results.getResults();
        Assert.assertEquals(expectedResults.length, singleResults.size());
        double calculatedResult[] = new double[6];
        for (EvaluationResult result : singleResults) {
            switch (result.getName()) {
            case FMeasureCalculator.MACRO_F1_SCORE_NAME: {
                calculatedResult[3] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_PRECISION_NAME: {
                calculatedResult[4] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_RECALL_NAME: {
                calculatedResult[5] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_F1_SCORE_NAME: {
                calculatedResult[0] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_PRECISION_NAME: {
                calculatedResult[1] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_RECALL_NAME: {
                calculatedResult[2] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            default: {
                throw new IllegalStateException("Got an unexpected result: " + result.getName());
            }
            }
        }
        Assert.assertArrayEquals("Arrays do not equal exp=" + Arrays.toString(expectedResults) + " calculated="
                + Arrays.toString(calculatedResult), expectedResults, calculatedResult, 0.000000001);
    }

}
