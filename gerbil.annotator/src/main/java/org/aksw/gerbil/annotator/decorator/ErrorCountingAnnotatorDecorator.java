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

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.IntEvaluationResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple decorator for an annotator which handles exceptions thrown
 * by the decorated annotator. It logs these exceptions and counts the errors.
 * This behavior makes it possible, that the BAT-Framework doesn't quit the
 * experiment even if an exception is thrown.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class ErrorCountingAnnotatorDecorator extends AbstractAnnotatorDecorator
        implements Evaluator<Model>, ErrorCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCountingAnnotatorDecorator.class);

    private static final double AMOUNT_OF_TOLERATED_ERRORS = 0.25;

    public static final String ERROR_COUNT_RESULT_NAME = "error count";

    private static boolean printDebugMsg = true;

    public static ErrorCountingAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator,
            int numberOfExpectedCalls) {
        int maxErrors = (int) Math.ceil(AMOUNT_OF_TOLERATED_ERRORS * numberOfExpectedCalls);
        switch (type) {
        //T1/T2
        case SWC2018T1:
        case SWC1:
            return new ErrorCountingSWCTask1System((SWCTask1System) annotator, maxErrors);
        case SWC_2019:
        case SWC2:
            return new ErrorCountingSWCTask2System((SWCTask2System) annotator, maxErrors);
        default:
            break;

        }
        LOGGER.error("Couldn't generate a ErrorCountingAnnotatorDecorator for the given annotator. Returning null.");
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


    private static class ErrorCountingSWCTask2System extends ErrorCountingAnnotatorDecorator
    implements SWCTask2System {

    	protected ErrorCountingSWCTask2System(SWCTask2System decoratedAnnotator, int maxErrors) {
    		super(decoratedAnnotator, maxErrors);
    	}


    	@Override
    	public List<Model> performTask2(Model model) throws GerbilException {
    		return ErrorCountingAnnotatorDecorator.performSWCTask2(this, model);
    	}



    }

    
    private static class ErrorCountingSWCTask1System extends ErrorCountingAnnotatorDecorator
            implements SWCTask1System {

        protected ErrorCountingSWCTask1System(SWCTask1System decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }


		@Override
		public List<Model> performTask1(Model model) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performSWCTask1(this, model);
		}
    }


    protected static void logResult(List<? extends Model> result, String annotatorName, String markingName) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(annotatorName);
        builder.append("] result=[");
        boolean first = true;
        for (Model m : result) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(markingName);
            builder.append(m.toString());
        }
        builder.append(']');
        LOGGER.debug(builder.toString());
    }

    protected static List<Model> performSWCTask1(ErrorCountingAnnotatorDecorator errorCounter, Model model)
            throws GerbilException {
        List<Model> result = null;
        try {
            result = ((SWCTask1System) errorCounter.getDecoratedAnnotator()).performTask1(model);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator (" + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator (" + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new ArrayList<Model>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "");
        }
        return result;
    }

    protected static List<Model> performSWCTask2(ErrorCountingAnnotatorDecorator errorCounter, Model model)
            throws GerbilException {
        List<Model> result = null;
        try {
            result = ((SWCTask2System) errorCounter.getDecoratedAnnotator()).performTask2(model);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator (" + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator (" + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new ArrayList<Model>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "");
        }
        return result;
    }

    
    protected int errorCount = 0;
    protected int maxErrors;

    protected ErrorCountingAnnotatorDecorator(Annotator decoratedAnnotator, int maxErrors) {
        super(decoratedAnnotator);
        this.maxErrors = maxErrors;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    protected void increaseErrorCount() throws GerbilException {
        ++errorCount;
        if (errorCount > maxErrors) {
            throw new GerbilException("Saw to many errors (maximum was set to " + maxErrors + ").",
                    ErrorTypes.TOO_MANY_SINGLE_ERRORS);
        }
    }

    @Override
    public void evaluate(List<List<Model>> annotatorResults, List<List<Model>> goldStandard,
            EvaluationResultContainer results) {
        results.addResult(new IntEvaluationResult(ERROR_COUNT_RESULT_NAME, errorCount));
    }

    public static synchronized void setPrintDebugMsg(boolean flag) {
        printDebugMsg = flag;
    }
}
