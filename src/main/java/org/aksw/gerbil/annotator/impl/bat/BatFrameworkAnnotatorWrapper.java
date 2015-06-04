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
package org.aksw.gerbil.annotator.impl.bat;

import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.utils.bat.BAT2NIF_TranslationHelper;
import org.aksw.gerbil.utils.bat.NIF2BAT_TranslationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatFrameworkAnnotatorWrapper {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(BatFrameworkAnnotatorWrapper.class);
    
    public static final String ANNOTATOR_NAME_SUFFIX = " (BAT)";

    public static Annotator create(TopicSystem annotator,
	    WikipediaApiInterface wikiApi) {
	LOGGER.warn("Using wrappers for BAT framework adapters is not recommended!");
	if ((annotator instanceof Sa2WSystem)
		|| (annotator instanceof A2WSystem)) {
	    return new A2KBSystemWrapper((A2WSystem) annotator, wikiApi);
	}
	if (annotator instanceof D2WSystem) {
	    return new D2KBSystemWrapper((D2WSystem) annotator, wikiApi);
	}
	return null; // TODO
    }

    protected abstract static class AbstractTopicSystemWrapper<T extends TopicSystem>
	    implements Annotator {
	protected T annotator;
	protected BAT2NIF_TranslationHelper translater;

	public AbstractTopicSystemWrapper(T annotator,
		WikipediaApiInterface wikiApi) {
	    this.annotator = annotator;
	    this.translater = new BAT2NIF_TranslationHelper(wikiApi);
	}

	@Override
	public String getName() {
	    return annotator.getName() + ANNOTATOR_NAME_SUFFIX;
	}

	protected List<Span> performRecognition(A2WSystem annotator,
		Document document) {
	    return new ArrayList<Span>(
		    translater.translateAnnotations(annotator.solveA2W(document
			    .getText())));
	}

	protected List<MeaningSpan> performExtraction(A2WSystem annotator,
		Document document) {
	    return translater.translateAnnotations(annotator.solveA2W(document
		    .getText()));
	}

	protected List<MeaningSpan> performLinking(D2WSystem annotator,
		Document document) {
	    return translater.translateAnnotations(annotator.solveD2W(
		    document.getText(),
		    NIF2BAT_TranslationHelper.createMentions(document)));
	}
    }

    protected static class A2KBSystemWrapper extends
	    AbstractTopicSystemWrapper<A2WSystem> implements EntityRecognizer,
	    EntityLinker, EntityExtractor {

	public A2KBSystemWrapper(A2WSystem annotator,
		WikipediaApiInterface wikiApi) {
	    super(annotator, wikiApi);
	}

	@Override
	public List<Span> performRecognition(Document document)
		throws GerbilException {
	    return performRecognition(annotator, document);
	}

	@Override
	public List<MeaningSpan> performExtraction(Document document) {
	    return performExtraction(annotator, document);
	}

	@Override
	public List<MeaningSpan> performLinking(Document document)
		throws GerbilException {
	    return performLinking(annotator, document);
	}
    }

    protected static class D2KBSystemWrapper extends
	    AbstractTopicSystemWrapper<D2WSystem> implements EntityLinker {

	public D2KBSystemWrapper(D2WSystem annotator,
		WikipediaApiInterface wikiApi) {
	    super(annotator, wikiApi);
	}

	@Override
	public List<MeaningSpan> performLinking(Document document)
		throws GerbilException {
	    return performLinking(annotator, document);
	}

    }
}
