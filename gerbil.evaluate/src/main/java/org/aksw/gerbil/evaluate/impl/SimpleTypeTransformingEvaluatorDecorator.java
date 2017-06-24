package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractTypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.TypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This is a simple {@link TypeTransformingEvaluatorDecorator} that casts all
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
public class SimpleTypeTransformingEvaluatorDecorator<U extends Marking, V extends Marking>
        extends AbstractTypeTransformingEvaluatorDecorator<U, V> {

    private Class<? extends V> clazz;

    public SimpleTypeTransformingEvaluatorDecorator(Evaluator<V> evaluator, Class<? extends V> clazz) {
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
