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
