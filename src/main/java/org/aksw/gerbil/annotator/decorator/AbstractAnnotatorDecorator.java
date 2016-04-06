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
package org.aksw.gerbil.annotator.decorator;

import java.io.IOException;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.utils.ClosePermitionGranter;

/**
 * Abstract implementation of an {@link AnnotatorDecorator}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public abstract class AbstractAnnotatorDecorator implements AnnotatorDecorator {

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

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        decoratedAnnotator.setClosePermitionGranter(granter);
    }
}
