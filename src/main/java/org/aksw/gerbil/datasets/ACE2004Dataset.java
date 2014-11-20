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
package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.annotations.GerbilDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.springframework.beans.factory.annotation.Autowired;

@GerbilDataset(name = "ACE2004", couldBeCached = true, applicableForExperiments = ExperimentType.Sa2W)
public class ACE2004Dataset extends AbstractA2WDatasetDecorator {

    private static final String ACE2004_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.TextsFolder";
    private static final String ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.AnnotationsFolder";

    @Autowired
    private WikipediaApiInterface wikiAPI;

    public ACE2004Dataset() {
    }

    @Override
    public A2WDataset getDataset() throws GerbilException {
        A2WDataset annotator = super.getDataset();
        if (annotator == null) {
            String textsFolder = GerbilConfiguration.getInstance().getString(ACE2004_TEXTS_FOLDER_PROPERTY_NAME);
            if (textsFolder == null) {
                throw new GerbilException("Couldn't load needed Property \"" + ACE2004_TEXTS_FOLDER_PROPERTY_NAME
                        + "\".", ErrorTypes.DATASET_LOADING_ERROR);
            }
            String annotationsFolder = GerbilConfiguration.getInstance().getString(
                    ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME);
            if (annotationsFolder == null) {
                throw new GerbilException("Couldn't load needed Property \"" + ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME
                        + "\".", ErrorTypes.DATASET_LOADING_ERROR);
            }
            try {
                annotator = new it.acubelab.batframework.datasetPlugins.ACE2004Dataset(textsFolder, annotationsFolder,
                        wikiAPI);
            } catch (Exception e) {
                throw new GerbilException("Couldn't load dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
            }
            setDataset(annotator);
        }
        return annotator;
    }
}
