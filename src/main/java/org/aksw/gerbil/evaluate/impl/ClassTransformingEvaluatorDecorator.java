package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractTypeChangingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.TypeChangingEvaluatorDecorator;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This is a simple {@link TypeChangingEvaluatorDecorator} that casts all
 * objects to the given internal target class. If an instance is not
 * implementing the given class it will be discarded.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <U>
 *            external {@link Marking} type
 * @param <V>
 *            internal {@link Marking} type
 */
public class ClassTransformingEvaluatorDecorator<U extends Marking, V extends Marking>
        extends AbstractTypeChangingEvaluatorDecorator<U, V> {

    private Class<V> clazz;

    public ClassTransformingEvaluatorDecorator(Evaluator<V> evaluator, Class<V> clazz) {
        super(evaluator);
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<V> changeType(List<U> markings) {
        List<V> newMarkings = new ArrayList<V>(markings.size());
        for (U marking : markings) {
            if (clazz.isInstance(marking)) {
                newMarkings.add((V) marking);
            }
        }
        return newMarkings;
    }

}
