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

public interface ClassSet extends Iterable<ClassNode> {

    // public ClassNode getRoot();

    public ClassNode getNode(String uri);

    public void addNode(ClassNode node/* , ClassNode parent */);

    public void addUriToNode(ClassNode node, String uri);

    /**
     * Removes the given node and all its children.
     * 
     * @param node
     */
    public void removeNode(ClassNode node);
}
