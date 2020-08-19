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
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKE2018Task4Annotator;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.REAnnotator;
import org.aksw.gerbil.annotator.RT2KBAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.IntEvaluationResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
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
public abstract class ErrorCountingAnnotatorDecorator extends AbstractAnnotatorDecorator implements Evaluator<Marking>,
        ErrorCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCountingAnnotatorDecorator.class);

    private static final double AMOUNT_OF_TOLERATED_ERRORS = 0.25;

    public static final String ERROR_COUNT_RESULT_NAME = "Error Count";

    private static boolean printDebugMsg = true;

    @SuppressWarnings("deprecation")
    public static ErrorCountingAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator,
            int numberOfExpectedCalls) {
        int maxErrors = (int) Math.ceil(AMOUNT_OF_TOLERATED_ERRORS * numberOfExpectedCalls);
        switch (type) {

        }
        // if (annotator instanceof Sc2WSystem) {
        // return new ErrorCountingSc2W((Sc2WSystem) annotator, maxErrors);
        // }
        // if (annotator instanceof C2WSystem) {
        // return new ErrorCountingC2W((C2WSystem) annotator, maxErrors);
        // }
        return null;
    }

    private static class ErrorCountingC2KBAnnotator extends ErrorCountingAnnotatorDecorator implements C2KBAnnotator {

        public ErrorCountingC2KBAnnotator(C2KBAnnotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performC2KB(this, document);
        }

    }

    private static class ErrorCountingD2KBAnnotator extends ErrorCountingAnnotatorDecorator implements D2KBAnnotator {

        public ErrorCountingD2KBAnnotator(D2KBAnnotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performD2KBTask(this, document);
        }
    }

    private static class ErrorCountingEntityRecognizer extends ErrorCountingAnnotatorDecorator implements
            EntityRecognizer {

        public ErrorCountingEntityRecognizer(EntityRecognizer decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performRecognition(this, document);
        }
    }

    private static class ErrorCountingOKE2018Task4Annotator extends ErrorCountingAnnotatorDecorator implements OKE2018Task4Annotator {

		protected ErrorCountingOKE2018Task4Annotator(Annotator decoratedAnnotator, int maxErrors) {
			super(decoratedAnnotator, maxErrors);
		}

		@Override
		public List<Relation> performRETask(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performRE(this, document);
		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performRecognition(this, document);

		}

		@Override
		public List<Marking> performOKE2018Task4(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performOKE2018Task4(this, document);

		}

		@Override
		public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performExtraction(this, document);
		}

		@Override
		public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performD2KBTask(this, document);
		}

		@Override
		public List<Meaning> performC2KB(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performC2KB(this, document);
		}

    }
    
    private static class ErrorCountingREAnnotator extends ErrorCountingAnnotatorDecorator implements REAnnotator {

		protected ErrorCountingREAnnotator(Annotator decoratedAnnotator, int maxErrors) {
			super(decoratedAnnotator, maxErrors);
		}

		@Override
		public List<Relation> performRETask(Document document) throws GerbilException {
			return ErrorCountingAnnotatorDecorator.performRE(this, document);
		}

    }

    private static class ErrorCountingA2KBAnnotator extends ErrorCountingD2KBAnnotator implements A2KBAnnotator {

        public ErrorCountingA2KBAnnotator(A2KBAnnotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performC2KB(this, document);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performRecognition(this, document);
        }

        @Override
        public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performExtraction(this, document);
        }

    }

    private static class ErrorCountingEntityTyper extends ErrorCountingAnnotatorDecorator implements EntityTyper {

        protected ErrorCountingEntityTyper(EntityTyper decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performTyping(this, document);
        }
    }

    private static class ErrorCountingRT2KBAnnotator extends ErrorCountingEntityRecognizer implements RT2KBAnnotator {

        protected ErrorCountingRT2KBAnnotator(RT2KBAnnotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performTyping(this, document);
        }

        @Override
        public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performRT2KBTask(this, document);
        }

    }

    private static class ErrorCountingOKETask1Annotator extends ErrorCountingA2KBAnnotator implements OKETask1Annotator {

        protected ErrorCountingOKETask1Annotator(OKETask1Annotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performTyping(this, document);
        }

        @Override
        public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performRT2KBTask(this, document);
        }

        @Override
        public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performOKETask1(this, document);
        }
    }

    private static class ErrorCountingOKETask2Annotator extends ErrorCountingAnnotatorDecorator implements
            OKETask2Annotator {

        protected ErrorCountingOKETask2Annotator(OKETask2Annotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performOKETask2(this, document);
        }

    }

    protected static void logResult(List<? extends Marking> result, String annotatorName, String markingName) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(annotatorName);
        builder.append("] result=[");
        boolean first = true;
        for (Marking m : result) {
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

    public static List<Relation> performRE(ErrorCountingAnnotatorDecorator errorCounter,
			Document document) throws GerbilException {
    	List<Relation> result = null;
        try {
            result = ((REAnnotator) errorCounter.getDecoratedAnnotator()).performRETask(document);
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
            return new ArrayList<Relation>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "Relation");
        }
        return result;
	}

    public static List<Marking> performOKE2018Task4(ErrorCountingAnnotatorDecorator errorCounter,
			Document document) throws GerbilException {
    	List<Marking> result = null;
        try {
            result = ((OKE2018Task4Annotator) errorCounter.getDecoratedAnnotator()).performOKE2018Task4(document);
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
            return new ArrayList<Marking>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "Meaning");
        }
        return result;
	}
    
	protected static List<Meaning> performC2KB(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<Meaning> result = null;
        try {
            result = ((C2KBAnnotator) errorCounter.getDecoratedAnnotator()).performC2KB(document);
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
            return new ArrayList<Meaning>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "Meaning");
        }
        return result;
    }

    protected static List<MeaningSpan> performD2KBTask(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<MeaningSpan> result = null;
        try {
            result = ((D2KBAnnotator) errorCounter.getDecoratedAnnotator()).performD2KBTask(document);
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
            return new ArrayList<MeaningSpan>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "MeaningSpan");
        }
        return result;
    }

    protected static List<MeaningSpan> performExtraction(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<MeaningSpan> result = null;
        try {
            result = ((A2KBAnnotator) errorCounter.getDecoratedAnnotator()).performA2KBTask(document);
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
            return new ArrayList<MeaningSpan>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "MeaningSpan");
        }
        return result;
    }

    protected static List<TypedSpan> performTyping(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<TypedSpan> result = null;
        try {
            result = ((EntityTyper) errorCounter.getDecoratedAnnotator()).performTyping(document);
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
            return new ArrayList<TypedSpan>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "TypedSpan");
        }
        return result;
    }

    protected static List<Span> performRecognition(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<Span> result = null;
        try {
            result = ((EntityRecognizer) errorCounter.getDecoratedAnnotator()).performRecognition(document);
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
            return new ArrayList<Span>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "Span");
        }
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask1(ErrorCountingAnnotatorDecorator errorCounter,
            Document document) throws GerbilException {
        List<TypedNamedEntity> result = null;
        try {
            result = ((OKETask1Annotator) errorCounter.getDecoratedAnnotator()).performTask1(document);
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
            return new ArrayList<TypedNamedEntity>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "TypedNamedEntity");
        }
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask2(ErrorCountingAnnotatorDecorator errorCounter,
            Document document) throws GerbilException {
        List<TypedNamedEntity> result = null;
        try {
            result = ((OKETask2Annotator) errorCounter.getDecoratedAnnotator()).performTask2(document);
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
            return new ArrayList<TypedNamedEntity>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "TypedNamedEntity");
        }
        return result;
    }

    public static List<TypedSpan> performRT2KBTask(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<TypedSpan> result = null;
        try {
            result = ((RT2KBAnnotator) errorCounter.getDecoratedAnnotator()).performRT2KBTask(document);
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
            return new ArrayList<TypedSpan>(0);
        }
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            logResult(result, errorCounter.getName(), "TypedNamedEntity");
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
    public void evaluate(List<List<Marking>> annotatorResults, List<List<Marking>> goldStandard,
            EvaluationResultContainer results,String language) {
        results.addResult(new IntEvaluationResult(ERROR_COUNT_RESULT_NAME, errorCount));
    }

    public static synchronized void setPrintDebugMsg(boolean flag) {
        printDebugMsg = flag;
    }
}
