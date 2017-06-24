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
package org.aksw.gerbil.semantic.subclass;

/**
 * A sub class inferencer shall infer all known subclasses of the given class
 * and store the resulting {@link ClassNode} objects in a {@link ClassSet}.
 * Note, that this shall include the given class itself. Thus, the set should
 * contain at least one single {@link ClassNode} object representing the given
 * class.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public interface SubClassInferencer {

    public void inferSubClasses(String classURI, ClassSet classes, ClassNodeFactory<? extends ClassNode> factory);
}
