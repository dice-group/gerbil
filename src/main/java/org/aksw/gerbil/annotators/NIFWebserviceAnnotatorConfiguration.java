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
package org.aksw.gerbil.annotators;

import it.unipi.di.acube.batframework.problems.TopicSystem;
import it.unipi.di.acube.batframework.systemPlugins.DBPediaApi;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ExperimentType;

public class NIFWebserviceAnnotatorConfiguration extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "NIF-based Web Service";

    private String annotaturURL;
    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public NIFWebserviceAnnotatorConfiguration(String annotaturURL, String annotatorName, boolean couldBeCached,
            WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, ExperimentType... applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.annotaturURL = annotaturURL;
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        return new NIFBasedAnnotatorWebservice(annotaturURL, this.getName(), wikiApi, dbpediaApi);
    }

}
