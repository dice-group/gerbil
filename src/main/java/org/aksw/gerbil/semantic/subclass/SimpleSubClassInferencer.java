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

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class SimpleSubClassInferencer implements SubClassInferencer {

    private Model classModel;

    public SimpleSubClassInferencer(Model classModel) {
        this.classModel = classModel;
    }

    @Override
    public void inferSubClasses(String classURI, ClassSet hierarchy, ClassNodeFactory<? extends ClassNode> factory) {
        Resource classResource = new ResourceImpl(classURI);
        Set<String> alreadySeenUris = new HashSet<String>();
        addOrUpdateUri(classResource, hierarchy, factory, alreadySeenUris);

        if (!classModel.containsResource(classResource)) {
            return;
        }

        StmtIterator iterator = classModel.listStatements(null, RDFS.subClassOf, classResource);
        Statement stmt;
        Resource resource;
        while (iterator.hasNext()) {
            stmt = iterator.next();
            resource = stmt.getSubject();
            if (!alreadySeenUris.contains(resource.getURI())) {
                addOrUpdateUri(resource, hierarchy, factory, alreadySeenUris);
            }
        }
    }

    private void addOrUpdateUri(Resource resource, ClassSet hierarchy, ClassNodeFactory<? extends ClassNode> factory,
            Set<String> alreadySeenUris) {
        String uri = resource.getURI();
        ClassNode node = hierarchy.getNode(uri);
        if (node == null) {
            node = factory.createNode(uri);
            hierarchy.addNode(node);
        } else {
            factory.updateNode(node);
        }
        alreadySeenUris.add(uri);

        StmtIterator iterator = classModel.listStatements(resource, OWL.sameAs, (RDFNode) null);
        Statement stmt;
        while (iterator.hasNext()) {
            stmt = iterator.next();
            uri = stmt.getObject().asResource().getURI();
            hierarchy.addUriToNode(node, uri);
            alreadySeenUris.add(uri);
        }
        iterator = classModel.listStatements(resource, OWL.equivalentClass, (RDFNode) null);
        while (iterator.hasNext()) {
            stmt = iterator.next();
            uri = stmt.getObject().asResource().getURI();
            hierarchy.addUriToNode(node, uri);
            alreadySeenUris.add(uri);
        }
    }

}
