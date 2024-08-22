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
package org.aksw.gerbil.evaluate;

/**
 * An evaluation result is a simple name-value pair. The name is expected to be
 * a String while the value is parameterized.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @param <T>
 */
public interface EvaluationResult<T> {

    /**
     * The result's name.
     */
    public String getName();

    /**
     * The result's value.
     */
    public T getValue();
}
