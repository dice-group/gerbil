package org.aksw.gerbil.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.aksw.gerbil.utils.DocumentTextComparison.DocumentTextComparisonResult;

/**
 * This class can revoke text edits and update the positions of NEs accordingly.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DocumentTextEditRevoker {

    /**
     * This method revoke text edits and update the positions of NEs
     * accordingly. Internally, it uses the Levenstein distance calculation
     * algorithm implemented in {@link DocumentTextComparison} to compare the
     * given document text with the given original text. Based on the comparison
     * result, the named entities in the document are moved to fit to the
     * original text. After that, the document text is set to the original text.
     * 
     * @param document
     *            The document that might have a new text and should be set back
     *            to the given original text.
     * @param originalText
     *            The original text of the document.
     * @return a reference to the given document
     */
    public static Document revokeTextEdits(Document document, String originalText) {
        if (!document.getText().equals(originalText)) {
            DocumentTextComparisonResult comparison = DocumentTextComparison.getLevensteinDistance(document.getText(),
                    originalText);
            updateNEPositions(document, comparison);
            document.setText(originalText);
        }
        return document;
    }

    private static void updateNEPositions(Document document, DocumentTextComparisonResult comparison) {
        List<Span> spans = document.getMarkings(Span.class);
        if (spans.size() == 0) {
            return;
        }
        Collections.sort(spans, new StartPosBasedComparator());
        int currentElement = 0;
        List<Span> currentEditedSpans = new ArrayList<Span>();
        int posDiff = 0;
        int numberOfSteps = comparison.getNumberOfSteps();
        for (int i = 0; i < numberOfSteps; ++i) {
            // First, check how the positions are changed with the next edit
            switch (comparison.getStep(i)) {
            case DELETE: {
                --posDiff;
                // update lengths of currently edited spans
                for (Span s : currentEditedSpans) {
                    s.setLength(s.getLength() - 1);
                }
                break;
            }
            case INSERT: {
                ++posDiff;
                // update lengths of currently edited spans
                for (Span s : currentEditedSpans) {
                    s.setLength(s.getLength() + 1);
                }
                break;
            }
            case SUBSTITUTE: // Nothing to do
            case NONE:
            }

            // check whether a span starts at this position
            while ((currentElement < spans.size()) && (spans.get(currentElement).getStartPosition() <= (i - posDiff))) {
                Span s = spans.get(currentElement);
                currentEditedSpans.add(s);
                s.setStartPosition(i);
                ++currentElement;
            }

            // check whether a span is Ending with this char
            for (int j = currentEditedSpans.size() - 1; j >= 0; --j) {
                Span s = currentEditedSpans.get(j);
                if ((s.getStartPosition() + s.getLength()) <= (i + 1)) {
                    currentEditedSpans.remove(j);
                }
            }
        }
    }

}
