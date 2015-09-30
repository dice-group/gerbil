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

import org.aksw.gerbil.datatypes.ExperimentType;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.systemPlugins.SpotlightAnnotator;
import it.acubelab.batframework.utils.WikipediaApiInterface;

public class SpotlightAnnotatorConfig extends AbstractAnnotatorConfiguration {
    
    public static final String ANNOTATOR_NAME = "DBpedia Spotlight";

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpApi;

    public SpotlightAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpApi) {
        super(ANNOTATOR_NAME, true, new ExperimentType[] { ExperimentType.Sa2KB });
        this.wikiApi = wikiApi;
        this.dbpApi = dbpApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        return new SpotlightAnnotator(dbpApi, wikiApi);
    }

}
