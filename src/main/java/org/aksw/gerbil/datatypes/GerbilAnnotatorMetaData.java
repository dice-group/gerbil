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
package org.aksw.gerbil.datatypes;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.annotations.GerbilAnnotator;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.exceptions.GerbilException;
import org.springframework.context.ApplicationContext;

public class GerbilAnnotatorMetaData extends AbstractGerbilAdapterMetaData implements AnnotatorConfiguration {

    private ApplicationContext context;
    private Class<? extends TopicSystem> annotatorClass;

    public GerbilAnnotatorMetaData(GerbilAnnotator annotatorAnnotation, ApplicationContext context,
            Class<? extends TopicSystem> annotatorClass) {
        super(annotatorAnnotation.name(), annotatorAnnotation.couldBeCached(), annotatorAnnotation
                .applicableForExperiments());
        this.context = context;
        this.annotatorClass = annotatorClass;
    }

    public TopicSystem loadAnnotator() throws InstantiationException, IllegalAccessException, GerbilException {
        return context.getAutowireCapableBeanFactory().createBean(annotatorClass);
    }

    @Override
    public TopicSystem getAnnotator(ExperimentType type) throws GerbilException {
        if (this.isApplicableForExperiment(type)) {
            try {
                return loadAnnotator();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GerbilException("Exception while instantiating annotator.", e,
                        ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        } else {
            return null;
        }
    }
}
