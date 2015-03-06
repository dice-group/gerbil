package org.aksw.gerbil.io.nif.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.EndPosBasedComparator;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;

public class NIFPositionHelper {

    /**
     * The positions in NIF are measured in codepoints, while Java counts in
     * terms of characters. So we have to correct the positions of the
     * annotations.
     * 
     * @param resultDocument
     */
    public static void correctAnnotationPositions(Document resultDocument) {
        List<Span> spans = resultDocument.getMarkings(Span.class);
        Collections.sort(spans, new StartPosBasedComparator());
        List<Span> annotationsSortedByEnd = new ArrayList<Span>(spans);
        Collections.sort(annotationsSortedByEnd, new EndPosBasedComparator());
        int startPositions[] = new int[spans.size()];
        int endPositions[] = new int[spans.size()];
        Span currentAnnotation;
        for (int i = 0; i < spans.size(); ++i) {
            startPositions[i] = spans.get(i).getStartPosition();
            currentAnnotation = annotationsSortedByEnd.get(i);
            endPositions[i] = currentAnnotation.getStartPosition() + currentAnnotation.getLength();
        }
        String text = resultDocument.getText();
        int codePointsCount = 0;
        int posInStart = 0, posInEnd = 0;
        for (int i = 0; i < text.length(); ++i) {
            codePointsCount += text.codePointCount(i, i + 1);
            while ((posInStart < startPositions.length) && (codePointsCount > startPositions[posInStart])) {
                spans.get(posInStart).setStartPosition(i);
                ++posInStart;
            }
            while ((posInEnd < endPositions.length) && (codePointsCount > endPositions[posInEnd])) {
                currentAnnotation = annotationsSortedByEnd.get(posInEnd);
                currentAnnotation.setLength(i - currentAnnotation.getStartPosition());
                ++posInEnd;
            }
        }
    }
}
