package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
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
    public <T extends Marking> Evaluator<T> createEvaluator(ExperimentTaskConfiguration configuration, Dataset dataset) {
        switch (configuration.type) {
        case Sa2KB:
        case A2KB:
        case EExt: {
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return (Evaluator<T>) new FMeasureCalculator<NamedEntity>(new MatchingsCounterImpl<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>((MatchingsSearcher<NamedEntity>) MatchingFactory
                            .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(localRetriever, globalClassifier))));
        }
        case ERec: {
            return (Evaluator<T>) new FMeasureCalculator<Span>(new MatchingsCounterImpl<Span>(
                    (MatchingsSearcher<Span>) MatchingFactory.createSpanMatchingsSearcher(configuration.matching)));
        }
        case D2KB:
        case ELink: { // FIXME define whether the problem reduction to D2KB
                      // should use a weak or strong matching
            SameAsRetriever localRetriever = getSameAsRetriever(dataset);
            return (Evaluator<T>) new FMeasureCalculator<NamedEntity>(new MatchingsCounterImpl<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>((MatchingsSearcher<NamedEntity>) MatchingFactory
                            .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(localRetriever, globalClassifier))));
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
}
