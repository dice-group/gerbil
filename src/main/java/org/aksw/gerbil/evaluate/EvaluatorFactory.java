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
package org.aksw.gerbil.evaluate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.ConfidenceScoreEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.DoubleResultComparator;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.HierarchicalFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.InKBClassBasedFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.SpanMergingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.SubTaskAverageCalculator;
import org.aksw.gerbil.evaluate.impl.filter.MarkingFilteringEvaluatorDecorator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingFactory;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.MatchingsSearcher;
import org.aksw.gerbil.matching.impl.MeaningMatchingsSearcher;
import org.aksw.gerbil.semantic.kb.ExactWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.DatasetBasedSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.MultipleSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.utils.filter.TypeBasedMarkingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class EvaluatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorFactory.class);

    private static final String DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY = "org.aksw.gerbil.evaluate.DefaultWellKnownKBs";
    private static final String DEFAULT_WELL_KNOWN_KBS[] = loadDefaultKBs();

    protected SameAsRetriever globalRetriever = null;
    protected UriKBClassifier globalClassifier = null;
    protected SubClassInferencer inferencer = null;

    public EvaluatorFactory() {
        this(null, null, null);
    }

    private static String[] loadDefaultKBs() {
        String kbs[] = GerbilConfiguration.getInstance().getStringArray(DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY);
        if (kbs == null) {
            LOGGER.error("Couldn't load the list of well known KBs. This GERBIL instance might not work as expected!");
        }
        return kbs;
    }

    public EvaluatorFactory(SameAsRetriever globalRetriever) {
        this(globalRetriever, null, null);
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier) {
        this(null, globalClassifier, null);
    }

    public EvaluatorFactory(SameAsRetriever globalRetriever, UriKBClassifier globalClassifier) {
        this(globalRetriever, globalClassifier, null);
    }

    public EvaluatorFactory(SameAsRetriever globalRetriever, UriKBClassifier globalClassifier,
            SubClassInferencer inferencer) {
        this.globalRetriever = globalRetriever;
        if (globalClassifier != null) {
            this.globalClassifier = globalClassifier;
        } else {
            this.globalClassifier = new SimpleWhiteListBasedUriKBClassifier(DEFAULT_WELL_KNOWN_KBS);
        }
        if (inferencer != null) {
            this.inferencer = inferencer;
        } else {
            this.inferencer = new SimpleSubClassInferencer(ModelFactory.createDefaultModel());
        }
    }

    @SuppressWarnings("rawtypes")
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        return createEvaluator(type, configuration, dataset, globalRetriever, globalClassifier, inferencer);
    }

    @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            SameAsRetriever globalRetriever, UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        switch (type) {
        case C2KB: {
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return new ConfidenceScoreEvaluatorDecorator<Meaning>(
                    new InKBClassBasedFMeasureCalculator<Meaning>(new MeaningMatchingsSearcher<Meaning>(
                            globalClassifier, globalRetriever, localRetriever, null), globalClassifier),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case Sa2KB:
        case A2KB:
        case EExt: {
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return new ConfidenceScoreEvaluatorDecorator<NamedEntity>(
                    new FMeasureCalculator<NamedEntity>(
                            new MatchingsCounterImpl<NamedEntity>(new CompoundMatchingsCounter<NamedEntity>(
                                    (MatchingsSearcher<NamedEntity>) MatchingFactory
                                            .createSpanMatchingsSearcher(configuration.matching),
                                    new MeaningMatchingsSearcher<NamedEntity>(globalClassifier, globalRetriever,
                                            localRetriever, null)))),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case ERec: {
            return new ConfidenceScoreEvaluatorDecorator<Span>(
                    new FMeasureCalculator<Span>(
                            new MatchingsCounterImpl<Span>((MatchingsSearcher<Span>) MatchingFactory
                                    .createSpanMatchingsSearcher(configuration.matching))),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case D2KB:
        case ELink: { // FIXME define whether the problem reduction to D2KB
            // should use a weak or strong matching
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return new ConfidenceScoreEvaluatorDecorator<NamedEntity>(
                    new InKBClassBasedFMeasureCalculator<NamedEntity>(new CompoundMatchingsCounter<NamedEntity>(
                            (MatchingsSearcher<NamedEntity>) MatchingFactory
                                    .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(globalClassifier, globalRetriever, localRetriever,
                                    null)),
                            globalClassifier),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case ETyping: {
            return new ConfidenceScoreEvaluatorDecorator<TypedSpan>(
                    new HierarchicalFMeasureCalculator<TypedSpan>(new HierarchicalMatchingsCounter<TypedSpan>(
                            (MatchingsSearcher<TypedSpan>) MatchingFactory
                                    .createSpanMatchingsSearcher(configuration.matching),
                            globalClassifier, inferencer)),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case OKE_Task1: {
            ExperimentTaskConfiguration subTaskConfig;
            List<SubTaskEvaluator<TypedNamedEntity>> evaluators = new ArrayList<SubTaskEvaluator<TypedNamedEntity>>();

            UriKBClassifier okeClassifierTask1 = new ExactWhiteListBasedUriKBClassifier(
                    Arrays.asList("http://www.ontologydesignpatterns.org/ont/d0.owl#Location",
                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization",
                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ERec, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ELink, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ELink, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ETyping, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ETyping, subTaskConfig, dataset,
                            globalRetriever, okeClassifierTask1, inferencer)));
            return new ConfidenceScoreEvaluatorDecorator<TypedNamedEntity>(
                    new SubTaskAverageCalculator<TypedNamedEntity>(evaluators), FMeasureCalculator.MICRO_F1_SCORE_NAME,
                    new DoubleResultComparator());
        }
        case OKE_Task2: {
            ExperimentTaskConfiguration subTaskConfig;
            List<SubTaskEvaluator<TypedNamedEntity>> evaluators = new ArrayList<SubTaskEvaluator<TypedNamedEntity>>();
            String classTypes[] = new String[] { RDFS.Class.getURI(), OWL.Class.getURI() };

            UriKBClassifier okeClassifierTask2 = new SimpleWhiteListBasedUriKBClassifier(
                    "http://www.ontologydesignpatterns.org/ont/");

            // sub task 1, find the correct type of the entity (use only
            // entities, without a class type!)
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ETyping, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    new MarkingFilteringEvaluatorDecorator<>(
                            new TypeBasedMarkingFilter<TypedNamedEntity>(false, classTypes),
                            (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ETyping, subTaskConfig,
                                    dataset, globalRetriever, okeClassifierTask2, inferencer))));
            // sub task 2, find the correct position of the type in the text
            // (use only entities with a class type!)
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    new MarkingFilteringEvaluatorDecorator<>(
                            new TypeBasedMarkingFilter<TypedNamedEntity>(true, classTypes),
                            new SpanMergingEvaluatorDecorator<>((Evaluator<TypedNamedEntity>) createEvaluator(
                                    ExperimentType.ERec, subTaskConfig, dataset)))));

            return new ConfidenceScoreEvaluatorDecorator<TypedNamedEntity>(
                    new SubTaskAverageCalculator<TypedNamedEntity>(evaluators), FMeasureCalculator.MICRO_F1_SCORE_NAME,
                    new DoubleResultComparator());
        }
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    protected void addSubTaskEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        ExperimentTaskConfiguration subTaskConfig;
        switch (configuration.type) {
        case ERec:
        case ELink:
        case ETyping:
        case D2KB:
        case C2KB:
        case OKE_Task1:
        case OKE_Task2: {
            return;
        }
        case Sa2KB: // falls through
        case A2KB:
        case EExt: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.ERec, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ELink, Matching.STRONG_ENTITY_MATCH);
            // evaluators.add(createEvaluator(ExperimentType.ELink,
            // configuration, dataset));
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.ELink, subTaskConfig, dataset)));
            return;
        }
        default: {
            throw new RuntimeException();
        }
        }
    }

    protected SameAsRetriever getSameAsRetriever(Dataset dataset) {
        SameAsRetriever localRetriever = DatasetBasedSameAsRetriever.create(dataset);
        if (localRetriever != null) {
            if (globalRetriever != null) {
                return new MultipleSameAsRetriever(localRetriever, globalRetriever);
            } else {
                return localRetriever;
            }
        } else {
            return globalRetriever;
        }
    }

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
}
