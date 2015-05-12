package org.aksw.gerbil.evaluate;

import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.InKBClassBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingFactory;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.MatchingsSearcher;
import org.aksw.gerbil.matching.impl.MeaningMatchingsSearcher;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.DatasetBasedSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.MultipleSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class EvaluatorFactory {

    protected SameAsRetriever globalRetriever = null;
    protected UriKBClassifier globalClassifier = null;

    public EvaluatorFactory() {
    }

    public EvaluatorFactory(SameAsRetriever globalRetriever) {
        this.globalRetriever = globalRetriever;
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier) {
        this.globalClassifier = globalClassifier;
    }

    public EvaluatorFactory(SameAsRetriever globalRetriever, UriKBClassifier globalClassifier) {
        this.globalRetriever = globalRetriever;
        this.globalClassifier = globalClassifier;
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    protected <T extends Marking> Evaluator<T> createEvaluator(ExperimentType type,
            ExperimentTaskConfiguration configuration, Dataset dataset) {
        switch (type) {
        case Sa2KB:
        case A2KB:
        case EExt: {
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return (Evaluator<T>) new FMeasureCalculator<NamedEntity>(new MatchingsCounterImpl<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>((MatchingsSearcher<NamedEntity>) MatchingFactory
                            .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(globalClassifier, globalRetriever,
                                    localRetriever, null))));
        }
        case ERec: {
            return (Evaluator<T>) new FMeasureCalculator<Span>(new MatchingsCounterImpl<Span>(
                    (MatchingsSearcher<Span>) MatchingFactory.createSpanMatchingsSearcher(configuration.matching)));
        }
        case D2KB:
        case ELink: { // FIXME define whether the problem reduction to D2KB
                      // should use a weak or strong matching
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return (Evaluator<T>) new InKBClassBasedFMeasureCalculator<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>(
                            (MatchingsSearcher<NamedEntity>) MatchingFactory
                                    .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(globalClassifier, globalRetriever,
                                    localRetriever, null)), globalClassifier);
        }
        default: {
            throw new RuntimeException();
        }
        }
    }

    @SuppressWarnings("deprecation")
    protected void addSubTaskEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        ExperimentTaskConfiguration subTaskConfig;
        switch (configuration.type) {
        case ERec:
        case ELink:
        case D2KB:
        case OKE_Task1:
        case OKE_Task2: {
            return;
        }
        case Sa2KB: // falls through
        case A2KB:
        case EExt: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.ERec, subTaskConfig,
                    dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ELink, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(createEvaluator(ExperimentType.ELink, configuration, dataset));
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

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration, Dataset dataset) {
        evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
}
