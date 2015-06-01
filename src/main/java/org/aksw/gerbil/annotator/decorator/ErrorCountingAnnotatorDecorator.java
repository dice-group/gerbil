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
package org.aksw.gerbil.annotator.decorator;

import it.acubelab.batframework.utils.AnnotationException;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.IntEvaluationResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
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
 * @author Michael Röder
 * 
 */
public abstract class ErrorCountingAnnotatorDecorator implements Evaluator<Marking>, ErrorCounter, Annotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCountingAnnotatorDecorator.class);

    private static final double AMOUNT_OF_TOLERATED_ERRORS = 0.25;

    public static final String ERROR_COUNT_RESULT_NAME = "error count";

    @SuppressWarnings("deprecation")
    public static ErrorCountingAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator,
            int numberOfExpectedCalls) {
        int maxErrors = (int) Math.ceil(AMOUNT_OF_TOLERATED_ERRORS * numberOfExpectedCalls);
        switch (type) {
        case A2KB:
            break;
        case C2KB:
            break;
        case D2KB:
            break;
        case EExt:
            return new ErrorCountingEntityExtractor((EntityExtractor) annotator, maxErrors);
        case ELink:
            return new ErrorCountingEntityLinker((EntityLinker) annotator, maxErrors);
        case ERec:
            return new ErrorCountingEntityRecognizer((EntityRecognizer) annotator, maxErrors);
        case ETyping:
            return new ErrorCountingEntityTyper((EntityTyper) annotator, maxErrors);
        case OKE_Task1:
            return new ErrorCountingOKETask1Annotator((OKETask1Annotator) annotator, maxErrors);
        case OKE_Task2:
            return new ErrorCountingOKETask2Annotator((OKETask2Annotator) annotator, maxErrors);
        case Rc2KB:
            break;
        case Sa2KB:
            break;
        case Sc2KB:
            break;
        default:
            break;

        }
        // if (annotator instanceof Sc2WSystem) {
        // return new ErrorCountingSc2W((Sc2WSystem) annotator, maxErrors);
        // }
        // if (annotator instanceof C2WSystem) {
        // return new ErrorCountingC2W((C2WSystem) annotator, maxErrors);
        // }
        return null;
    }

    private static class ErrorCountingEntityLinker extends ErrorCountingAnnotatorDecorator implements EntityLinker {

        public ErrorCountingEntityLinker(EntityLinker decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<MeaningSpan> performLinking(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performLinking(this, document);
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

    private static class ErrorCountingEntityExtractor extends ErrorCountingEntityLinker implements EntityExtractor {

        public ErrorCountingEntityExtractor(EntityExtractor decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performRecognition(this, document);
        }

        @Override
        public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
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

    private static class ErrorCountingOKETask1Annotator extends ErrorCountingEntityExtractor implements
            OKETask1Annotator {

        protected ErrorCountingOKETask1Annotator(OKETask1Annotator decoratedAnnotator, int maxErrors) {
            super(decoratedAnnotator, maxErrors);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return ErrorCountingAnnotatorDecorator.performTyping(this, document);
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

    protected static List<MeaningSpan> performLinking(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<MeaningSpan> result = null;
        try {
            result = ((EntityLinker) errorCounter.getDecoratedAnnotator()).performLinking(document);
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (MeaningSpan ne : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("NamedEntity");
                builder.append(ne.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static List<MeaningSpan> performExtraction(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws GerbilException {
        List<MeaningSpan> result = null;
        try {
            result = ((EntityExtractor) errorCounter.getDecoratedAnnotator()).performExtraction(document);
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (MeaningSpan ne : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("NamedEntity");
                builder.append(ne.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (TypedSpan ts : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("NamedEntity");
                builder.append(ts.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static List<Span> performRecognition(ErrorCountingAnnotatorDecorator errorCounter, Document document)
            throws AnnotationException, GerbilException {
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Span s : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Span");
                builder.append(s.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask1(ErrorCountingAnnotatorDecorator errorCounter,
            Document document) throws AnnotationException, GerbilException {
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Span s : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Span");
                builder.append(s.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask2(ErrorCountingAnnotatorDecorator errorCounter,
            Document document) throws AnnotationException, GerbilException {
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
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Span s : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Span");
                builder.append(s.toString());
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected int errorCount = 0;
    protected int maxErrors;
    protected Annotator decoratedAnnotator;

    protected ErrorCountingAnnotatorDecorator(Annotator decoratedAnnotator, int maxErrors) {
        this.decoratedAnnotator = decoratedAnnotator;
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

    @Override
    public String getName() {
        return decoratedAnnotator.getName();
    }

    protected Annotator getDecoratedAnnotator() {
        return decoratedAnnotator;
    }

    protected void increaseErrorCount() throws GerbilException {
        ++errorCount;
        if (errorCount > maxErrors) {
            throw new GerbilException("Saw to many errors (maximum was set to " + maxErrors + ").",
                    ErrorTypes.TO_MANY_SINGLE_ERRORS);
        }
    }

    @Override
    public void evaluate(List<List<Marking>> annotatorResults, List<List<Marking>> goldStandard,
            EvaluationResultContainer results) {
        results.addResult(new IntEvaluationResult(ERROR_COUNT_RESULT_NAME, errorCount));
    }
}
