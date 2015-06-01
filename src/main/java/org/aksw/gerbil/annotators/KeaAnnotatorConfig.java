/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.annotators;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeaAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeaAnnotatorConfig.class);

    public static final String ANNOTATOR_NAME = "Kea";

    private static final String ANNOTATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.annotationUrl";
    private static final String DISAMBIGATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.disambiguationUrl";
    private static final String USER_NAME_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.user";
    private static final String PASSWORD_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.password";

    public KeaAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, ExperimentType.EExt);
    }

    @Override
    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
        String propertyKey;
        // If we need a D2KB system
        if (ExperimentType.ELink.equalsOrContainsType(type)) {
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

        return new NIFBasedAnnotatorWebservice(url.toString(), this.getName());
    }
}
