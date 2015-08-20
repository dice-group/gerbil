package org.aksw.gerbil.utils;

import java.util.Comparator;

import org.aksw.gerbil.datatypes.ExperimentType;

public class ExperimentTypeComparator implements Comparator<ExperimentType> {

    @Override
    public int compare(ExperimentType type1, ExperimentType type2) {
        // if type1 >= type2
        if (type1.equalsOrContainsType(type2)) {
            // if type2 >= type1
            if (type2.equalsOrContainsType(type1)) {
                return type1.name().compareTo(type2.name());
            } else {
                return 1;
            }
        } else {
            // if type2 >= type1
            if (type2.equalsOrContainsType(type1)) {
                return type1.name().compareTo(type2.name());
            } else {
                return -1;
            }
        }
    }

}
