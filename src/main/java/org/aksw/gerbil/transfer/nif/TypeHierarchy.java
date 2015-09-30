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
