package org.aksw.gerbil.matching;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.metrics.MatchRelation;

public class MatchingFactory {

    public static MatchRelation<Annotation> createMatchRelation(Matching metric) {
        switch (metric) {
        case WEAK_ANNOTATION_MATCH:
            return null; // TODO
        case STRONG_ANNOTATION_MATCH:
            return null; // TODO
        case CONCEPT_ANNOTATION_MATCH:
            return null; // TODO
        case MENTION_ANNOTATION_MATCH:
            return null; // TODO
        }
        return null;
    }
}
