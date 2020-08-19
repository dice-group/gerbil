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
package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskConfiguration {

    public AnnotatorConfiguration annotatorConfig;
    public DatasetConfiguration datasetConfig;
    public ExperimentType type;
    public Matching matching;
    public String language;

    public ExperimentTaskConfiguration(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
                                       ExperimentType type, String language) {
        super();
        this.annotatorConfig = annotatorConfig;
        this.datasetConfig = datasetConfig;
        this.type = type;
        this.language = language;
      //  this.matching = matching;
    }

    public AnnotatorConfiguration getAnnotatorConfig() {
        return annotatorConfig;
    }

    public void setAnnotatorConfig(AnnotatorConfiguration annotatorConfig) {
        this.annotatorConfig = annotatorConfig;
    }

    public DatasetConfiguration getDatasetConfig() {
        return datasetConfig;
    }

    public void setDatasetConfig(DatasetConfiguration datasetConfig) {
        this.datasetConfig = datasetConfig;
    }

    public ExperimentType getType() {
        return type;
    }

    public void setType(ExperimentType type) {
        this.type = type;
    }

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

    @Override
    public String toString() {
        return "eTConfig(\"" + annotatorConfig.getName() + "\",\"" + datasetConfig.getName() + "\",\"" + type.name() + "\")";
    }
}
