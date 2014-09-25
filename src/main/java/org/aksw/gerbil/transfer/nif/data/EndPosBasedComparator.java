package org.aksw.gerbil.transfer.nif.data;

import java.util.Comparator;

import org.aksw.gerbil.transfer.nif.Annotation;

public class EndPosBasedComparator implements Comparator<Annotation> {

    @Override
    public int compare(Annotation a1, Annotation a2) {
        int diff = (a1.getStartPosition() + a1.getLength()) - (a2.getStartPosition() + a2.getLength());
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
