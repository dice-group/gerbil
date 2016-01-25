package org.aksw.gerbil.datatypes.marking;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

public interface ClassifiedMarking extends Marking {

    public List<MarkingClasses> getClasses();

    public boolean hasClass(MarkingClasses clazz);

    public void setClass(MarkingClasses clazz);

    public void unsetClass(MarkingClasses clazz);

    public Marking getWrappedMarking();
}
