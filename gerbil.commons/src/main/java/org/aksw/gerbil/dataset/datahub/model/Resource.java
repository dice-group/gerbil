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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a single resource within a Dataset
 * 
 * @author Ross Jones (ross.jones@okfn.org)
 * @version 2.2
 * @since 2012-05-01
 */
public class Resource {
    public static class Response {
        private String help;

        private Error error;

        private boolean success;

        private Resource result;

        public Response() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Resource getResult() {
            return result;
        }

        public void setResult(Resource result) {
            this.result = result;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

        public String getHelp() {
            return help;
        }

        public void setHelp(String help) {
            this.help = help;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public static class TrackingSummary {
        private long recent;

        private long total;

        public TrackingSummary() {
        }

        public long getRecent() {
            return recent;
        }

        public void setRecent(long recent) {
            this.recent = recent;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private String cache_last_updated;

    private String cache_url;

    private String created;

    private String description;

    private String format;

    private String hash;

    private String id;

    private String last_modified;

    private String mimetype;

    private String mimetype_inner;

    private String name;

    // only on create
    private String package_id;

    // generated
    private long position;

    // generated
    private String resource_group_id;

    private String resource_type;

    private String revision_id;

    // generated
    private String revision_timestamp;

    private long size;

    // generated
    private String state;

    // generated
    private TrackingSummary tracking_summary;

    private String url;

    // generated
    private String url_type;

    private String webstore_last_updated;

    private String webstore_url;

    public Resource() {
    }

    public String getCache_last_updated() {
        return cache_last_updated;
    }

    public void setCache_last_updated(String cache_last_updated) {
        this.cache_last_updated = cache_last_updated;
    }

    public String getCache_url() {
        return cache_url;
    }

    public void setCache_url(String cache_url) {
        this.cache_url = cache_url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype_inner() {
        return mimetype_inner;
    }

    public void setMimetype_inner(String mimetype_inner) {
        this.mimetype_inner = mimetype_inner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getResource_group_id() {
        return resource_group_id;
    }

    public void setResource_group_id(String resource_group_id) {
        this.resource_group_id = resource_group_id;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    public String getRevision_timestamp() {
        return revision_timestamp;
    }

    public void setRevision_timestamp(String revision_timestamp) {
        this.revision_timestamp = revision_timestamp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public TrackingSummary getTracking_summary() {
        return tracking_summary;
    }

    public void setTracking_summary(TrackingSummary tracking_summary) {
        this.tracking_summary = tracking_summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_type() {
        return url_type;
    }

    public void setUrl_type(String url_type) {
        this.url_type = url_type;
    }

    public String getWebstore_last_updated() {
        return webstore_last_updated;
    }

    public void setWebstore_last_updated(String webstore_last_updated) {
        this.webstore_last_updated = webstore_last_updated;
    }

    public String getWebstore_url() {
        return webstore_url;
    }

    public void setWebstore_url(String webstore_url) {
        this.webstore_url = webstore_url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cache_last_updated == null) ? 0 : cache_last_updated.hashCode());
        result = prime * result + ((cache_url == null) ? 0 : cache_url.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((last_modified == null) ? 0 : last_modified.hashCode());
        result = prime * result + ((mimetype == null) ? 0 : mimetype.hashCode());
        result = prime * result + ((mimetype_inner == null) ? 0 : mimetype_inner.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((package_id == null) ? 0 : package_id.hashCode());
        result = prime * result + (int) (position ^ (position >>> 32));
        result = prime * result + ((resource_group_id == null) ? 0 : resource_group_id.hashCode());
        result = prime * result + ((resource_type == null) ? 0 : resource_type.hashCode());
        result = prime * result + ((revision_id == null) ? 0 : revision_id.hashCode());
        result = prime * result + ((revision_timestamp == null) ? 0 : revision_timestamp.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((tracking_summary == null) ? 0 : tracking_summary.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((url_type == null) ? 0 : url_type.hashCode());
        result = prime * result + ((webstore_last_updated == null) ? 0 : webstore_last_updated.hashCode());
        result = prime * result + ((webstore_url == null) ? 0 : webstore_url.hashCode());
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
        Resource other = (Resource) obj;
        if (cache_last_updated == null) {
            if (other.cache_last_updated != null)
                return false;
        } else if (!cache_last_updated.equals(other.cache_last_updated))
            return false;
        if (cache_url == null) {
            if (other.cache_url != null)
                return false;
        } else if (!cache_url.equals(other.cache_url))
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
        if (format == null) {
            if (other.format != null)
                return false;
        } else if (!format.equals(other.format))
            return false;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (last_modified == null) {
            if (other.last_modified != null)
                return false;
        } else if (!last_modified.equals(other.last_modified))
            return false;
        if (mimetype == null) {
            if (other.mimetype != null)
                return false;
        } else if (!mimetype.equals(other.mimetype))
            return false;
        if (mimetype_inner == null) {
            if (other.mimetype_inner != null)
                return false;
        } else if (!mimetype_inner.equals(other.mimetype_inner))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (package_id == null) {
            if (other.package_id != null)
                return false;
        } else if (!package_id.equals(other.package_id))
            return false;
        if (position != other.position)
            return false;
        if (resource_group_id == null) {
            if (other.resource_group_id != null)
                return false;
        } else if (!resource_group_id.equals(other.resource_group_id))
            return false;
        if (resource_type == null) {
            if (other.resource_type != null)
                return false;
        } else if (!resource_type.equals(other.resource_type))
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
        if (size != other.size)
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (tracking_summary == null) {
            if (other.tracking_summary != null)
                return false;
        } else if (!tracking_summary.equals(other.tracking_summary))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (url_type == null) {
            if (other.url_type != null)
                return false;
        } else if (!url_type.equals(other.url_type))
            return false;
        if (webstore_last_updated == null) {
            if (other.webstore_last_updated != null)
                return false;
        } else if (!webstore_last_updated.equals(other.webstore_last_updated))
            return false;
        if (webstore_url == null) {
            if (other.webstore_url != null)
                return false;
        } else if (!webstore_url.equals(other.webstore_url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Resource [cache_last_updated=");
        builder.append(cache_last_updated);
        builder.append(", cache_url=");
        builder.append(cache_url);
        builder.append(", created=");
        builder.append(created);
        builder.append(", description=");
        builder.append(description);
        builder.append(", format=");
        builder.append(format);
        builder.append(", hash=");
        builder.append(hash);
        builder.append(", id=");
        builder.append(id);
        builder.append(", last_modified=");
        builder.append(last_modified);
        builder.append(", mimetype=");
        builder.append(mimetype);
        builder.append(", mimetype_inner=");
        builder.append(mimetype_inner);
        builder.append(", name=");
        builder.append(name);
        builder.append(", package_id=");
        builder.append(package_id);
        builder.append(", position=");
        builder.append(position);
        builder.append(", resource_group_id=");
        builder.append(resource_group_id);
        builder.append(", resource_type=");
        builder.append(resource_type);
        builder.append(", revision_id=");
        builder.append(revision_id);
        builder.append(", revision_timestamp=");
        builder.append(revision_timestamp);
        builder.append(", size=");
        builder.append(size);
        builder.append(", state=");
        builder.append(state);
        builder.append(", tracking_summary=");
        builder.append(tracking_summary);
        builder.append(", url=");
        builder.append(url);
        builder.append(", url_type=");
        builder.append(url_type);
        builder.append(", webstore_last_updated=");
        builder.append(webstore_last_updated);
        builder.append(", webstore_url=");
        builder.append(webstore_url);
        builder.append("]");
        return builder.toString();
    }

}
