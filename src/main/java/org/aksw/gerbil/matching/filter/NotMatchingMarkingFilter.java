package org.aksw.gerbil.matching.filter;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

/**
 * Removes every {@link Marking} that is not matching.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface NotMatchingMarkingFilter<T extends Marking> {

    /**
     * Returns a list of {@link Marking}s that does not contain any
     * {@link Marking}s that is not matching the given gold standard list.
     * 
     * @param markings
     *            the list of {@link Marking}s
     * @return a filtered list of {@link Marking}s
     */
    public List<? extends T> filterMarkings(List<T> markings, List<T> goldStandard);
}
