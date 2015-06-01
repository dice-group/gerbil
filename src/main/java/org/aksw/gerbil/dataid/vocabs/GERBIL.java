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
package org.aksw.gerbil.dataid.vocabs;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class GERBIL {

    private static final Logger LOGGER = LoggerFactory.getLogger(GERBIL.class);

    protected static final String uri = "http://gerbil.aksw.org/gerbil/vocab#";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

    public static final Resource Experiment = resource("Experiment");
    public static final Resource ExperimentTask = resource("ExperimentTask");

    public static final Resource A2KB = resource("A2KB");
    public static final Resource C2KB = resource("C2KB");
    public static final Resource D2KB = resource("D2KB");
    public static final Resource Rc2KB = resource("Rc2KB");
    public static final Resource Sa2KB = resource("Sa2KB");
    public static final Resource Sc2KB = resource("Sc2KB");
    public static final Resource OKE2015_Task1 = resource("OKE2015_Task1");
    public static final Resource OKE2015_Task2 = resource("OKE2015_Task2");

    public static final Resource StrongAnnoMatch = resource("StrongAnnoMatch");
    public static final Resource WeakAnnoMatch = resource("WeakAnnoMatch");
    public static final Resource StrongEntityMatch = resource("StrongEntityMatch");

    public static final Resource DSD = resource("dsd");

    public static final Property annotator = property("annotator");
    public static final Property dataset = property("dataset");
    public static final Property experimentType = property("experimentType");
    public static final Property errorCount = property("errorCount");
    public static final Property macroF1 = property("macroF1");
    public static final Property macroPrecision = property("macroPrecision");
    public static final Property macroRecall = property("macroRecall");
    public static final Property matching = property("matching");
    public static final Property microF1 = property("microF1");
    public static final Property microPrecision = property("microPrecision");
    public static final Property microRecall = property("microRecall");
    public static final Property statusCode = property("statusCode");
    public static final Property timestamp = property("timestamp");
    public static final Property topic = property("topic");

    public static Resource getMatchingResource(Matching matching) {
        switch (matching) {
        case STRONG_ANNOTATION_MATCH:
            return StrongAnnoMatch;
        case WEAK_ANNOTATION_MATCH:
            return WeakAnnoMatch;
        case STRONG_ENTITY_MATCH:
            return StrongEntityMatch;
        default:
            ;
        }
        LOGGER.error("Got an unknown matching type: " + matching.name());
        return null;
    }

    @SuppressWarnings("deprecation")
    public static Resource getExperimentTypeResource(ExperimentType type) {
        switch (type) {
        case A2KB:
            return A2KB;
        case C2KB:
            return C2KB;
        case D2KB:
            return D2KB;
        case Rc2KB:
            return Rc2KB;
        case Sa2KB:
            return Sa2KB;
        case Sc2KB:
            return Sc2KB;
        case OKE_Task1:
            return OKE2015_Task1;
        case OKE_Task2:
            return OKE2015_Task2;
            // FIXME add missing experiment types
        default:
            ;
        }
        LOGGER.error("Got an unknown experiment type: " + type.name());
        return null;
    }
}
