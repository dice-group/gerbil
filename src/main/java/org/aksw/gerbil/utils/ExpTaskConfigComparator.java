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

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

/**
 * A Comparator that sorts the {@link ExperimentTaskConfiguration} ascending
 * regarding the dataset name and if the dataset names are equal, ascending
 * regarding the annotator name.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ExpTaskConfigComparator implements Comparator<ExperimentTaskConfiguration> {

    @Override
    public int compare(ExperimentTaskConfiguration config1, ExperimentTaskConfiguration config2) {
        int diff = config1.datasetConfig.getName().compareTo(config2.datasetConfig.getName());
        if (diff == 0) {
            return config1.annotatorConfig.getName().compareTo(config2.annotatorConfig.getName());
        }
        return diff;
    }

}
