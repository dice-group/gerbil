package org.aksw.gerbil.transfer.nif;

import java.util.Set;

public interface TypedMarking extends Marking {

    public Set<String> getTypes();

    public void setTypes(Set<String> types);
}
