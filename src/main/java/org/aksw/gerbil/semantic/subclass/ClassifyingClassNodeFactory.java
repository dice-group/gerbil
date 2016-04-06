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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyingClassNodeFactory implements ClassNodeFactory<ClassifiedClassNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyingClassNodeFactory.class);

    private int classId;

    public ClassifyingClassNodeFactory(int classId) {
        this.classId = classId;
    }

    @Override
    public ClassifiedClassNode createNode(String uri) {
        ClassifiedClassNode node = new ClassifiedClassNode(uri);
        node.addClassId(classId);
        return node;
    }

    @Override
    public void updateNode(ClassNode oldNode) {
        if (oldNode instanceof ClassifiedClassNode) {
            ((ClassifiedClassNode) oldNode).addClassId(classId);
        } else {
            LOGGER.warn("Got a node that is not an instance of the ClassifiedClassNode class. Can't add classification to this node.");
        }
    }

}
