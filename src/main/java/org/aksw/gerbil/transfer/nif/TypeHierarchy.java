package org.aksw.gerbil.transfer.nif;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public interface TypeHierarchy {

    public TypeNode getTypeHierarchy(String uri);

    public static class TypeNode {
        /**
         * URIs of this type.
         */
        public List<String> uris;
        /**
         * Super types of this type.
         */
        public List<TypeNode> superTypes;
        /**
         * sub types of this type;
         */
        public List<TypeNode> subTypes;
        
        public TypeNode(String typeUri) {
            this.uris = new ArrayList<String>();
            this.uris.add(typeUri);
            this.superTypes = new ArrayList<TypeNode>();
            this.subTypes = new ArrayList<TypeNode>();
        }
    }
}
