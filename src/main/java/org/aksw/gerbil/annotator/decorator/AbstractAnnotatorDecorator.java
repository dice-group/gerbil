package org.aksw.gerbil.annotator.decorator;

import java.io.IOException;

import org.aksw.gerbil.annotator.Annotator;

/**
 * Abstract implementation of an {@link AnnotatorDecorator}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class AbstractAnnotatorDecorator implements AnnotatorDecorator {

    protected Annotator decoratedAnnotator;

    public AbstractAnnotatorDecorator(Annotator decoratedAnnotator) {
        this.decoratedAnnotator = decoratedAnnotator;
    }

    @Override
    public String getName() {
        return decoratedAnnotator.getName();
    }

    @Override
    public void setName(String name) {
        decoratedAnnotator.setName(name);
    }

    @Override
    public Annotator getDecoratedAnnotator() {
        return decoratedAnnotator;
    }

    @Override
    public void close() throws IOException {
        decoratedAnnotator.close();
    }
}
