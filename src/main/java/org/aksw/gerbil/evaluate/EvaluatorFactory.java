package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.matching.MatchingFactory;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.MatchingsSearcher;
import org.aksw.gerbil.matching.impl.MeaningMatchingsCounter;
import org.aksw.gerbil.semantic.DatasetBasedSameAsRetriever;
import org.aksw.gerbil.semantic.MultipleSameAsRetriever;
import org.aksw.gerbil.semantic.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class EvaluatorFactory {

    protected SameAsRetriever globalRetriever = null;

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
                            new MeaningMatchingsCounter<NamedEntity>(localRetriever))));
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
                            new MeaningMatchingsCounter<NamedEntity>(localRetriever))));
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
