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

import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.impl.bat.BatFrameworkAnnotatorWrapper;
import org.aksw.gerbil.bat.annotator.AgdistisAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class AgdistisAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "AGDISTIS"
	    + BatFrameworkAnnotatorWrapper.ANNOTATOR_NAME_SUFFIX;

    private static final String AGDISTIS_HOST_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Host";
    private static final String AGDISTIS_PORT_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Port";

    private WikipediaApiInterface wikiApi;

    @SuppressWarnings("deprecation")
    public AgdistisAnnotatorConfig(WikipediaApiInterface wikiApi) {
	super(ANNOTATOR_NAME, true, ExperimentType.D2KB);
	this.wikiApi = wikiApi;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
	String host = GerbilConfiguration.getInstance().getString(
		AGDISTIS_HOST_PROPERTY_NAME);
	if (host == null) {
	    throw new GerbilException("Couldn't load needed property \""
		    + AGDISTIS_HOST_PROPERTY_NAME + "\".",
		    ErrorTypes.ANNOTATOR_LOADING_ERROR);
	}
	String portString = GerbilConfiguration.getInstance().getString(
		AGDISTIS_PORT_PROPERTY_NAME);
	if (portString == null) {
	    throw new GerbilException("Couldn't load needed property \""
		    + AGDISTIS_PORT_PROPERTY_NAME + "\".",
		    ErrorTypes.ANNOTATOR_LOADING_ERROR);
	}
	int port;
	try {
	    port = Integer.parseInt(portString);
	} catch (Exception e) {
	    throw new GerbilException(
		    "Couldn't parse the integer of the property \""
			    + AGDISTIS_PORT_PROPERTY_NAME + "\".", e,
		    ErrorTypes.ANNOTATOR_LOADING_ERROR);
	}
	return BatFrameworkAnnotatorWrapper.create(new AgdistisAnnotator(host,
		port, wikiApi), wikiApi);
    }
}
