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

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
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
        implements Evaluator<Marking>, TimeMeasurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeMeasuringAnnotatorDecorator.class);

    public static final String AVG_TIME_RESULT_NAME = "avg millis/doc";

    @SuppressWarnings("deprecation")
    public static TimeMeasuringAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator) {
        switch (type) {
        case C2KB:
            return new TimeMeasuringC2KBAnnotator((C2KBAnnotator) annotator);
        case A2KB:
            return new TimeMeasuringA2KBAnnotator((A2KBAnnotator) annotator);
        case D2KB:
            return new TimeMeasuringD2KBAnnotator((D2KBAnnotator) annotator);
        case ERec:
            return new TimeMeasuringEntityRecognizer((EntityRecognizer) annotator);
        case ETyping:
            return new TimeMeasuringEntityTyper((EntityTyper) annotator);
        case OKE_Task1:
            return new TimeMeasuringOKETask1Annotator((OKETask1Annotator) annotator);
        case OKE_Task2:
            return new TimeMeasuringOKETask2Annotator((OKETask2Annotator) annotator);
        case QA:
            return new TimeMeasuringQASystem((QASystem) annotator);
        case Rc2KB:
            break;
        case Sa2KB:
            break;
        case Sc2KB:
            break;
        default:
            break;
        }
        LOGGER.error("Couldn't generate a TimeMeasuringAnnotatorDecorator for the given annotator. Returning null.");
        return null;
    }

    private static class TimeMeasuringC2KBAnnotator extends TimeMeasuringAnnotatorDecorator implements C2KBAnnotator {

        public TimeMeasuringC2KBAnnotator(C2KBAnnotator decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performC2KB(this, document);
        }

    }

    private static class TimeMeasuringD2KBAnnotator extends TimeMeasuringAnnotatorDecorator implements D2KBAnnotator {

        public TimeMeasuringD2KBAnnotator(D2KBAnnotator decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performD2KBTask(this, document);
        }
    }

    private static class TimeMeasuringEntityRecognizer extends TimeMeasuringAnnotatorDecorator
            implements EntityRecognizer {

        public TimeMeasuringEntityRecognizer(EntityRecognizer decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performRecognition(this, document);
        }
    }

    private static class TimeMeasuringA2KBAnnotator extends TimeMeasuringD2KBAnnotator implements A2KBAnnotator {

        public TimeMeasuringA2KBAnnotator(A2KBAnnotator decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performC2KB(this, document);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performRecognition(this, document);
        }

        @Override
        public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performExtraction(this, document);
        }

    }

    private static class TimeMeasuringEntityTyper extends TimeMeasuringAnnotatorDecorator implements EntityTyper {

        protected TimeMeasuringEntityTyper(EntityTyper decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performTyping(this, document);
        }
    }

    private static class TimeMeasuringOKETask1Annotator extends TimeMeasuringA2KBAnnotator
            implements OKETask1Annotator {

        protected TimeMeasuringOKETask1Annotator(OKETask1Annotator decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performTyping(this, document);
        }

        @Override
        public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performOKETask1(this, document);
        }
    }

    private static class TimeMeasuringOKETask2Annotator extends TimeMeasuringAnnotatorDecorator
            implements OKETask2Annotator {

        protected TimeMeasuringOKETask2Annotator(OKETask2Annotator decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performOKETask2(this, document);
        }
    }

    private static class TimeMeasuringQASystem extends TimeMeasuringAnnotatorDecorator implements QASystem {

        protected TimeMeasuringQASystem(QASystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Marking> answerQuestion(Document document, String questionLang) throws GerbilException {
            return TimeMeasuringAnnotatorDecorator.performQATask(this, document, questionLang);
        }
    }

    protected static List<Meaning> performC2KB(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<Meaning> result = null;
        result = ((C2KBAnnotator) timeMeasurer.getDecoratedAnnotator()).performC2KB(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<MeaningSpan> performD2KBTask(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<MeaningSpan> result = null;
        result = ((D2KBAnnotator) timeMeasurer.getDecoratedAnnotator()).performD2KBTask(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<MeaningSpan> performExtraction(TimeMeasuringAnnotatorDecorator timeMeasurer,
            Document document) throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<MeaningSpan> result = null;
        result = ((A2KBAnnotator) timeMeasurer.getDecoratedAnnotator()).performA2KBTask(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<TypedSpan> performTyping(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<TypedSpan> result = null;
        result = ((EntityTyper) timeMeasurer.getDecoratedAnnotator()).performTyping(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<Span> performRecognition(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<Span> result = null;
        result = ((EntityRecognizer) timeMeasurer.getDecoratedAnnotator()).performRecognition(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask1(TimeMeasuringAnnotatorDecorator timeMeasurer,
            Document document) throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<TypedNamedEntity> result = null;
        result = ((OKETask1Annotator) timeMeasurer.getDecoratedAnnotator()).performTask1(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<TypedNamedEntity> performOKETask2(TimeMeasuringAnnotatorDecorator timeMeasurer,
            Document document) throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<TypedNamedEntity> result = null;
        result = ((OKETask2Annotator) timeMeasurer.getDecoratedAnnotator()).performTask2(document);
        timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
        return result;
    }

    protected static List<Marking> performQATask(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document, String questionLang)
            throws GerbilException {
        long startTime = System.currentTimeMillis();
        List<Marking> result = null;
        result = ((QASystem) timeMeasurer.getDecoratedAnnotator()).answerQuestion(document, questionLang);
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
    public void evaluate(List<Document> instances, List<List<Marking>> annotatorResults, List<List<Marking>> goldStandard,
            EvaluationResultContainer results) {
        if (callCount > 0) {
            results.addResult(new DoubleEvaluationResult(AVG_TIME_RESULT_NAME, getAverageRuntime()));
        }
    }
}
