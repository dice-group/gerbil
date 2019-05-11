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
package org.aksw.gerbil.annotator.decorator;

import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple decorator for an annotator which measures the time needed
 * for annotations. This task is handled by this annotator decorator due to an
 * easier adapter implementation and time measuring problems if an error occur
 * inside the adapter.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class TimeMeasuringAnnotatorDecorator extends AbstractAnnotatorDecorator
        implements Evaluator<Model>, TimeMeasurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeMeasuringAnnotatorDecorator.class);

    public static final String AVG_TIME_RESULT_NAME = "avg millis/doc";

    public static TimeMeasuringAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator) {
        switch (type) {
        //case T1/T2
        case SWC2018T1:
        case SWC1:
            return new TimeMeasuringSWC1Annotator((SWCTask1System) annotator);
        case SWC_2019:
        case SWC2:
        	return new TimeMeasuringSWC2Annotator((SWCTask2System) annotator);
        default:
            break;
        }
        LOGGER.error("Couldn't generate a TimeMeasuringAnnotatorDecorator for the given annotator. Returning null.");
        return null;
    }


	@Override
	public File2SystemEntry getFileMapping() {
		return decoratedAnnotator.getFileMapping();
	}


	@Override
	public void setFileMapping(File2SystemEntry entry) {
		decoratedAnnotator.setFileMapping(entry);
	}
    
    private static class TimeMeasuringSWC1Annotator extends TimeMeasuringAnnotatorDecorator implements SWCTask1System {

        protected TimeMeasuringSWC1Annotator(SWCTask1System decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Model> performTask1(Model model) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performSWC1Task(this, model);
        }
    }

    private static class TimeMeasuringSWC2Annotator extends TimeMeasuringAnnotatorDecorator implements SWCTask2System {

        protected TimeMeasuringSWC2Annotator(SWCTask2System decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Model> performTask2(Model model) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performSWC2Task(this, model);
        }
    }

    
    protected static List<Model> performSWC1Task(TimeMeasuringAnnotatorDecorator timeMeasurer, Model model)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<Model> result = null;
        result = ((SWCTask1System) timeMeasurer.getDecoratedAnnotator()).performTask1(model);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }


    protected static List<Model> performSWC2Task(TimeMeasuringAnnotatorDecorator timeMeasurer, Model model)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<Model> result = null;
        result = ((SWCTask2System) timeMeasurer.getDecoratedAnnotator()).performTask2(model);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    
    protected long timeSum = 0;
    protected int callCount = 0;

    protected TimeMeasuringAnnotatorDecorator(Annotator decoratedAnnotator) {
        super(decoratedAnnotator);
    }

    protected void addCallRuntime(long runtime) {
        timeSum += runtime;
        ++callCount;
    }

    @Override
    public double getAverageRuntime() {
        if (callCount > 0) {
            return (double) timeSum / (double) callCount;
        } else {
            return Double.NaN;
        }
    }

    @Override
    public void reset() {
        timeSum = 0;
        callCount = 0;
    }

    @Override
    public void evaluate(List<List<Model>> annotatorResults, List<List<Model>> goldStandard,
            EvaluationResultContainer results) {
        if (callCount > 0) {
            results.addResult(new DoubleEvaluationResult(AVG_TIME_RESULT_NAME, getAverageRuntime()));
        }
    }
}
