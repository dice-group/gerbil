package org.aksw.gerbil.transfer.nif.data;

import java.util.Calendar;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.ProvenanceInfo;

public class ProvenanceInfoImpl implements ProvenanceInfo {

    protected Calendar startedAt;
    protected Calendar endedAt;
    protected Set<String> associatedAgents;

    public ProvenanceInfoImpl(Calendar startedAt, Calendar endedAt, Set<String> associatedAgents) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.associatedAgents = associatedAgents;
    }

    public ProvenanceInfoImpl(ProvenanceInfo provenanceInfo) {
        this(provenanceInfo.getStartedAt(), provenanceInfo.getEndedAt(), provenanceInfo.getAssociatedAgents());
    }

    @Override
    public void setProvenanceInfo(ProvenanceInfo provencance) {
        // nothing to do
    }

    @Override
    public ProvenanceInfo getProvenanceInfo() {
        return null;
    }

    @Override
    public Set<String> getAssociatedAgents() {
        return associatedAgents;
    }

    @Override
    public Calendar getStartedAt() {
        return startedAt;
    }

    @Override
    public Calendar getEndedAt() {
        return endedAt;
    }

    public Object clone() throws CloneNotSupportedException {
        return new ProvenanceInfoImpl(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((associatedAgents == null) ? 0 : associatedAgents.hashCode());
        result = prime * result + ((endedAt == null) ? 0 : endedAt.hashCode());
        result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
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
        ProvenanceInfoImpl other = (ProvenanceInfoImpl) obj;
        if (associatedAgents == null) {
            if (other.associatedAgents != null)
                return false;
        } else if (!associatedAgents.equals(other.associatedAgents))
            return false;
        if (endedAt == null) {
            if (other.endedAt != null)
                return false;
        } else if (!endedAt.equals(other.endedAt))
            return false;
        if (startedAt == null) {
            if (other.startedAt != null)
                return false;
        } else if (!startedAt.equals(other.startedAt))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProvenanceInfoImpl [startedAt=");
        builder.append(startedAt);
        builder.append(", endedAt=");
        builder.append(endedAt);
        builder.append(", associatedAgents=");
        builder.append(associatedAgents);
        builder.append("]");
        return builder.toString();
    }
    
}
