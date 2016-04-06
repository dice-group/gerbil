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
package org.aksw.gerbil.annotator.impl.bat;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.utils.bat.BAT2NIF_TranslationHelper;
import org.aksw.gerbil.utils.bat.NIF2BAT_TranslationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unipi.di.acube.batframework.problems.A2WSystem;
import it.unipi.di.acube.batframework.problems.C2WSystem;
import it.unipi.di.acube.batframework.problems.D2WSystem;
import it.unipi.di.acube.batframework.problems.Sa2WSystem;
import it.unipi.di.acube.batframework.problems.TopicSystem;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

public class BatFrameworkAnnotatorWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatFrameworkAnnotatorWrapper.class);

    public static final String ANNOTATOR_NAME_SUFFIX = " (BAT)";

    public static Annotator create(TopicSystem annotator, WikipediaApiInterface wikiApi) {
        LOGGER.warn("Using wrappers for BAT framework adapters is not recommended!");
        if ((annotator instanceof Sa2WSystem) || (annotator instanceof A2WSystem)) {
            return new A2KBSystemWrapper((A2WSystem) annotator, wikiApi);
        }
        if (annotator instanceof D2WSystem) {
            return new D2KBSystemWrapper((D2WSystem) annotator, wikiApi);
        }
        LOGGER.error(
                "Couldn't find a matching wrapper for \"" + annotator.getClass().getName() + "\". Returning null.");
        return null;
    }

    protected abstract static class AbstractTopicSystemWrapper<T extends TopicSystem> extends AbstractAnnotator {
        protected T annotator;
        protected BAT2NIF_TranslationHelper translater;

        public AbstractTopicSystemWrapper(T annotator, WikipediaApiInterface wikiApi) {
            this.annotator = annotator;
            this.translater = new BAT2NIF_TranslationHelper(wikiApi);
        }

        @Override
        public String getName() {
            return annotator.getName() + ANNOTATOR_NAME_SUFFIX;
        }

        protected List<Span> performRecognition(A2WSystem annotator, Document document) {
            return new ArrayList<Span>(translater.translateAnnotations(annotator.solveA2W(document.getText())));
        }

        protected List<MeaningSpan> performExtraction(A2WSystem annotator, Document document) {
            return translater.translateAnnotations(annotator.solveA2W(document.getText()));
        }

        protected List<MeaningSpan> performLinking(D2WSystem annotator, Document document) {
            return translater.translateAnnotations(
                    annotator.solveD2W(document.getText(), NIF2BAT_TranslationHelper.createMentions(document)));
        }

        protected List<Meaning> performC2KB(C2WSystem annotator, Document document) throws GerbilException {
            return translater.translateTags(annotator.solveC2W(document.getText()));
        }

    }

    protected static class A2KBSystemWrapper extends AbstractTopicSystemWrapper<A2WSystem>
            implements EntityRecognizer, D2KBAnnotator, A2KBAnnotator {

        public A2KBSystemWrapper(A2WSystem annotator, WikipediaApiInterface wikiApi) {
            super(annotator, wikiApi);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return performC2KB(annotator, document);
        }

        @Override
        public List<Span> performRecognition(Document document) throws GerbilException {
            return performRecognition(annotator, document);
        }

        @Override
        public List<MeaningSpan> performA2KBTask(Document document) {
            return performExtraction(annotator, document);
        }

        @Override
        public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
            return performLinking(annotator, document);
        }
    }

    protected static class D2KBSystemWrapper extends AbstractTopicSystemWrapper<D2WSystem> implements D2KBAnnotator {

        public D2KBSystemWrapper(D2WSystem annotator, WikipediaApiInterface wikiApi) {
            super(annotator, wikiApi);
        }

        @Override
        public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
            return performLinking(annotator, document);
        }

    }

    protected static class C2KBSystemWrapper extends AbstractTopicSystemWrapper<C2WSystem> implements C2KBAnnotator {

        public C2KBSystemWrapper(C2WSystem annotator, WikipediaApiInterface wikiApi) {
            super(annotator, wikiApi);
        }

        @Override
        public List<Meaning> performC2KB(Document document) throws GerbilException {
            return performC2KB(annotator, document);
        }

    }
}
