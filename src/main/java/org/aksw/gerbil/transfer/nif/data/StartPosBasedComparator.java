package org.aksw.gerbil.transfer.nif.data;

import java.util.Comparator;

import org.aksw.gerbil.transfer.nif.Span;

public class StartPosBasedComparator implements Comparator<Span> {

    @Override
    public int compare(Span a1, Span a2) {
        int diff = a1.getStartPosition() - a2.getStartPosition();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
