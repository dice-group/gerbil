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
package org.aksw.gerbil.utils;

import java.util.Comparator;

import org.aksw.gerbil.datatypes.ExperimentType;

public class ExperimentTypeComparator implements Comparator<ExperimentType> {

    @Override
    public int compare(ExperimentType type1, ExperimentType type2) {
        // if type1 >= type2
        if (type1.equalsOrContainsType(type2)) {
            // if type2 >= type1
            if (type2.equalsOrContainsType(type1)) {
                return type1.name().compareTo(type2.name());
            } else {
                return 1;
            }
        } else {
            // if type2 >= type1
            if (type2.equalsOrContainsType(type1)) {
                return -1;
            } else {
                return type1.name().compareTo(type2.name());
            }
        }
    }

}
