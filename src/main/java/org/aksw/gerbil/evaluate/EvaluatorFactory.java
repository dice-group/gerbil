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
import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.ClassifiedSpanMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.impl.ClassConsideringFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.ClassifyingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.ConfidenceScoreEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.DoubleResultComparator;
import org.aksw.gerbil.evaluate.impl.EmptyEvaluationAvoidingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.GSInKBClassifyingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.HierarchicalFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.SimpleTypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.SpanMergingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.SubTaskAverageCalculator;
import org.aksw.gerbil.evaluate.impl.filter.MarkingFilteringEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.filter.SearcherBasedNotMatchingMarkingFilter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.MatchingsSearcherFactory;
import org.aksw.gerbil.matching.impl.ClassifiedMeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.CompoundMatchingsSearcher;
import org.aksw.gerbil.matching.impl.EqualsBasedMatchingsSearcher;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.QAMatchingsCounter;
import org.aksw.gerbil.matching.impl.StrongSpanMatchingsSearcher;
import org.aksw.gerbil.matching.impl.clas.EmergingEntityMeaningClassifier;
import org.aksw.gerbil.matching.impl.clas.UriBasedMeaningClassifier;
import org.aksw.gerbil.qa.datatypes.AnswerItemType;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.AnswerType;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.qa.datatypes.Relation;
import org.aksw.gerbil.semantic.kb.ExactWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.utils.filter.TypeBasedMarkingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

@SuppressWarnings("deprecation")
public class EvaluatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorFactory.class);

    private static final String DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY = "org.aksw.gerbil.evaluate.DefaultWellKnownKB";
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier classifier, SubClassInferencer inferencer) {
        switch (type) {
        case C2KB: {
            return new ClassifyingEvaluatorDecorator<Meaning, ClassifiedMeaning>(
                    new ClassConsideringFMeasureCalculator<ClassifiedMeaning>(
                            new MatchingsCounterImpl<ClassifiedMeaning>(new ClassifiedMeaningMatchingsSearcher()),
                            MarkingClasses.IN_KB, MarkingClasses.EE),
                    new UriBasedMeaningClassifier<ClassifiedMeaning>(classifier, MarkingClasses.IN_KB),
                    new EmergingEntityMeaningClassifier<ClassifiedMeaning>());
        }
        case Sa2KB:
        case A2KB: {
            MatchingsSearcher<ClassifiedSpanMeaning> searcher = (MatchingsSearcher<ClassifiedSpanMeaning>) MatchingsSearcherFactory
                    .createSpanMatchingsSearcher(configuration.matching);
            return new ClassifyingEvaluatorDecorator<MeaningSpan, ClassifiedSpanMeaning>(
                    new ClassConsideringFMeasureCalculator<ClassifiedSpanMeaning>(
                            new MatchingsCounterImpl<ClassifiedSpanMeaning>(
                                    new CompoundMatchingsSearcher<ClassifiedSpanMeaning>(searcher,
                                            new ClassifiedMeaningMatchingsSearcher<ClassifiedSpanMeaning>())),
                            MarkingClasses.IN_KB, MarkingClasses.EE, MarkingClasses.GS_IN_KB),
                    new UriBasedMeaningClassifier<ClassifiedSpanMeaning>(classifier, MarkingClasses.IN_KB),
                    new EmergingEntityMeaningClassifier<ClassifiedSpanMeaning>());
        }
        case ERec: {
            return new ConfidenceBasedFMeasureCalculator<Span>(
                    new MatchingsCounterImpl<Span>((MatchingsSearcher<Span>) MatchingsSearcherFactory
                            .createSpanMatchingsSearcher(configuration.matching)));
        }
        case D2KB: {
            return new SearcherBasedNotMatchingMarkingFilter<MeaningSpan>(
                    new StrongSpanMatchingsSearcher<MeaningSpan>(),
                    new ClassifyingEvaluatorDecorator<MeaningSpan, ClassifiedSpanMeaning>(
                            new GSInKBClassifyingEvaluatorDecorator<ClassifiedSpanMeaning>(
                                    new ClassConsideringFMeasureCalculator<ClassifiedSpanMeaning>(
                                            new MatchingsCounterImpl<ClassifiedSpanMeaning>(
                                                    new CompoundMatchingsSearcher<ClassifiedSpanMeaning>(
                                                            (MatchingsSearcher<ClassifiedSpanMeaning>) MatchingsSearcherFactory
                                                                    .createSpanMatchingsSearcher(
                                                                            configuration.matching),
                                                            new ClassifiedMeaningMatchingsSearcher<ClassifiedSpanMeaning>())),
                                            MarkingClasses.IN_KB, MarkingClasses.EE, MarkingClasses.GS_IN_KB),
                                    new StrongSpanMatchingsSearcher<ClassifiedSpanMeaning>()),
                            new UriBasedMeaningClassifier<ClassifiedSpanMeaning>(classifier, MarkingClasses.IN_KB),
                            new EmergingEntityMeaningClassifier<ClassifiedSpanMeaning>()),
                    true);
        }
        case ETyping: {
            return new SearcherBasedNotMatchingMarkingFilter<TypedSpan>(new StrongSpanMatchingsSearcher<TypedSpan>(),
                    new ConfidenceScoreEvaluatorDecorator<TypedSpan>(
                            new HierarchicalFMeasureCalculator<TypedSpan>(new HierarchicalMatchingsCounter<TypedSpan>(
                                    (MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
                                            .createSpanMatchingsSearcher(configuration.matching),
                                    classifier, inferencer)),
                            FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator()),
                    true);
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

        // -------- QA Experiments --------

        case AType: {
            return new SimpleTypeTransformingEvaluatorDecorator<Marking, AnswerType>(
                    new EmptyEvaluationAvoidingEvaluatorDecorator<AnswerType>(new FMeasureCalculator<AnswerType>(
                            new MatchingsCounterImpl<AnswerType>(new EqualsBasedMatchingsSearcher<AnswerType>()))),
                    AnswerType.class);
        }
        case AIT2KB: {
            return new SimpleTypeTransformingEvaluatorDecorator<Marking, Meaning>(
                    new EmptyEvaluationAvoidingEvaluatorDecorator<Meaning>(
                            (Evaluator<Meaning>) createEvaluator(ExperimentType.C2KB, configuration, dataset)),
                    AnswerItemType.class);
        }
        case P2KB: {
            return new SimpleTypeTransformingEvaluatorDecorator<Marking, Meaning>(
                    new EmptyEvaluationAvoidingEvaluatorDecorator<Meaning>(
                            (Evaluator<Meaning>) createEvaluator(ExperimentType.C2KB, configuration, dataset)),
                    Property.class);
        }
        case QA: {
            return new SimpleTypeTransformingEvaluatorDecorator<Marking, AnswerSet>(
                    new FMeasureCalculator<AnswerSet>(new QAMatchingsCounter()), AnswerSet.class);
        }
        case RE2KB: {
            return new SimpleTypeTransformingEvaluatorDecorator<Marking, Relation>(
                    new EmptyEvaluationAvoidingEvaluatorDecorator<Relation>(new FMeasureCalculator<Relation>(
                            new MatchingsCounterImpl<Relation>(new EqualsBasedMatchingsSearcher<Relation>()))),
                    Relation.class);
        }
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }

    @SuppressWarnings({ "unchecked" })
    protected void addSubTaskEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        ExperimentTaskConfiguration subTaskConfig;
        switch (configuration.type) {
        case AIT2KB: // falls through
        case AType:
        case C2KB:
        case D2KB:
        case ERec:
        case ETyping:
        case P2KB:
        case RE2KB:
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
        case QA: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.AIT2KB, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.AIT2KB, subTaskConfig, dataset)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.AType, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.AType, subTaskConfig, dataset)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.C2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    new SimpleTypeTransformingEvaluatorDecorator<Marking, Meaning>(
                            createEvaluator(ExperimentType.C2KB, subTaskConfig, dataset), Meaning.class)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.P2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.P2KB, subTaskConfig, dataset)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.RE2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig,
                    createEvaluator(ExperimentType.RE2KB, subTaskConfig, dataset)));
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
