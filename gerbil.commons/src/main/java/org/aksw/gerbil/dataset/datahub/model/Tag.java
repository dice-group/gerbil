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

import java.util.Date;

/**
 * Represents a tag
 * 
 * @author Ross Jones (ross.jones@okfn.org)
 * @version 1.7
 * @since 2012-05-01
 */
public class Tag {

    private String display_name;

    private String id;

    private String name;

    private Date revision_timestamp;

    private String state;

    private String vocabulary_id;

    public Tag() {
    }

    public Tag(String display_name, String id, String name, Date revision_timestamp, String state, String vocabulary_id) {
        super();
        this.display_name = display_name;
        this.id = id;
        this.name = name;
        this.revision_timestamp = revision_timestamp;
        this.state = state;
        this.vocabulary_id = vocabulary_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRevision_timestamp() {
        return revision_timestamp;
    }

    public void setRevision_timestamp(Date revision_timestamp) {
        this.revision_timestamp = revision_timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVocabulary_id() {
        return vocabulary_id;
    }

    public void setVocabulary_id(String vocabulary_id) {
        this.vocabulary_id = vocabulary_id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((display_name == null) ? 0 : display_name.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((revision_timestamp == null) ? 0 : revision_timestamp.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((vocabulary_id == null) ? 0 : vocabulary_id.hashCode());
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
        Tag other = (Tag) obj;
        if (display_name == null) {
            if (other.display_name != null)
                return false;
        } else if (!display_name.equals(other.display_name))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (revision_timestamp == null) {
            if (other.revision_timestamp != null)
                return false;
        } else if (!revision_timestamp.equals(other.revision_timestamp))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (vocabulary_id == null) {
            if (other.vocabulary_id != null)
                return false;
        } else if (!vocabulary_id.equals(other.vocabulary_id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tag [display_name=");
        builder.append(display_name);
        builder.append(", id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", revision_timestamp=");
        builder.append(revision_timestamp);
        builder.append(", state=");
        builder.append(state);
        builder.append(", vocabulary_id=");
        builder.append(vocabulary_id);
        builder.append("]");
        return builder.toString();
    }

}
