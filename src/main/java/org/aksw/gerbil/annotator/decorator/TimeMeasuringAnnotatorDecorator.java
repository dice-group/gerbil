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

import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

/**
 * This is a simple decorator for an annotator which handles exceptions thrown
 * by the decorated annotator. It logs these exceptions and counts the errors.
 * This behavior makes it possible, that the BAT-Framework doesn't quit the
 * experiment even if an exception is thrown.
 * 
 * @author Michael Röder
 * 
 */
public abstract class TimeMeasuringAnnotatorDecorator implements Evaluator<Marking>, TimeMeasurer, Annotator {

	public static final String AVG_TIME_RESULT_NAME = "avg sec./doc";

	@SuppressWarnings("deprecation")
	public static TimeMeasuringAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator) {
		switch (type) {
		case A2KB:
			break;
		case C2KB:
			break;
		case D2KB:
			break;
		case EExt:
			return new TimeMeasuringEntityExtractor((EntityExtractor) annotator);
		case ELink:
			return new TimeMeasuringEntityLinker((EntityLinker) annotator);
		case ERec:
			return new TimeMeasuringEntityRecognizer((EntityRecognizer) annotator);
		case ETyping:
			return new TimeMeasuringEntityTyper((EntityTyper) annotator);
		case OKE_Task1:
			return new TimeMeasuringOKETask1Annotator((OKETask1Annotator) annotator);
		case OKE_Task2:
			return new TimeMeasuringOKETask2Annotator((OKETask2Annotator) annotator);
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

	private static class TimeMeasuringEntityLinker extends TimeMeasuringAnnotatorDecorator implements EntityLinker {

		public TimeMeasuringEntityLinker(EntityLinker decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<MeaningSpan> performLinking(Document document) throws GerbilException {
			return TimeMeasuringAnnotatorDecorator.performLinking(this, document);
		}
	}

	private static class TimeMeasuringEntityRecognizer extends TimeMeasuringAnnotatorDecorator implements
			EntityRecognizer {

		public TimeMeasuringEntityRecognizer(EntityRecognizer decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return TimeMeasuringAnnotatorDecorator.performRecognition(this, document);
		}
	}

	private static class TimeMeasuringEntityExtractor extends TimeMeasuringEntityLinker implements EntityExtractor {

		public TimeMeasuringEntityExtractor(EntityExtractor decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<Span> performRecognition(Document document) throws GerbilException {
			return TimeMeasuringAnnotatorDecorator.performRecognition(this, document);
		}

		@Override
		public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
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

	private static class TimeMeasuringOKETask1Annotator extends TimeMeasuringEntityExtractor implements
			OKETask1Annotator {

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

	private static class TimeMeasuringOKETask2Annotator extends TimeMeasuringAnnotatorDecorator implements
			OKETask2Annotator {

		protected TimeMeasuringOKETask2Annotator(OKETask2Annotator decoratedAnnotator) {
			super(decoratedAnnotator);
		}

		@Override
		public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
			return TimeMeasuringAnnotatorDecorator.performOKETask2(this, document);
		}
	}

	protected static List<MeaningSpan> performLinking(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
			throws GerbilException {
		long startTime = System.currentTimeMillis();
		List<MeaningSpan> result = null;
		result = ((EntityLinker) timeMeasurer.getDecoratedAnnotator()).performLinking(document);
		timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
		return result;
	}

	protected static List<MeaningSpan> performExtraction(TimeMeasuringAnnotatorDecorator timeMeasurer, Document document)
			throws GerbilException {
		long startTime = System.currentTimeMillis();
		List<MeaningSpan> result = null;
		result = ((EntityExtractor) timeMeasurer.getDecoratedAnnotator()).performExtraction(document);
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
			throws AnnotationException, GerbilException {
		long startTime = System.currentTimeMillis();
		List<Span> result = null;
		result = ((EntityRecognizer) timeMeasurer.getDecoratedAnnotator()).performRecognition(document);
		timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
		return result;
	}

	protected static List<TypedNamedEntity> performOKETask1(TimeMeasuringAnnotatorDecorator timeMeasurer,
			Document document) throws AnnotationException, GerbilException {
		long startTime = System.currentTimeMillis();
		List<TypedNamedEntity> result = null;
		result = ((OKETask1Annotator) timeMeasurer.getDecoratedAnnotator()).performTask1(document);
		timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
		return result;
	}

	protected static List<TypedNamedEntity> performOKETask2(TimeMeasuringAnnotatorDecorator timeMeasurer,
			Document document) throws AnnotationException, GerbilException {
		long startTime = System.currentTimeMillis();
		List<TypedNamedEntity> result = null;
		result = ((OKETask2Annotator) timeMeasurer.getDecoratedAnnotator()).performTask2(document);
		timeMeasurer.addCallRuntime(System.currentTimeMillis() - startTime);
		return result;
	}

	protected long timeSum = 0;
	protected int callCount = 0;
	protected Annotator decoratedAnnotator;

	protected TimeMeasuringAnnotatorDecorator(Annotator decoratedAnnotator) {
		this.decoratedAnnotator = decoratedAnnotator;
	}

	@Override
	public String getName() {
		return decoratedAnnotator.getName();
	}

	protected Annotator getDecoratedAnnotator() {
		return decoratedAnnotator;
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
	public void evaluate(List<List<Marking>> annotatorResults, List<List<Marking>> goldStandard,
			EvaluationResultContainer results) {
		if (callCount > 0) {
			results.addResult(new DoubleEvaluationResult(AVG_TIME_RESULT_NAME, getAverageRuntime()));
		}
	}
}
