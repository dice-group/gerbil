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

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.bat.datasets.Microposts2014Dataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

/**
 * ...
 *
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class Microposts2014Config extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME_START = "Microposts2014";   
    private static final String DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.Microposts2014DatasetConfig";
    
    private Microposts2014Chunk chunk;
    private WikipediaApiInterface wikiApi;

    public static enum Microposts2014Chunk {
        TRAIN, TEST
    }
    
	public Microposts2014Config(	
									Microposts2014Chunk chunk, 
									WikipediaApiInterface wikiApi
						 	    ) 
	{
	    super(DATASET_NAME_START, true, ExperimentType.Sa2W);
	    this.chunk = chunk;
        this.wikiApi = wikiApi;
        // Set the correct name
        switch (chunk) {
        case TRAIN: {
            setName(getName() + "-Train");
            break;
        }
        case TEST: {
            setName(getName() + "-Test");
            break;
        }
        }
	}

	@Override
	protected TopicDataset loadDataset() throws Exception {
        switch (chunk) {
        case TRAIN: {
    		String file = GerbilConfiguration.getInstance().getString(DATASET_FILE_PROPERTY_NAME.concat(".Train"));
            if (file == null) {
                throw new IOException("Couldn't load needed Property \"" + DATASET_FILE_PROPERTY_NAME + "\".");
            }
            return new Microposts2014Dataset(file, wikiApi);
        }
        case TEST: {
        	String file = GerbilConfiguration.getInstance().getString(DATASET_FILE_PROPERTY_NAME.concat(".Test"));
            if (file == null) {
                throw new IOException("Couldn't load needed Property \"" + DATASET_FILE_PROPERTY_NAME + "\".");
            }        	
            return new Microposts2014Dataset(file, wikiApi);
        }
        }
        return null;
	}

}
