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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
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
 * This is a decorator for an {@link Annotator} which is used to ensure that a
 * certain delay is inserted between two consecutive calls to the
 * {@link Annotator}. This class should be used wrapped by a
 * {@link SingleInstanceSecuringAnnotatorDecorator} to ensure that not two
 * different instances of the {@link Annotator} are used within a single
 * process.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 * 
 */
public abstract class DelayInsertingAnnotatorDecorator extends AbstractAnnotatorDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayInsertingAnnotatorDecorator.class);

    protected static final Map<String, Long> annotatorUsageTimestamps = new HashMap<String, Long>();
    protected static final Semaphore registryMutex = new Semaphore(1);

    private final long delay;

    protected DelayInsertingAnnotatorDecorator(Annotator decoratedAnnotator, long delay) {
        super(decoratedAnnotator);
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    @SuppressWarnings("deprecation")
    public static DelayInsertingAnnotatorDecorator createDecorator(ExperimentType type, long delay,
            Annotator annotator) {
        switch (type) {
        case C2KB:
            return new DelayInsertingC2KBAnnotator((C2KBAnnotator) annotator, delay);
        case A2KB:
            return new DelayInsertingA2KBAnnotator((A2KBAnnotator) annotator, delay);
        case D2KB:
            return new DelayInsertingD2KBAnnotator((D2KBAnnotator) annotator, delay);
        case ERec:
            return new DelayInsertingEntityRecognizer((EntityRecognizer) annotator, delay);
        case ETyping:
            return new DelayInsertingEntityTyper((EntityTyper) annotator, delay);
        case OKE_Task1:
            return new DelayInsertingOKETask1Annotator((OKETask1Annotator) annotator, delay);
        case OKE_Task2:
            return new DelayInsertingOKETask2Annotator((OKETask2Annotator) annotator, delay);
        case QA:
            return new DelayInsertingQASystem((QASystem) annotator, delay);
        case Rc2KB:
            break;
        case Sa2KB:
            break;
        case Sc2KB:
            break;
        default:
            break;

        }
        LOGGER.error("Couldn't generate a DelayInsertingAnnotatorDecorator for the given annotator. Returning null.");
        return null;
    }

    private static class DelayInsertingC2KBAnnotator extends DelayInsertingAnnotatorDecorator implements C2KBAnnotator {

        public DelayInsertingC2KBAnnotator(C2KBAnnotator decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performC2KB(this, document);
        }
    }

    private static class DelayInsertingD2KBAnnotator extends DelayInsertingAnnotatorDecorator implements D2KBAnnotator {

        public DelayInsertingD2KBAnnotator(D2KBAnnotator decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performD2KBTask(this, document);
        }
    }

    private static class DelayInsertingEntityRecognizer extends DelayInsertingAnnotatorDecorator
            implements EntityRecognizer {

        public DelayInsertingEntityRecognizer(EntityRecognizer decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performRecognition(this, document);
        }
    }

    private static class DelayInsertingA2KBAnnotator extends DelayInsertingD2KBAnnotator implements A2KBAnnotator {

        public DelayInsertingA2KBAnnotator(A2KBAnnotator decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performC2KB(this, document);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performRecognition(this, document);
        }

        @Override
        public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performExtraction(this, document);
        }
    }

    private static class DelayInsertingEntityTyper extends DelayInsertingAnnotatorDecorator implements EntityTyper {

        protected DelayInsertingEntityTyper(EntityTyper decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performTyping(this, document);
        }
    }

    private static class DelayInsertingOKETask1Annotator extends DelayInsertingA2KBAnnotator
            implements OKETask1Annotator {

        protected DelayInsertingOKETask1Annotator(OKETask1Annotator decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<TypedSpan> performTyping(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performTyping(this, document);
        }

        @Override
        public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performOKETask1(this, document);
        }
    }

    private static class DelayInsertingOKETask2Annotator extends DelayInsertingAnnotatorDecorator
            implements OKETask2Annotator {

        protected DelayInsertingOKETask2Annotator(OKETask2Annotator decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performOKETask2(this, document);
        }
    }

    private static class DelayInsertingQASystem extends DelayInsertingAnnotatorDecorator implements QASystem {

        protected DelayInsertingQASystem(QASystem decoratedAnnotator, long delay) {
            super(decoratedAnnotator, delay);
        }

        @Override
        public List<Marking> answerQuestion(Document document, String questionLang) throws GerbilException {
            return DelayInsertingAnnotatorDecorator.performQATask(this, document, questionLang);
        }
    }

    protected static <R> List<R> performRequest(Function<Document, List<R>> function,
            DelayInsertingAnnotatorDecorator decorator, Document document) throws GerbilException {
        List<R> result = null;
        try {
            insertDelayIfNeeded(decorator);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for the configured delay.", e);
            throw new GerbilException("Interrupted while waiting for the configured delay.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        try {
            result = function.apply(document);
        } finally {
            saveTimestamp(decorator);
        }
        return result;
    }

    protected static List<Meaning> performC2KB(DelayInsertingAnnotatorDecorator decorator, Document document)
            throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((C2KBAnnotator) decorator.getDecoratedAnnotator()).performC2KB(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<MeaningSpan> performD2KBTask(DelayInsertingAnnotatorDecorator decorator, Document document)
            throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((D2KBAnnotator) decorator.getDecoratedAnnotator()).performD2KBTask(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<MeaningSpan> performExtraction(DelayInsertingAnnotatorDecorator decorator, Document document)
            throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((A2KBAnnotator) decorator.getDecoratedAnnotator()).performA2KBTask(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<TypedSpan> performTyping(DelayInsertingAnnotatorDecorator decorator, Document document)
            throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((EntityTyper) decorator.getDecoratedAnnotator()).performTyping(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<Span> performRecognition(DelayInsertingAnnotatorDecorator decorator, Document document)
            throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((EntityRecognizer) decorator.getDecoratedAnnotator()).performRecognition(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<TypedNamedEntity> performOKETask1(DelayInsertingAnnotatorDecorator decorator,
            Document document) throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((OKETask1Annotator) decorator.getDecoratedAnnotator()).performTask1(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<TypedNamedEntity> performOKETask2(DelayInsertingAnnotatorDecorator decorator,
            Document document) throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((OKETask2Annotator) decorator.getDecoratedAnnotator()).performTask2(d);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    protected static List<Marking> performQATask(DelayInsertingAnnotatorDecorator decorator, Document document,
            String questionLanguage) throws GerbilException {
        try {
            return DelayInsertingAnnotatorDecorator.performRequest(d -> {
                try {
                    return ((QASystem) decorator.getDecoratedAnnotator()).answerQuestion(d, questionLanguage);
                } catch (Exception e) {
                    throw new DelayInsertingRuntimeException(e);
                }
            }, decorator, document);
        } catch (DelayInsertingRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GerbilException) {
                throw (GerbilException) cause;
            } else {
                throw e;
            }
        }
    }

    /**
     * Registers the given {@link Annotator} (if it is not already present in the
     * registration) and returns its semaphore.
     * 
     * @param decoratedAnnotator
     * @return
     */
    protected static void insertDelayIfNeeded(DelayInsertingAnnotatorDecorator decoratedAnnotator)
            throws InterruptedException {
        try {
            registryMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for registry mutex. Returning null.", e);
            return;
        }
        try {
            String key = decoratedAnnotator.getName();
            if (annotatorUsageTimestamps.containsKey(key)) {
                Thread.sleep(
                        annotatorUsageTimestamps.get(key) + decoratedAnnotator.getDelay() - System.currentTimeMillis());
            }
        } finally {
            registryMutex.release();
        }
    }

    /**
     * Removes the given {@link Annotator} from the registration.
     * 
     * @param decoratedAnnotator
     */
    protected static void saveTimestamp(Annotator decoratedAnnotator) {
        try {
            registryMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for registry mutex. Aborting.");
            return;
        }
        try {
            annotatorUsageTimestamps.put(decoratedAnnotator.getName(), System.currentTimeMillis());
        } finally {
            registryMutex.release();
        }
    }

    protected static class DelayInsertingRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public DelayInsertingRuntimeException() {
        }

        public DelayInsertingRuntimeException(String message) {
            super(message);
        }

        public DelayInsertingRuntimeException(Exception cause) {
            super(cause);
        }

        public DelayInsertingRuntimeException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
