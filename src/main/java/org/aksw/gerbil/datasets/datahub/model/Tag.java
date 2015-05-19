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
package org.aksw.gerbil.datasets.datahub.model;

import java.util.Date;

/**
 * Represents a tag
 * 
 * @author Ross Jones <ross.jones@okfn.org>
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
