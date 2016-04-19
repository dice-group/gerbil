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
package org.aksw.gerbil.matching.impl;

import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Meaning;

/**
 * This implementation of a {@link MatchingsSearcher} searches for a matching
 * meaning for every given expected meaning and a list of annotator results.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 * @param <T>
 *            is the {@link Meaning} class or one of its extensions.
 */
public class ClassifierBasedMeaningMatchingsSearcher<T extends Meaning> extends AbstractMeaningMatchingsSearcher<T> {

    private UriKBClassifier uriKBClassifier;

    public ClassifierBasedMeaningMatchingsSearcher() {
    }

    public ClassifierBasedMeaningMatchingsSearcher(UriKBClassifier uriKBClassifier) {
        this.uriKBClassifier = uriKBClassifier;
    }

    @Override
    protected boolean hasKbUri(T meaning) {
        return (uriKBClassifier == null) ? true : uriKBClassifier.containsKBUri(meaning.getUris());
    }
}
