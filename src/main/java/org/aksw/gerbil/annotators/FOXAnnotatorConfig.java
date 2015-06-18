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
import org.aksw.gerbil.annotator.impl.fox.FOXAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

@Deprecated
public class FOXAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final String FOX_SERVICE_URL_PARAMETER_KEY = "org.aksw.gerbil.annotators.FOXAnnotatorConfig.serviceUrl";

    public FOXAnnotatorConfig() {
        super(FOXAnnotator.NAME, true, ExperimentType.OKE_Task1);
    }

    @Override
    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
        String serviceUrl = GerbilConfiguration.getInstance().getString(FOX_SERVICE_URL_PARAMETER_KEY);
        if (serviceUrl == null) {
            throw new GerbilException("Couldn't load the needed property \"" + FOX_SERVICE_URL_PARAMETER_KEY + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        return new FOXAnnotator(serviceUrl);
    }
}
