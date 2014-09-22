package org.aksw.gerbil.matching;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.metrics.ConceptAnnotationMatch;
import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.StrongAnnotationMatch;
import it.acubelab.batframework.metrics.WeakAnnotationMatch;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.datatypes.ExperimentType;

public class MatchingFactory {

    public static MatchRelation<Annotation> createMatchRelation(WikipediaApiInterface wikiApi, Matching matching,
            ExperimentType type) {
        switch (matching) {
        case WEAK_ANNOTATION_MATCH: {
            // this is for Sa2W, A2W
            if (type.equalsOrContainsType(ExperimentType.A2W)) {
                return new WeakAnnotationMatch(wikiApi);
            }
            break;
        }
        case STRONG_ANNOTATION_MATCH: {
            // this is for Sa2W, A2W and D2W
            if (type.equalsOrContainsType(ExperimentType.D2W)) {
                return new StrongAnnotationMatch(wikiApi);
            }
            break;
        }
        case STRONG_ENTITY_MATCH: {
            // this is for Sc2W, Rc2W and C2W
            if (ExperimentType.Sc2W.equalsOrContainsType(type)) {
                return new ConceptAnnotationMatch(wikiApi);
            }
            break;
        }
        }
        return null;
    }
}
