/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.EndPosBasedComparator;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFPositionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFPositionHelper.class);

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

        checkPositionsForConspicuity(annotationsSortedByEnd, text);
    }

    public static void checkPositionsForConspicuity(List<Span> spans, String text) {
        int start, end;
        for (Span s : spans) {
            start = s.getStartPosition();
            end = start + s.getLength();
            // make sure that the start position is not a whitespace
            if (Character.isWhitespace(text.charAt(start))) {
                printWarning(text, start, end, "Found an anormal marking that starts with a whitespace");
            }
            // make sure that the character directly in front of the span is no
            // letter
            if ((start > 0) && (Character.isAlphabetic(text.charAt(start - 1)))) {
                printWarning(text, start, end, "Found an anormal marking that has a letter in front of it");
            }
            // make sure that the last character is not a whitespace
            if (Character.isWhitespace(text.charAt(end - 1))) {
                printWarning(text, start, end, "Found an anormal marking that ends with a whitespace");
            }
            // make sure that the character directly behind the span is not a
            // letter
            if ((end < text.length()) && (Character.isAlphabetic(text.charAt(end)))) {
                printWarning(text, start, end, "Found an anormal marking that has a letter directly behind it");
            }
        }
    }

    private static void printWarning(String text, int start, int end, String warningMsg) {
        StringBuilder builder = new StringBuilder();
        int snippetStart;
        builder.append(warningMsg);
        builder.append(": \"");
        if ((start - 20) <= 0) {
            snippetStart = 0;
        } else {
            snippetStart = start - 20;
            builder.append("...");
        }
        builder.append(text.substring(snippetStart, start));
        builder.append('\'');
        builder.append(text.substring(start, end));
        builder.append('\'');
        if ((end + 20) >= text.length()) {
            builder.append(text.substring(end));
        } else {
            builder.append(text.substring(end, end + 20));
            builder.append("...");
        }
        builder.append('"');
        LOGGER.warn(builder.toString());
    }
}
