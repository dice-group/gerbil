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

public class Organization {
    private String approval_status;

    private Date created;

    private String description;

    private String id;

    private String image_url;

    private Boolean is_organization;

    private String name;

    private String revision_id;

    private Date revision_timestamp;

    private String state;

    private String Title;

    private String type;

    public Organization() {
    }

    public Organization(String approval_status, Date created, String description, String id, String image_url,
            Boolean is_organization, String name, String revision_id, Date revision_timestamp, String state,
            String title, String type) {
        super();
        this.approval_status = approval_status;
        this.created = created;
        this.description = description;
        this.id = id;
        this.image_url = image_url;
        this.is_organization = is_organization;
        this.name = name;
        this.revision_id = revision_id;
        this.revision_timestamp = revision_timestamp;
        this.state = state;
        Title = title;
        this.type = type;
    }

    public String getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(String approval_status) {
        this.approval_status = approval_status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getIs_organization() {
        return is_organization;
    }

    public void setIs_organization(Boolean is_organization) {
        this.is_organization = is_organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Title == null) ? 0 : Title.hashCode());
        result = prime * result + ((approval_status == null) ? 0 : approval_status.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((image_url == null) ? 0 : image_url.hashCode());
        result = prime * result + ((is_organization == null) ? 0 : is_organization.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((revision_id == null) ? 0 : revision_id.hashCode());
        result = prime * result + ((revision_timestamp == null) ? 0 : revision_timestamp.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Organization other = (Organization) obj;
        if (Title == null) {
            if (other.Title != null)
                return false;
        } else if (!Title.equals(other.Title))
            return false;
        if (approval_status == null) {
            if (other.approval_status != null)
                return false;
        } else if (!approval_status.equals(other.approval_status))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (image_url == null) {
            if (other.image_url != null)
                return false;
        } else if (!image_url.equals(other.image_url))
            return false;
        if (is_organization == null) {
            if (other.is_organization != null)
                return false;
        } else if (!is_organization.equals(other.is_organization))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (revision_id == null) {
            if (other.revision_id != null)
                return false;
        } else if (!revision_id.equals(other.revision_id))
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
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Organization [approval_status=");
        builder.append(approval_status);
        builder.append(", created=");
        builder.append(created);
        builder.append(", description=");
        builder.append(description);
        builder.append(", id=");
        builder.append(id);
        builder.append(", image_url=");
        builder.append(image_url);
        builder.append(", is_organization=");
        builder.append(is_organization);
        builder.append(", name=");
        builder.append(name);
        builder.append(", revision_id=");
        builder.append(revision_id);
        builder.append(", revision_timestamp=");
        builder.append(revision_timestamp);
        builder.append(", state=");
        builder.append(state);
        builder.append(", Title=");
        builder.append(Title);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

}
