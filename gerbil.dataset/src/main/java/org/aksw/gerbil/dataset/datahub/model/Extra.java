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
package org.aksw.gerbil.dataset.datahub.model;

/**
 * Represents an extra metadata field in a dataset or group
 * 
 * @author Ross Jones (ross.jones@okfn.org)
 * @version 1.7
 * @since 2012-05-01
 */
public class Extra {

    private String key;

    private String value;

    private Extras __extras;

    public class Extras {
        private String package_id;
        private String revision_id;

        public String getPackage_id() {
            return package_id;
        }

        public void setPackage_id(String package_id) {
            this.package_id = package_id;
        }

        public String getRevision_id() {
            return revision_id;
        }

        public void setRevision_id(String revision_id) {
            this.revision_id = revision_id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((package_id == null) ? 0 : package_id.hashCode());
            result = prime * result + ((revision_id == null) ? 0 : revision_id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Extras other = (Extras) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (package_id == null) {
                if (other.package_id != null)
                    return false;
            } else if (!package_id.equals(other.package_id))
                return false;
            if (revision_id == null) {
                if (other.revision_id != null)
                    return false;
            } else if (!revision_id.equals(other.revision_id))
                return false;
            return true;
        }

        private Extra getOuterType() {
            return Extra.this;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Extras [package_id=");
            builder.append(package_id);
            builder.append(", revision_id=");
            builder.append(revision_id);
            builder.append("]");
            return builder.toString();
        }

    }

    public Extra() {
    }

    public Extra(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Extra other = (Extra) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Extra [key=");
        builder.append(key);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

    public Extras get__extras() {
        return __extras;
    }

    public void set__extras(Extras __extras) {
        this.__extras = __extras;
    }

}
