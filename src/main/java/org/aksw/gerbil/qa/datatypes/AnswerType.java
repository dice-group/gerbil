package org.aksw.gerbil.qa.datatypes;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;

public class AnswerType implements Marking {

    private AnswerTypes type;

    public AnswerType(AnswerTypes type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        AnswerType other = (AnswerType) obj;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AnswerType [type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public Object clone() {
        return new AnswerType(type);
    }

	@Override
	public void setProvenanceInfo(ProvenanceInfo provencance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProvenanceInfo getProvenanceInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
