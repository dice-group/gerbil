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
import java.util.List;

import org.aksw.gerbil.dataset.datahub.model.Resource.TrackingSummary;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a CKAN Dataset (previously a Package)
 * 
 * @author Ross Jones (ross.jones@okfn.org)
 * @version 1.7
 * @since 2012-05-01
 */
public class Dataset {

    public static class Response {
        private String help;

        private Error error;

        private boolean success;

        private Dataset result;

        public String getHelp() {
            return help;
        }

        public void setHelp(String help) {
            this.help = help;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Dataset getResult() {
            return result;
        }

        public void setResult(Dataset result) {
            this.result = result;
        }
    }

    private String author;

    private String author_email;

    private String creator_user_id;

    private List<Extra> extras;

    private List<Group> groups;

    private String id;

    private Boolean isopen;

    private String license_id;

    private String license_title;

    private String license_url;

    private String maintainer;

    private String maintainer_email;

    private Date metadata_created;

    private Date metadata_modified;

    private String name;

    private String notes;

    private Long num_resources;

    private Long num_tags;

    private Organization organization;

    private String owner_org;

    @JsonProperty("private")
    private Boolean _private;

    private List<Object> relationships_as_object;

    private List<Object> relationships_as_subject;

    private List<Resource> resources;

    private String revision_id;

    private Date revision_timestamp;

    private String state;

    private List<Tag> tags;

    private String title;

    private TrackingSummary tracking_summary;

    private String type;

    private String url;

    private String version;

    public Dataset() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_email() {
        return author_email;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getCreator_user_id() {
        return creator_user_id;
    }

    public void setCreator_user_id(String creator_user_id) {
        this.creator_user_id = creator_user_id;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsopen() {
        return isopen;
    }

    public void setIsopen(Boolean isopen) {
        this.isopen = isopen;
    }

    public String getLicense_id() {
        return license_id;
    }

    public void setLicense_id(String license_id) {
        this.license_id = license_id;
    }

    public String getLicense_title() {
        return license_title;
    }

    public void setLicense_title(String license_title) {
        this.license_title = license_title;
    }

    public String getLicense_url() {
        return license_url;
    }

    public void setLicense_url(String license_url) {
        this.license_url = license_url;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getMaintainer_email() {
        return maintainer_email;
    }

    public void setMaintainer_email(String maintainer_email) {
        this.maintainer_email = maintainer_email;
    }

    public Date getMetadata_created() {
        return metadata_created;
    }

    public void setMetadata_created(Date metadata_created) {
        this.metadata_created = metadata_created;
    }

    public Date getMetadata_modified() {
        return metadata_modified;
    }

    public void setMetadata_modified(Date metadata_modified) {
        this.metadata_modified = metadata_modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getNum_resources() {
        return num_resources;
    }

    public void setNum_resources(Long num_resources) {
        this.num_resources = num_resources;
    }

    public Long getNum_tags() {
        return num_tags;
    }

    public void setNum_tags(Long num_tags) {
        this.num_tags = num_tags;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getOwner_org() {
        return owner_org;
    }

    public void setOwner_org(String owner_org) {
        this.owner_org = owner_org;
    }

    public Boolean get_private() {
        return _private;
    }

    public void set_private(Boolean _private) {
        this._private = _private;
    }

    public List<Object> getRelationships_as_object() {
        return relationships_as_object;
    }

    public void setRelationships_as_object(List<Object> relationships_as_object) {
        this.relationships_as_object = relationships_as_object;
    }

    public List<Object> getRelationships_as_subject() {
        return relationships_as_subject;
    }

    public void setRelationships_as_subject(List<Object> relationships_as_subject) {
        this.relationships_as_subject = relationships_as_subject;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TrackingSummary getTracking_summary() {
        return tracking_summary;
    }

    public void setTracking_summary(TrackingSummary tracking_summary) {
        this.tracking_summary = tracking_summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_private == null) ? 0 : _private.hashCode());
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((author_email == null) ? 0 : author_email.hashCode());
        result = prime * result + ((creator_user_id == null) ? 0 : creator_user_id.hashCode());
        result = prime * result + ((extras == null) ? 0 : extras.hashCode());
        result = prime * result + ((groups == null) ? 0 : groups.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((isopen == null) ? 0 : isopen.hashCode());
        result = prime * result + ((license_id == null) ? 0 : license_id.hashCode());
        result = prime * result + ((license_title == null) ? 0 : license_title.hashCode());
        result = prime * result + ((license_url == null) ? 0 : license_url.hashCode());
        result = prime * result + ((maintainer == null) ? 0 : maintainer.hashCode());
        result = prime * result + ((maintainer_email == null) ? 0 : maintainer_email.hashCode());
        result = prime * result + ((metadata_created == null) ? 0 : metadata_created.hashCode());
        result = prime * result + ((metadata_modified == null) ? 0 : metadata_modified.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((notes == null) ? 0 : notes.hashCode());
        result = prime * result + ((num_resources == null) ? 0 : num_resources.hashCode());
        result = prime * result + ((num_tags == null) ? 0 : num_tags.hashCode());
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
        result = prime * result + ((owner_org == null) ? 0 : owner_org.hashCode());
        result = prime * result + ((relationships_as_object == null) ? 0 : relationships_as_object.hashCode());
        result = prime * result + ((relationships_as_subject == null) ? 0 : relationships_as_subject.hashCode());
        result = prime * result + ((resources == null) ? 0 : resources.hashCode());
        result = prime * result + ((revision_id == null) ? 0 : revision_id.hashCode());
        result = prime * result + ((revision_timestamp == null) ? 0 : revision_timestamp.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((tracking_summary == null) ? 0 : tracking_summary.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        Dataset other = (Dataset) obj;
        if (_private == null) {
            if (other._private != null)
                return false;
        } else if (!_private.equals(other._private))
            return false;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (author_email == null) {
            if (other.author_email != null)
                return false;
        } else if (!author_email.equals(other.author_email))
            return false;
        if (creator_user_id == null) {
            if (other.creator_user_id != null)
                return false;
        } else if (!creator_user_id.equals(other.creator_user_id))
            return false;
        if (extras == null) {
            if (other.extras != null)
                return false;
        } else if (!extras.equals(other.extras))
            return false;
        if (groups == null) {
            if (other.groups != null)
                return false;
        } else if (!groups.equals(other.groups))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (isopen == null) {
            if (other.isopen != null)
                return false;
        } else if (!isopen.equals(other.isopen))
            return false;
        if (license_id == null) {
            if (other.license_id != null)
                return false;
        } else if (!license_id.equals(other.license_id))
            return false;
        if (license_title == null) {
            if (other.license_title != null)
                return false;
        } else if (!license_title.equals(other.license_title))
            return false;
        if (license_url == null) {
            if (other.license_url != null)
                return false;
        } else if (!license_url.equals(other.license_url))
            return false;
        if (maintainer == null) {
            if (other.maintainer != null)
                return false;
        } else if (!maintainer.equals(other.maintainer))
            return false;
        if (maintainer_email == null) {
            if (other.maintainer_email != null)
                return false;
        } else if (!maintainer_email.equals(other.maintainer_email))
            return false;
        if (metadata_created == null) {
            if (other.metadata_created != null)
                return false;
        } else if (!metadata_created.equals(other.metadata_created))
            return false;
        if (metadata_modified == null) {
            if (other.metadata_modified != null)
                return false;
        } else if (!metadata_modified.equals(other.metadata_modified))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (notes == null) {
            if (other.notes != null)
                return false;
        } else if (!notes.equals(other.notes))
            return false;
        if (num_resources == null) {
            if (other.num_resources != null)
                return false;
        } else if (!num_resources.equals(other.num_resources))
            return false;
        if (num_tags == null) {
            if (other.num_tags != null)
                return false;
        } else if (!num_tags.equals(other.num_tags))
            return false;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        if (owner_org == null) {
            if (other.owner_org != null)
                return false;
        } else if (!owner_org.equals(other.owner_org))
            return false;
        if (relationships_as_object == null) {
            if (other.relationships_as_object != null)
                return false;
        } else if (!relationships_as_object.equals(other.relationships_as_object))
            return false;
        if (relationships_as_subject == null) {
            if (other.relationships_as_subject != null)
                return false;
        } else if (!relationships_as_subject.equals(other.relationships_as_subject))
            return false;
        if (resources == null) {
            if (other.resources != null)
                return false;
        } else if (!resources.equals(other.resources))
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
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (tracking_summary == null) {
            if (other.tracking_summary != null)
                return false;
        } else if (!tracking_summary.equals(other.tracking_summary))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Dataset [author=");
        builder.append(author);
        builder.append(", author_email=");
        builder.append(author_email);
        builder.append(", creator_user_id=");
        builder.append(creator_user_id);
        builder.append(", extras=");
        builder.append(extras);
        builder.append(", groups=");
        builder.append(groups);
        builder.append(", id=");
        builder.append(id);
        builder.append(", isopen=");
        builder.append(isopen);
        builder.append(", license_id=");
        builder.append(license_id);
        builder.append(", license_title=");
        builder.append(license_title);
        builder.append(", license_url=");
        builder.append(license_url);
        builder.append(", maintainer=");
        builder.append(maintainer);
        builder.append(", maintainer_email=");
        builder.append(maintainer_email);
        builder.append(", metadata_created=");
        builder.append(metadata_created);
        builder.append(", metadata_modified=");
        builder.append(metadata_modified);
        builder.append(", name=");
        builder.append(name);
        builder.append(", notes=");
        builder.append(notes);
        builder.append(", num_resources=");
        builder.append(num_resources);
        builder.append(", num_tags=");
        builder.append(num_tags);
        builder.append(", organization=");
        builder.append(organization);
        builder.append(", owner_org=");
        builder.append(owner_org);
        builder.append(", _private=");
        builder.append(_private);
        builder.append(", relationships_as_object=");
        builder.append(relationships_as_object);
        builder.append(", relationships_as_subject=");
        builder.append(relationships_as_subject);
        builder.append(", resources=");
        builder.append(resources);
        builder.append(", revision_id=");
        builder.append(revision_id);
        builder.append(", revision_timestamp=");
        builder.append(revision_timestamp);
        builder.append(", state=");
        builder.append(state);
        builder.append(", tags=");
        builder.append(tags);
        builder.append(", title=");
        builder.append(title);
        builder.append(", tracking_summary=");
        builder.append(tracking_summary);
        builder.append(", type=");
        builder.append(type);
        builder.append(", url=");
        builder.append(url);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
