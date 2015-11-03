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
package org.aksw.gerbil.annotator;

import org.aksw.gerbil.datatypes.AdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public interface AnnotatorConfiguration extends AdapterConfiguration {

    /**
     * Returns the annotator or null if the annotator can't be used for the
     * given {@link ExperimentType}.
     * 
     * @param type
     * @return
     * @throws GerbilException
     *             if an error occurs while loading the annotator
     */
    public Annotator getAnnotator(ExperimentType type) throws GerbilException;
}
