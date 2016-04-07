/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This decorator implements the {@link Evaluator} interface for the type
 * &lt;U&gt; but transforms the lists internally into lists of &lt;V&gt; before
 * calling the
 * {@link Evaluator#evaluate(java.util.List, java.util.List, EvaluationResultContainer)}
 * method of the decorated {@link Evaluator}. The way how it is transformed
 * depends on the implementation of this interface.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <U>
 *            external {@link Marking} type
 * @param <V>
 *            internal {@link Marking} type
 */
public interface TypeTransformingEvaluatorDecorator<U extends Marking, V extends Marking> extends Evaluator<U> {

    /**
     * Returns the decorated {@link Evaluator}.
     * 
     * @return the decorated {@link Evaluator}
     */
    public Evaluator<V> getDecorated();
}
