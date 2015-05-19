package org.aksw.gerbil.utils.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypedMarking;

public class TypeBasedMarkingFilter<T extends TypedMarking> extends AbstractMarkingFilter<T> {

    private Set<String> types = new HashSet<String>();
    private boolean acceptTypes;

    public TypeBasedMarkingFilter(boolean acceptTypes, String... types) {
        this.types.addAll(Arrays.asList(types));
        this.acceptTypes = acceptTypes;
    }

    @Override
    public boolean isMarkingGood(T marking) {
        Set<String> types = marking.getTypes();
        if (acceptTypes) {
            for (String acceptedType : this.types) {
                if (types.contains(acceptedType)) {
                    return true;
                }
            }
            return false;
        } else {
            for (String notAcceptedType : this.types) {
                if (types.contains(notAcceptedType)) {
                    return false;
                }
            }
            return true;
        }
    }

}
