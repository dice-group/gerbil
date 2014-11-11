//   NERD Annotator - It triggers queries to the NERD framework 
// 	 http://nerd.eurecom.fr and it parses the results
//
//   Copyright 2014 EURECOM
//
//   Authors:
//      Giuseppe Rizzo <giuse.rizzo@gmail.com>
//
//   Licensed under ...

package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.NERDAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class NERDAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "NERD-ML";
    private static final String NERD_WEB_SERVICE_KEY_PROPERTY_NAME = "org.aksw.gerbil.annotators.nerd.Key";

    private WikipediaApiInterface wikiApi;

    /**
     * Shouldn't be used until we have finished porting the project to Spring.
     */
    @Deprecated
    public NERDAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
    }

    public NERDAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        // Load and use the key if there is one
        String key = GerbilConfiguration.getInstance().getString(NERD_WEB_SERVICE_KEY_PROPERTY_NAME);
        if (key == null) {
            throw new GerbilException("Couldn't load the NERD API key from properties file.",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        } else {
            return new NERDAnnotator(wikiApi, key);
        }
    }

}
