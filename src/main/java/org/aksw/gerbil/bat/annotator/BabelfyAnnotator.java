package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;
import it.uniroma1.lcl.babelfy.Babelfy;
import it.uniroma1.lcl.babelfy.Babelfy.AccessType;
import it.uniroma1.lcl.babelfy.Babelfy.Matching;
import it.uniroma1.lcl.babelfy.BabelfyKeyNotValidOrLimitReached;
import it.uniroma1.lcl.babelfy.data.BabelSynsetAnchor;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * The BabelFy Annotator.
 * 
 * <p>
 * <i>Andrea Moro: "I recommend to use the maximum amount of available characters (3500) at each request (i.e., try to
 * put a document all together or split it in chunks of 3500 characters) both for scalability and performance
 * reasons."</i><br>
 * This means, that we have to split up documents longer than 3500 characters. Unfortunately, BabelFy seems to measure
 * the length on the escaped text which means that every text could be three times longer than the unescaped text. Thus,
 * we have to set {@link #BABELFY_MAX_TEXT_LENGTH}={@value #BABELFY_MAX_TEXT_LENGTH}.
 * </p>
 */
public class BabelfyAnnotator implements A2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(BabelfyAnnotator.class);

    private static final int BABELFY_MAX_TEXT_LENGTH = 1500;

    public static final String NAME = "Babelfy";

    // private long calib = -1;
    // private long lastTime = -1;
    private String key;

    @Autowired
    private WikipediaApiInterface wikiApi;

    public BabelfyAnnotator() {
        this("");
    }

    public BabelfyAnnotator(WikipediaApiInterface wikiApi) {
        this("", wikiApi);
    }

    public BabelfyAnnotator(String key) {
        this.key = key;
    }

    public BabelfyAnnotator(String key, WikipediaApiInterface wikiApi) {
        this.key = key;
        this.wikiApi = wikiApi;
    }

    public String getName() {
        return NAME;
    }

    public long getLastAnnotationTime() {
        // if (calib == -1)
        // calib = TimingCalibrator.getOffset(this);
        // return lastTime - calib > 0 ? lastTime - calib : 0;
        return -1;
    }

    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
            throws AnnotationException {
        return solveA2W(text, true);
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        return ProblemReduction.A2WToC2W(solveA2W(text));
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        return solveA2W(text, true);
    }

    protected HashSet<Annotation> solveA2W(String text, boolean isShortDocument)
            throws AnnotationException {
        if (text.length() > BABELFY_MAX_TEXT_LENGTH) {
            return solveA2WForLongTexts(text);
        }
        Babelfy bfy = Babelfy.getInstance(AccessType.ONLINE);
        HashSet<Annotation> annotations = Sets.newHashSet();
        // lastTime = Calendar.getInstance().getTimeInMillis();
        try {
            it.uniroma1.lcl.babelfy.data.Annotation babelAnnotations = bfy.babelfy(key, text,
                    isShortDocument ? Matching.PARTIAL : Matching.EXACT, Language.EN);
            int positionInText = 0, posSurfaceFormInText = 0;
            String surfaceForm;
            String lowercasedText = text.toLowerCase();
            for (BabelSynsetAnchor anchor : babelAnnotations.getAnnotations()) {
                // The positions of BabelSynsetAnchors are measured in tokens --> we have to find the token inside the
                // text
                surfaceForm = anchor.getAnchorText();
                posSurfaceFormInText = lowercasedText.indexOf(surfaceForm, positionInText);
                List<String> uris = anchor.getBabelSynset().getDBPediaURIs(Language.EN);
                String uri;
                if ((posSurfaceFormInText >= 0) && (uris != null) && (uris.size() > 0)) {
                    // DIRTY FIX Babelfy seems to return URIs with "DBpedia.org" instead of "dbpedia.org"
                    uri = uris.get(0);
                    uri = uri.replaceAll("DBpedia.org", "dbpedia.org");
                    int id = DBpediaToWikiId.getId(wikiApi, uri);
                    annotations.add(new Annotation(posSurfaceFormInText, surfaceForm.length(), id));
                    // annotations.add(new Annotation(anchor.getStart(), anchor.getEnd() - anchor.getStart(), id));
                    positionInText = posSurfaceFormInText + surfaceForm.length();
                }
            }
        } catch (BabelfyKeyNotValidOrLimitReached e) {
            LOGGER.error("The BabelFy Key is invalid or has reached its limit.", e);
            throw new AnnotationException("The BabelFy Key is invalid or has reached its limit: "
                    + e.getLocalizedMessage());
        } catch (IOException | URISyntaxException e) {
            // LOGGER.error("Exception while requesting annotations from BabelFy. Returning empty Annotation set.", e);
            throw new AnnotationException("Exception while requesting annotations from BabelFy: "
                    + e.getLocalizedMessage());
        }
        // lastTime = Calendar.getInstance().getTimeInMillis() - lastTime;
        return annotations;
    }

    protected HashSet<Annotation> solveA2WForLongTexts(String text) {
        List<String> chunks = splitText(text);
        HashSet<Annotation> annotations;
        annotations = solveA2W(chunks.get(0), false);

        HashSet<Annotation> tempAnnotations;
        int startOfChunk = 0;
        for (int i = 1; i < chunks.size(); ++i) {
            // get annotations. Note that
            tempAnnotations = solveA2W(chunks.get(i), false);
            // We have to correct the positions of the annotations
            startOfChunk += chunks.get(i - 1).length();
            for (Annotation annotation : tempAnnotations) {
                annotations.add(new Annotation(annotation.getPosition() + startOfChunk, annotation.getLength(),
                        annotation.getConcept()));
            }
        }
        return annotations;
    }

    protected List<String> splitText(String text) {
        List<String> chunks = new ArrayList<String>();
        int start = 0, end = 0, nextEnd = 0;
        // As long as we have to create chunks
        while ((nextEnd >= 0) && ((text.length() - nextEnd) > BABELFY_MAX_TEXT_LENGTH)) {
            // We have to use the next space, even it would be too far away
            end = nextEnd = text.indexOf(' ', start + 1);
            // Search for the next possible end this chunk
            while ((nextEnd >= 0) && ((nextEnd - start) < BABELFY_MAX_TEXT_LENGTH)) {
                end = nextEnd;
                nextEnd = text.indexOf(' ', end + 1);
            }
            // Add the chunk
            chunks.add(text.substring(start, end));
            start = end;
        }
        // Add the last chunk
        chunks.add(text.substring(start));
        return chunks;
    }
}
