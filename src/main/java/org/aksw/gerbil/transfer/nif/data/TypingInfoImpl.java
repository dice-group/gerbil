/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypeHierarchy;
import org.aksw.gerbil.transfer.nif.TypingInfo;

import com.hp.hpl.jena.vocabulary.RDFS;

@Deprecated
public class TypingInfoImpl implements TypingInfo, TypeHierarchy {

    protected Map<String, TypeNode> uriToNodeMapping = new HashMap<String, TypeNode>();
    protected Map<String, Set<String>> entityUriToTypeUriMapping = new HashMap<String, Set<String>>();

    @Override
    public TypeNode getTypeHierarchy(String uri) {
        if (uriToNodeMapping.containsKey(uri)) {
            return uriToNodeMapping.get(uri);
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getTypeUris(String uri) {
        if (!entityUriToTypeUriMapping.containsKey(uri)) {
            return null;
        }
        Set<String> directTypes = entityUriToTypeUriMapping.get(uri);
        Set<String> typeUris = new HashSet<String>();
        for (String type : directTypes) {
            typeUris.add(type);
            addAllParents(uriToNodeMapping.get(type), typeUris);
        }
        return typeUris;
    }

    protected void addAllParents(TypeNode type, Set<String> typeUris) {
        for (TypeNode superType : type.superTypes) {
            if (typeUris.contains(superType)) {
                typeUris.addAll(superType.uris);
                addAllParents(superType, typeUris);
            }
        }
    }

    public void addTypeInformation(String entityUri, String typeUri) {
        // Add the type to the hierarchy
        addType(typeUri);
        // Add the link between entity and type
        Set<String> types;
        if (entityUriToTypeUriMapping.containsKey(entityUri)) {
            types = entityUriToTypeUriMapping.get(entityUri);
        } else {
            types = new HashSet<String>();
        }
        types.add(typeUri);
    }

    public void addType(String typeUri) {
        if (!uriToNodeMapping.containsKey(typeUri)) {
            uriToNodeMapping.put(typeUri, new TypeNode(typeUri));
        }
        // Add the type as entity of rdfs:Class;
        addTypeInformation(typeUri, RDFS.Class.getURI());
    }

    public void addSubType(String typeUri, String subType) {
        // retrieve the type nodes
        TypeNode typeNode = getOrAddTypeNode(typeUri);
        TypeNode subTypeNode = getOrAddTypeNode(subType);
        // Add the linking in both directions
        typeNode.subTypes.add(subTypeNode);
        subTypeNode.superTypes.add(typeNode);
    }

    protected TypeNode getOrAddTypeNode(String typeUri) {
        if (!uriToNodeMapping.containsKey(typeUri)) {
            addType(typeUri);
        }
        return uriToNodeMapping.get(typeUri);
    }
}
