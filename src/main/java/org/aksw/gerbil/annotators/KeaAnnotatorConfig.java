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
import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeaAnnotatorConfig extends AbstractAnnotatorConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeaAnnotatorConfig.class);

	public static final String ANNOTATOR_NAME = "Kea";

	private static final String ANNOTATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.annotationUrl";
	private static final String DISAMBIGATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.disambiguationUrl";
	private static final String USER_NAME_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.user";
	private static final String PASSWORD_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.password";

	private WikipediaApiInterface wikiApi;
	private DBPediaApi dbpediaApi;

	/**
	 * The annotator instance shared by all experiment tasks.
	 */
	private KeaAnnotator instance = null;

	public KeaAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
		super(ANNOTATOR_NAME, true, ExperimentType.Sa2KB);
		this.wikiApi = wikiApi;
		this.dbpediaApi = dbpediaApi;
	}

	@Override
	protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
		if (instance == null) {
			String propertyKey;
			// If this we need a D2KB system
			if (ExperimentType.D2KB.equalsOrContainsType(type)) {
				propertyKey = DISAMBIGATION_URL_PROPERTY_KEY;
			} else {
				propertyKey = ANNOTATION_URL_PROPERTY_KEY;
			}
			String annotatorURL = GerbilConfiguration.getInstance().getString(propertyKey);
			if (annotatorURL == null) {
				throw new GerbilException("Couldn't load property \"" + propertyKey
						+ "\" containing the URL for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
			}
			annotatorURL = annotatorURL.replace("http://", "");
			StringBuilder url = new StringBuilder();
			url.append("http://");

			// load user name and password
			String user = GerbilConfiguration.getInstance().getString(USER_NAME_PROPERTY_KEY);
			String password = GerbilConfiguration.getInstance().getString(PASSWORD_PROPERTY_KEY);
			if ((user != null) && (password != null)) {
				url.append(user);
				url.append(':');
				url.append(password);
				url.append('@');
			} else {
				LOGGER.error("Couldn't load the user name (" + USER_NAME_PROPERTY_KEY + ") or the password property ("
						+ PASSWORD_PROPERTY_KEY + "). It is possbile that this annotator won't work.");
			}
			url.append(annotatorURL);

			instance = new KeaAnnotator(url.toString(), this.getName(), wikiApi, dbpediaApi);
		}
		return instance;
	}

	protected static class KeaAnnotator extends NIFBasedAnnotatorWebservice {

		public KeaAnnotator(String url, String name, WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
			super(url, name, wikiApi, dbpediaApi);
		}

		@Override
		protected synchronized Document request(Document document) {
			return super.request(document);
		}
	}
}
