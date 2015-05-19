package org.aksw.gerbil.evaluate.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

public abstract class AbstractMarkingFilter<T extends Marking> implements MarkingFilter<T> {

    @Override
    public List<T> filterList(List<T> markings) {
        List<T> filteredMarkings = new ArrayList<T>(markings.size());
        for (T marking : markings) {
            if (isMarkingGood(marking)) {
                filteredMarkings.add(marking);
            }
        }
        return filteredMarkings;
    }

    @Override
    public List<List<T>> filterListOfLists(List<List<T>> markings) {
        List<List<T>> filteredMarkings = new ArrayList<List<T>>(markings.size());
        for (List<T> list : markings) {
            filteredMarkings.add(filterList(list));
        }
        return filteredMarkings;
    }
}
