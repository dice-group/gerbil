package org.aksw.gerbil.transfer.nif;

import java.util.Calendar;
import java.util.Set;

public interface ProvenanceInfo extends Marking {

    public Set<String> getAssociatedAgents();

    public Calendar getStartedAt();

    public Calendar getEndedAt();
}
