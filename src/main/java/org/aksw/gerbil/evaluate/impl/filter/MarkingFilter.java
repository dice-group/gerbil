package org.aksw.gerbil.evaluate.impl.filter;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

public interface MarkingFilter<T extends Marking> {

    public boolean isMarkingGood(T marking);

    public List<T> filterList(List<T> markings);

    public List<List<T>> filterListOfLists(List<List<T>> markings);
}
