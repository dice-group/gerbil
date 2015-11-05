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
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.MatchingsSearcherFactory;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.MeaningMatchingsSearcher;
import org.aksw.gerbil.semantic.kb.ExactWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
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

    protected UriKBClassifier globalClassifier = null;
    protected SubClassInferencer inferencer = null;

    public EvaluatorFactory() {
        this(null, null);
    }

    private static String[] loadDefaultKBs() {
        String kbs[] = GerbilConfiguration.getInstance().getStringArray(DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY);
        if (kbs == null) {
            LOGGER.error("Couldn't load the list of well known KBs. This GERBIL instance might not work as expected!");
        }
        return kbs;
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier) {
        this(globalClassifier, null);
    }

    public EvaluatorFactory(SubClassInferencer inferencer) {
        this(null, inferencer);
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
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
        return createEvaluator(type, configuration, dataset, globalClassifier, inferencer);
    }

    @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        switch (type) {
        case C2KB: {
            return new ConfidenceScoreEvaluatorDecorator<Meaning>(
                    new InKBClassBasedFMeasureCalculator<Meaning>(
                            new MeaningMatchingsSearcher<Meaning>(globalClassifier), globalClassifier),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case Sa2KB:
        case A2KB: {
            return new ConfidenceScoreEvaluatorDecorator<NamedEntity>(
                    new FMeasureCalculator<NamedEntity>(
                            new MatchingsCounterImpl<NamedEntity>(new CompoundMatchingsCounter<NamedEntity>(
                                    (MatchingsSearcher<NamedEntity>) MatchingsSearcherFactory
                                            .createSpanMatchingsSearcher(configuration.matching),
                                    new MeaningMatchingsSearcher<NamedEntity>(globalClassifier)))),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case ERec: {
            return new ConfidenceScoreEvaluatorDecorator<Span>(
                    new FMeasureCalculator<Span>(
                            new MatchingsCounterImpl<Span>((MatchingsSearcher<Span>) MatchingsSearcherFactory
                                    .createSpanMatchingsSearcher(configuration.matching))),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case D2KB: {
            return new ConfidenceScoreEvaluatorDecorator<NamedEntity>(
                    new InKBClassBasedFMeasureCalculator<NamedEntity>(new CompoundMatchingsCounter<NamedEntity>(
                            (MatchingsSearcher<NamedEntity>) MatchingsSearcherFactory
                                    .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(globalClassifier)), globalClassifier),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case ETyping: {
            return new ConfidenceScoreEvaluatorDecorator<TypedSpan>(
                    new HierarchicalFMeasureCalculator<TypedSpan>(new HierarchicalMatchingsCounter<TypedSpan>(
                            (MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
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
                    ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.D2KB, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ETyping, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ETyping, subTaskConfig, dataset,
                            okeClassifierTask1, inferencer)));
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
                                    dataset, okeClassifierTask2, inferencer))));
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
        case D2KB:
        case ETyping:
        case C2KB:
            // Since the OKE challenge tasks are using the results of their
            // subtasks, the definition of subtasks is part of their evaluation
            // creation
        case OKE_Task1:
        case OKE_Task2: {
            return;
        }
        case Sa2KB: // falls through
        case A2KB: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.ERec, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
            // evaluators.add(createEvaluator(ExperimentType.ELink,
            // configuration, dataset));
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.D2KB, subTaskConfig, dataset)));
            return;
        }
        default: {
            throw new RuntimeException();
        }
        }
    }

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
}
