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
package org.aksw.gerbil.datatypes.marking;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;

public class ClassifiedMarkingFactory {

    public static ClassifiedMarking createClassifiedMeaning(Marking marking) {
        if (marking instanceof ScoredNamedEntity) {
            ScoredNamedEntity sne = (ScoredNamedEntity) marking;
            return new ClassifiedScoredNamedEntity(sne.getStartPosition(), sne.getLength(), sne.getUris(),
                    sne.getConfidence());
        } else if (marking instanceof MeaningSpan) {
            MeaningSpan ne = (MeaningSpan) marking;
            return new ClassifiedNamedEntity(ne.getStartPosition(), ne.getLength(), ne.getUris());
        } else if (marking instanceof Meaning) {
            return new ClassifiedAnnotation(((Meaning) marking).getUris());
        }
        return null;
    }
}
