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

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
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
 * This is a decorator for an {@link Annotator} which is used to make sure that
 * the decorated {@link Annotator} instance is used by one single thread at a
 * time. This is needed because of some Singleton based annotator
 * implementations that lead to an increase of the runtime measurement while
 * their threads are not working but waiting for a Semaphore. Thus, this
 * decorator should be used to decorate the time measurement.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class SingleInstanceSecuringAnnotatorDecorator extends AbstractAnnotatorDecorator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceSecuringAnnotatorDecorator.class);

	@SuppressWarnings("deprecation")
	public static SingleInstanceSecuringAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator) {
		switch (type) {

		//case MT:
		//	break;
		default:
			break;

		}
		return null;
	}

	private static class SingleInstanceSecuringC2KBAnnotator extends SingleInstanceSecuringAnnotatorDecorator
			implements C2KBAnnotator {

		public SingleInstanceSecuringC2KBAnnotator(C2KBAnnotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Meaning> performC2KB(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performC2KB(this, document);
		}
	}

	private static class SingleInstanceSecuringREAnnotator extends SingleInstanceSecuringAnnotatorDecorator
			implements REAnnotator {

		public SingleInstanceSecuringREAnnotator(REAnnotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Relation> performRETask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRE(this, document);

		}
	}

	private static class SingleInstanceSecuringOKE2018Task4Annotator extends SingleInstanceSecuringAnnotatorDecorator
			implements OKE2018Task4Annotator {

		public SingleInstanceSecuringOKE2018Task4Annotator(OKE2018Task4Annotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Relation> performRETask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRE(this, document);

		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRecognition(this, document);

		}

		@Override
		public List<Marking> performOKE2018Task4(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performOKE2018Task4(this, document);

		}

		@Override
		public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performExtraction(this, document);

		}

		@Override
		public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performD2KBTask(this, document);
		}

		@Override
		public List<Meaning> performC2KB(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performC2KB(this, document);
		}
	}

	private static class SingleInstanceSecuringD2KBAnnotator extends SingleInstanceSecuringAnnotatorDecorator
			implements D2KBAnnotator {

		public SingleInstanceSecuringD2KBAnnotator(D2KBAnnotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performD2KBTask(this, document);
		}
	}

	private static class SingleInstanceSecuringEntityRecognizer extends SingleInstanceSecuringAnnotatorDecorator
			implements EntityRecognizer {

		public SingleInstanceSecuringEntityRecognizer(EntityRecognizer decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRecognition(this, document);
		}
	}

	private static class SingleInstanceSecuringA2KBAnnotator extends SingleInstanceSecuringD2KBAnnotator
			implements A2KBAnnotator {

		public SingleInstanceSecuringA2KBAnnotator(A2KBAnnotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Meaning> performC2KB(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performC2KB(this, document);
		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRecognition(this, document);
		}

		@Override
		public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performExtraction(this, document);
		}

	}

	private static class SingleInstanceSecuringEntityTyper extends SingleInstanceSecuringAnnotatorDecorator
			implements EntityTyper {

		protected SingleInstanceSecuringEntityTyper(EntityTyper decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<TypedSpan> performTyping(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performTyping(this, document);
		}
	}

	private static class SingleInstanceSecuringRT2KBAnnotator extends SingleInstanceSecuringEntityRecognizer
			implements RT2KBAnnotator {

		protected SingleInstanceSecuringRT2KBAnnotator(RT2KBAnnotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<TypedSpan> performTyping(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performTyping(this, document);
		}

		@Override
		public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRT2KBTask(this, document);
		}
	}

	private static class SingleInstanceSecuringOKETask1Annotator extends SingleInstanceSecuringA2KBAnnotator
			implements OKETask1Annotator {

		protected SingleInstanceSecuringOKETask1Annotator(OKETask1Annotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<TypedSpan> performTyping(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performTyping(this, document);
		}

		@Override
		public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performRT2KBTask(this, document);
		}

		@Override
		public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performOKETask1(this, document);
		}
	}

	private static class SingleInstanceSecuringOKETask2Annotator extends SingleInstanceSecuringAnnotatorDecorator
			implements OKETask2Annotator {

		protected SingleInstanceSecuringOKETask2Annotator(OKETask2Annotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performOKETask2(this, document);
		}
	}

	protected static List<Meaning> performC2KB(SingleInstanceSecuringAnnotatorDecorator decorator, Document document)
			throws GerbilException {
		List<Meaning> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((C2KBAnnotator) decorator.getDecoratedAnnotator()).performC2KB(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<MeaningSpan> performD2KBTask(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<MeaningSpan> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((D2KBAnnotator) decorator.getDecoratedAnnotator()).performD2KBTask(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<MeaningSpan> performExtraction(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<MeaningSpan> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((A2KBAnnotator) decorator.getDecoratedAnnotator()).performA2KBTask(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<TypedSpan> performTyping(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<TypedSpan> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((EntityTyper) decorator.getDecoratedAnnotator()).performTyping(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<Span> performRecognition(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<Span> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((EntityRecognizer) decorator.getDecoratedAnnotator()).performRecognition(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}
	
	protected static List<Relation> performRE(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<Relation> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((REAnnotator) decorator.getDecoratedAnnotator()).performRETask(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}
	
	protected static List<Marking> performOKE2018Task4(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<Marking> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((OKE2018Task4Annotator) decorator.getDecoratedAnnotator()).performOKE2018Task4(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<TypedNamedEntity> performOKETask1(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<TypedNamedEntity> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((OKETask1Annotator) decorator.getDecoratedAnnotator()).performTask1(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<TypedNamedEntity> performOKETask2(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<TypedNamedEntity> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((OKETask2Annotator) decorator.getDecoratedAnnotator()).performTask2(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	protected static List<TypedSpan> performRT2KBTask(SingleInstanceSecuringAnnotatorDecorator decorator,
			Document document) throws GerbilException {
		List<TypedSpan> result = null;
		try {
			decorator.semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
			throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		try {
			result = ((RT2KBAnnotator) decorator.getDecoratedAnnotator()).performRT2KBTask(document);
		} finally {
			decorator.semaphore.release();
		}
		return result;
	}

	/**
	 * Registers the given {@link Annotator} (if it is not already present in the
	 * registration) and returns its semaphore.
	 * 
	 * @param decoratedAnnotator
	 * @return
	 */
	protected static Semaphore registerAnnotator(Annotator decoratedAnnotator) {
		try {
			registryMutex.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Exception while waiting for registry mutex. Returning null.", e);
			return null;
		}
		Annotator annotator = decoratedAnnotator;
		while (annotator instanceof AnnotatorDecorator) {
			annotator = ((AnnotatorDecorator) annotator).getDecoratedAnnotator();
		}
		RegistryValue value;
		if (annotatorRegistry.containsKey(annotator)) {
			value = annotatorRegistry.get(annotator);
		} else {
			value = new RegistryValue();
			annotatorRegistry.put(annotator, value);
		}
		++value.usageCounter;
		Semaphore semaphore = value.semaphore;
		registryMutex.release();
		return semaphore;
	}

	/**
	 * Removes the given {@link Annotator} from the registration.
	 * 
	 * @param decoratedAnnotator
	 */
	protected static void unregisterAnnotator(Annotator decoratedAnnotator) {
		try {
			registryMutex.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Exception while waiting for registry mutex. Aborting.");
			return;
		}
		Annotator annotator = decoratedAnnotator;
		while (annotator instanceof AnnotatorDecorator) {
			annotator = ((AnnotatorDecorator) annotator).getDecoratedAnnotator();
		}
		if (annotatorRegistry.containsKey(annotator)) {
			RegistryValue value = annotatorRegistry.get(annotator);
			--value.usageCounter;
			if (value.usageCounter == 0) {
				annotatorRegistry.remove(annotator);
			}
		} else {
			LOGGER.warn("Expected to find the annotator {} inside the registry but it wasn't there. Ignoring it.",
					annotator.toString());
		}
		registryMutex.release();
	}

	protected static final Map<Annotator, RegistryValue> annotatorRegistry = new HashMap<Annotator, RegistryValue>();
	protected static final Semaphore registryMutex = new Semaphore(1);

	private final Semaphore semaphore;

	protected SingleInstanceSecuringAnnotatorDecorator(Annotator decoratedAnnotator) {
		super(decoratedAnnotator);
		semaphore = registerAnnotator(decoratedAnnotator);
	}

	@Override
	protected void finalize() throws Throwable {
		unregisterAnnotator(decoratedAnnotator);
		super.finalize();
	}

	private static class RegistryValue {
		public final Semaphore semaphore = new Semaphore(1);
		public int usageCounter = 0;
	}
}
