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
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.GSInKBClassifyingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.HierarchicalFMeasureCalculator;
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
import org.aksw.gerbil.matching.impl.StrongSpanMatchingsSearcher;
import org.aksw.gerbil.matching.impl.clas.EmergingEntityMeaningClassifier;
import org.aksw.gerbil.matching.impl.clas.UriBasedMeaningClassifier;
import org.aksw.gerbil.semantic.kb.ExactWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.utils.filter.TypeBasedMarkingFilter;
import org.aksw.gerbil.web.config.RootConfig;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;


@SuppressWarnings("deprecation")
public class EvaluatorFactory {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(EvaluatorFactory.class);

    protected UriKBClassifier globalClassifier = null;
    protected SubClassInferencer inferencer = null;

    public EvaluatorFactory() {
        this(null, null);
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
            this.globalClassifier = RootConfig.createDefaultUriKBClassifier();
        }
        if (inferencer != null) {
            this.inferencer = inferencer;
        } else {
            this.inferencer = new SimpleSubClassInferencer(ModelFactory.createDefaultModel());
        }
    }

    @SuppressWarnings("rawtypes")
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset) {
        return createEvaluator(type, configuration, dataset, globalClassifier, inferencer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier classifier, SubClassInferencer inferencer) {
        switch (type) {
        case C2KB: {
            return new ClassifyingEvaluatorDecorator<Meaning, ClassifiedMeaning>(
                    new ClassConsideringFMeasureCalculator<ClassifiedMeaning>(
                            new MatchingsCounterImpl<ClassifiedMeaning>(new ClassifiedMeaningMatchingsSearcher()),
                            MarkingClasses.IN_KB, MarkingClasses.EE), new UriBasedMeaningClassifier<ClassifiedMeaning>(
                            classifier, MarkingClasses.IN_KB), new EmergingEntityMeaningClassifier<ClassifiedMeaning>());
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
            return new ConfidenceBasedFMeasureCalculator<Span>(new MatchingsCounterImpl<Span>(
                    (MatchingsSearcher<Span>) MatchingsSearcherFactory
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
                                                                    .createSpanMatchingsSearcher(configuration.matching),
                                                            new ClassifiedMeaningMatchingsSearcher<ClassifiedSpanMeaning>())),
                                            MarkingClasses.IN_KB, MarkingClasses.EE, MarkingClasses.GS_IN_KB),
                                    new StrongSpanMatchingsSearcher<ClassifiedSpanMeaning>()),
                            new UriBasedMeaningClassifier<ClassifiedSpanMeaning>(classifier, MarkingClasses.IN_KB),
                            new EmergingEntityMeaningClassifier<ClassifiedSpanMeaning>()), true);
        }
        case ETyping: {
            return new SearcherBasedNotMatchingMarkingFilter<TypedSpan>(
                    new StrongSpanMatchingsSearcher<TypedSpan>(),
                    new ConfidenceScoreEvaluatorDecorator<TypedSpan>(new HierarchicalFMeasureCalculator<TypedSpan>(
                            new HierarchicalMatchingsCounter<TypedSpan>(
                                    (MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
                                            .createSpanMatchingsSearcher(configuration.matching), classifier,
                                    inferencer)), FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator()),
                    true);
        }
        case RT2KB: {
            return new ConfidenceScoreEvaluatorDecorator<TypedSpan>(new HierarchicalFMeasureCalculator<TypedSpan>(
                    new HierarchicalMatchingsCounter<TypedSpan>((MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
                            .createSpanMatchingsSearcher(configuration.matching), classifier, inferencer)),
                    FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator());
        }
        case OKE_Task1: {
            ExperimentTaskConfiguration subTaskConfig;
            List<SubTaskEvaluator<TypedNamedEntity>> evaluators = new ArrayList<SubTaskEvaluator<TypedNamedEntity>>();

            UriKBClassifier okeClassifierTask1 = new ExactWhiteListBasedUriKBClassifier(Arrays.asList(
                    "http://www.ontologydesignpatterns.org/ont/d0.owl#Location",
                    "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization",
                    "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                    "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, (Evaluator<TypedNamedEntity>) createEvaluator(
                    ExperimentType.ERec, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, (Evaluator<TypedNamedEntity>) createEvaluator(
                    ExperimentType.D2KB, subTaskConfig, dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ETyping, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, (Evaluator<TypedNamedEntity>) createEvaluator(
                    ExperimentType.ETyping, subTaskConfig, dataset, okeClassifierTask1, inferencer)));
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
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, new MarkingFilteringEvaluatorDecorator<>(
                    new TypeBasedMarkingFilter<TypedNamedEntity>(false, classTypes),
                    (Evaluator<TypedNamedEntity>) createEvaluator(ExperimentType.ETyping, subTaskConfig, dataset,
                            okeClassifierTask2, inferencer))));
            // sub task 2, find the correct position of the type in the text
            // (use only entities with a class type!)
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, new MarkingFilteringEvaluatorDecorator<>(
                    new TypeBasedMarkingFilter<TypedNamedEntity>(true, classTypes),
                    new SpanMergingEvaluatorDecorator<>((Evaluator<TypedNamedEntity>) createEvaluator(
                            ExperimentType.ERec, subTaskConfig, dataset)))));

            return new ConfidenceScoreEvaluatorDecorator<TypedNamedEntity>(
                    new SubTaskAverageCalculator<TypedNamedEntity>(evaluators), FMeasureCalculator.MICRO_F1_SCORE_NAME,
                    new DoubleResultComparator());
        }
        case RE:
        	return new ConfidenceBasedFMeasureCalculator<Relation>(new MatchingsCounterImpl<Relation>(
        			 new EqualsBasedMatchingsSearcher<Relation>()));
        case OKE2018Task4:
            ExperimentTaskConfiguration subTaskConfig;
            List<SubTaskEvaluator> evaluators = new ArrayList<SubTaskEvaluator>();
            
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.RE, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new ClassSubTaskEvaluator<>(subTaskConfig, (Evaluator<Marking>) createEvaluator(
                    ExperimentType.RE, subTaskConfig, dataset), Relation.class));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.A2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new ClassSubTaskEvaluator<Meaning>(subTaskConfig, (Evaluator<Meaning>) createEvaluator(
                    ExperimentType.A2KB, subTaskConfig, dataset, classifier,inferencer ), Meaning.class));
            
            
            return new ConfidenceScoreEvaluatorDecorator(
                    new SubTaskAverageCalculator(evaluators), FMeasureCalculator.MICRO_F1_SCORE_NAME,
                    new DoubleResultComparator());
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
        case ERec: // falls through
        case D2KB:
        case ETyping:
        case C2KB:
        case RE:
        case OKE2018Task4:
       
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
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.ERec, subTaskConfig,
                    dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
            // evaluators.add(createEvaluator(ExperimentType.ELink,
            // configuration, dataset));
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.D2KB, subTaskConfig,
                    dataset)));
            return;
        }
        case RT2KB: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.ERec, subTaskConfig,
                    dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ETyping, Matching.STRONG_ENTITY_MATCH);
            // evaluators.add(createEvaluator(ExperimentType.ELink,
            // configuration, dataset));
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.ETyping, subTaskConfig,
                    dataset)));
            return;
        }

        default: {
            throw new RuntimeException();
        }
        }
    }

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration, Dataset dataset) {
        evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
}
