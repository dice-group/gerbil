package org.aksw.gerbil.matching.impl.clas;

import org.aksw.gerbil.transfer.nif.Marking;

public interface MarkingClassifier<T extends Marking> {

    public int getNumberOfClasses();

    public int getClass(T marking);
}
