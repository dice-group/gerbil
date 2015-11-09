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
package org.aksw.gerbil.utils.filter;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

public interface MarkingFilter<T extends Marking> {

    /**
     * Returns true if the marking is good and does not have to be filtered out.
     * 
     * @param marking
     * @return
     */
    public boolean isMarkingGood(T marking);

    /**
     * Returns a filtered list based on the given list.
     * 
     * @param markings
     * @return
     */
    public List<T> filterList(List<T> markings);

    public List<List<T>> filterListOfLists(List<List<T>> markings);
}
