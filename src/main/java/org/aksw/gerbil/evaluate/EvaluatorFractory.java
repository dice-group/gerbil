package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.StrongSpanMatching;
import org.aksw.gerbil.matching.impl.WeakSpanMatching;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class EvaluatorFractory {

    public static Evaluator createEvaluators(ExperimentTaskConfiguration configuration) {
        switch (configuration.type) {
        case Sa2KB:
        case A2KB:
        case EntityExtraction: {
            return new FMeasureCalculator<NamedEntity>(
                    configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatching<NamedEntity>()
                            : new StrongSpanMatching<NamedEntity>());
        }
        case EntityRecognition: {
            return new FMeasureCalculator<Span>(
                    configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatching<Span>()
                            : new StrongSpanMatching<Span>());
        }
        case D2KB:
        case EntityLinking: {
            return new FMeasureCalculator<NamedEntity>(
                    configuration.matching == Matching.WEAK_ANNOTATION_MATCH ? new WeakSpanMatching<NamedEntity>()
                            : new StrongSpanMatching<NamedEntity>());
        }
        default: {
            throw new RuntimeException();
        }
        }
    }
}
