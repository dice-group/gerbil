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
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.utils.AnnotationException;

import java.util.HashSet;

import org.aksw.gerbil.exceptions.GerbilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractSa2WAnnotatorDecorator implements AnnotatorDecorator<Sa2WSystem>, Sa2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSa2WAnnotatorDecorator.class);

    protected Sa2WSystem annotator;

    @Override
    public Sa2WSystem getAnnotator() throws GerbilException {
        return annotator;
    }

    protected void setAnnotator(Sa2WSystem annotator) {
        this.annotator = annotator;
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        try {
            return getAnnotator().solveA2W(text);
        } catch (GerbilException e) {
            throw new AnnotationException(e.getLocalizedMessage());
        }
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        try {
            return getAnnotator().solveC2W(text);
        } catch (GerbilException e) {
            throw new AnnotationException(e.getLocalizedMessage());
        }
    }

    @Override
    public String getName() {
        try {
            return getAnnotator().getName();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get annotator.", e);
            return "";
        }
    }

    @Override
    public long getLastAnnotationTime() {
        try {
            return getAnnotator().getLastAnnotationTime();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get annotator.", e);
            return -1;
        }
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
        try {
            return getAnnotator().solveD2W(text, mentions);
        } catch (GerbilException e) {
            throw new AnnotationException(e.getLocalizedMessage());
        }
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
        try {
            return getAnnotator().solveSc2W(text);
        } catch (GerbilException e) {
            throw new AnnotationException(e.getLocalizedMessage());
        }
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
        try {
            return getAnnotator().solveSa2W(text);
        } catch (GerbilException e) {
            throw new AnnotationException(e.getLocalizedMessage());
        }
    }

}
