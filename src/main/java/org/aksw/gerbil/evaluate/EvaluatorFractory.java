package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.MeaningMatchingsCounter;
import org.aksw.gerbil.matching.impl.StrongSpanMatchingsCounter;
import org.aksw.gerbil.matching.impl.WeakSpanMatchingsCounter;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class EvaluatorFractory {

    @SuppressWarnings("unchecked")
    public static <T extends Marking> Evaluator<T> createEvaluators(ExperimentTaskConfiguration configuration) {
        switch (configuration.type) {
        case Sa2KB:
        case A2KB:
        case EExt: {
            return (Evaluator<T>) new FMeasureCalculator<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>(
                            configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatchingsCounter<NamedEntity>()
                                    : new StrongSpanMatchingsCounter<NamedEntity>(),
                            new MeaningMatchingsCounter<NamedEntity>()));
        }
        case ERec: {
            return (Evaluator<T>) new FMeasureCalculator<Span>(
                    configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatchingsCounter<Span>()
                            : new StrongSpanMatchingsCounter<Span>());
        }
        case D2KB:
        case ELink: { // FIXME define whether the problem reduction to D2KB
                      // should use a weak or strong matching
            return (Evaluator<T>) new FMeasureCalculator<NamedEntity>(
                    new CompoundMatchingsCounter<NamedEntity>(
                            configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatchingsCounter<NamedEntity>()
                                    : new StrongSpanMatchingsCounter<NamedEntity>(),
                            new MeaningMatchingsCounter<NamedEntity>()));
        }
        default: {
            throw new RuntimeException();
        }
        }
    }
}
