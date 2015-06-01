/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.semantic.subclass;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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
