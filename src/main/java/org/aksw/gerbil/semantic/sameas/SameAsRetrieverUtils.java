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
package org.aksw.gerbil.semantic.sameas;

import java.util.Collection;

import org.aksw.gerbil.datatypes.marking.MeaningsContainingMarking;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Relation;

public class SameAsRetrieverUtils {

	public static void addSameURIsToMarkings(SameAsRetriever retriever, Collection<? extends Marking> markings) {
		for (Marking marking : markings) {
			if (marking instanceof Meaning) {
				retriever.addSameURIs(((Meaning) marking).getUris());
			} else if (marking instanceof MeaningsContainingMarking) {
				addSameURIsToMeanings(retriever, ((MeaningsContainingMarking) marking).getMeanings());
			} else if (marking instanceof Relation) {
				addSameURIsToRelation(retriever, (Relation) marking);
			}
		}
	}

	public static void addSameURIsToRelation(SameAsRetriever retriever, Relation relation) {
		retriever.addSameURIs(relation.getSubject().getUris());
		retriever.addSameURIs(relation.getPredicate().getUris());
		retriever.addSameURIs(relation.getObject().getUris());

	}


	public static void addSameURIsToMeanings(SameAsRetriever retriever, Collection<? extends Meaning> meanings) {
		for (Meaning meaning : meanings) {
			retriever.addSameURIs(meaning.getUris());
		}
	}
}
