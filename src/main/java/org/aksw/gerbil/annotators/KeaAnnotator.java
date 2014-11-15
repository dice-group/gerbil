/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.utils.AnnotationException;

import java.util.HashSet;

import org.aksw.gerbil.annotations.GerbilAnnotator;
import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GerbilAnnotator(name = "Kea", couldBeCached = true, applicableForExperiments = ExperimentType.Sa2W)
public class KeaAnnotator extends NIFBasedAnnotatorWebservice {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeaAnnotator.class);

    public static final String ANNOTATOR_NAME = "Kea";

    private static final String ANNOTATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.annotationUrl";
    private static final String DISAMBIGATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.disambiguationUrl";
    private static final String USER_NAME_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.user";
    private static final String PASSWORD_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.password";

    private String disambiguationUrl;
    private String annotationUrl;

    public KeaAnnotator() throws GerbilException {
        super("", ANNOTATOR_NAME);
        disambiguationUrl = GerbilConfiguration.getInstance().getString(DISAMBIGATION_URL_PROPERTY_KEY);
        if (disambiguationUrl == null) {
            throw new GerbilException("Couldn't load property \"" + DISAMBIGATION_URL_PROPERTY_KEY + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        annotationUrl = GerbilConfiguration.getInstance().getString(ANNOTATION_URL_PROPERTY_KEY);
        if (annotationUrl == null) {
            throw new GerbilException("Couldn't load property \"" + ANNOTATION_URL_PROPERTY_KEY + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        annotationUrl = annotationUrl.replace("http://", "");
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
        url.append(annotationUrl);
        annotationUrl = url.toString();
        this.url = annotationUrl;
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        this.url = annotationUrl;
        return super.solveA2W(text);
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        this.url = annotationUrl;
        return super.solveC2W(text);
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
        this.url = disambiguationUrl;
        return super.solveD2W(text, mentions);
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
        this.url = annotationUrl;
        return super.solveSa2W(text);
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
        this.url = annotationUrl;
        return super.solveSc2W(text);
    }
}
