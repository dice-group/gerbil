package org.aksw.gerbil.annotator.decorator;

import org.aksw.gerbil.annotator.Annotator;

/**
 * Interface of a Decorator of an {@link Annotator}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface AnnotatorDecorator extends Annotator {

    /**
     * Returns the decorated {@link Annotator}.
     * 
     * @return the decorated annotator.
     */
    public Annotator getDecoratedAnnotator();
}
