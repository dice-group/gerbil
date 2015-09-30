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
package org.aksw.gerbil.matching;

import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.StrongAnnotationMatch;
import it.acubelab.batframework.metrics.StrongTagMatch;
import it.acubelab.batframework.metrics.WeakAnnotationMatch;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.datatypes.ExperimentType;

public class MatchingFactory {

    public static MatchRelation<? extends Tag> createMatchRelation(WikipediaApiInterface wikiApi, Matching matching,
            ExperimentType type) {
        switch (matching) {
        case WEAK_ANNOTATION_MATCH: {
            // this is for Sa2KB, A2KB
            if (type.equalsOrContainsType(ExperimentType.A2KB)) {
                return new WeakAnnotationMatch(wikiApi);
            }
            break;
        }
        case STRONG_ANNOTATION_MATCH: {
            // this is for Sa2KB, A2KB and D2KB
            if (type.equalsOrContainsType(ExperimentType.D2KB)) {
                return new StrongAnnotationMatch(wikiApi);
            }
            break;
        }
        case STRONG_ENTITY_MATCH: {
            // this is for Sc2KB, Rc2KB and C2KB
            if (ExperimentType.Sc2KB.equalsOrContainsType(type)) {
                return new StrongTagMatch(wikiApi);
            }
            break;
        }
        }
        return null;
    }
}
