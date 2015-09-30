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

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class EntityclassifierEUConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "Entityclassifier.eu NER";

    private static final String URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.EntityclassifierEUConfig.url";
    private static final String API_KEY_PROPERTY_KEY = "org.aksw.gerbil.annotators.EntityclassifierEUConfig.apiKey";

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public EntityclassifierEUConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2KB);
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String annotatorURL = GerbilConfiguration.getInstance().getString(URL_PROPERTY_KEY);
        if (annotatorURL == null) {
            throw new GerbilException("Couldn't load property \"" + URL_PROPERTY_KEY
                    + "\" containing the URL for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        String apiKey = GerbilConfiguration.getInstance().getString(API_KEY_PROPERTY_KEY);
        if (apiKey == null) {
            throw new GerbilException("Couldn't load property \"" + API_KEY_PROPERTY_KEY
                    + "\" containing the API Key for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        return new NIFBasedAnnotatorWebservice(annotatorURL + apiKey, this.getName(), wikiApi, dbpediaApi);
    }
}
