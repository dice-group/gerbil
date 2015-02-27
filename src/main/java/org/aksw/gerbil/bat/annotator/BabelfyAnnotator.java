/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;
import it.uniroma1.lcl.babelfy.commons.BabelfyConstraints;
import it.uniroma1.lcl.babelfy.commons.BabelfyParameters;
import it.uniroma1.lcl.babelfy.commons.IBabelfy;
import it.uniroma1.lcl.babelfy.commons.annotation.CharOffsetFragment;
import it.uniroma1.lcl.babelfy.commons.annotation.DisambiguationConstraint;
import it.uniroma1.lcl.babelfy.commons.annotation.MCS;
import it.uniroma1.lcl.babelfy.commons.annotation.PoStaggingOptions;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotationResource;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotationType;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.jlt.util.Language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * The Babelfy Annotator.
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
public class BabelfyAnnotator implements Sa2WSystem {

//	private static final Logger LOGGER = LoggerFactory.getLogger(BabelfyAnnotator.class);

	private static final int BABELFY_MAX_TEXT_LENGTH = 3500;

	public static final String NAME = "Babelfy";

	// private long calib = -1;
	// private long lastTime = -1;

	@Autowired
	private WikipediaApiInterface wikiApi;

	public BabelfyAnnotator(WikipediaApiInterface wikiApi) {
		this.wikiApi = wikiApi;
	}

	@Override
	public String getName() {
		return NAME;
	}

    public long getLastAnnotationTime() {
        return -1;
    }

	@Override
	public HashSet<Tag> solveC2W(String text) throws AnnotationException {
		return ProblemReduction.Sc2WToC2W(ProblemReduction.Sa2WToSc2W(solveSa2W(text)));
	}

	@Override
	public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
		return ProblemReduction.Sa2WToA2W(solveSa2W(text));
	}

	@Override
	public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
		return ProblemReduction.Sa2WToSc2W(solveSa2W(text));
	}

	@Override
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
			throws AnnotationException {
		return ProblemReduction.Sa2WToD2W(solveGeneralSa2W(text, mentions), mentions, -1.0f);
	}

	@Override
	public HashSet<ScoredAnnotation> solveSa2W(String text)
			throws AnnotationException {
		return solveGeneralSa2W(text, null);
	}

	protected HashSet<ScoredAnnotation> solveGeneralSa2W(String text,
			HashSet<Mention> mentions) throws AnnotationException {
		HashSet<ScoredAnnotation> annotations = Sets.newHashSet();
		List<String> chunks = splitText(text);
		BabelfyParameters bfyParameters = new BabelfyParameters();
		bfyParameters.setAnnotationResource(SemanticAnnotationResource.WIKI);
		bfyParameters.setMCS(MCS.OFF);
		bfyParameters.setExtendCandidatesWithAIDAmeans(true);
		if (mentions != null) {
			bfyParameters.setThreshold(0.0);
			bfyParameters.setPoStaggingOptions(PoStaggingOptions.INPUT_FRAGMENTS_AS_NOUNS);
			bfyParameters.setDisambiguationConstraint(
					DisambiguationConstraint.DISAMBIGUATE_ALL_RETURN_INPUT_FRAGMENTS);
		} else {
			bfyParameters.setThreshold(0.3);
			bfyParameters.setAnnotationType(SemanticAnnotationType.NAMED_ENTITIES);
		}

		IBabelfy bfy = new Babelfy(bfyParameters);
		int prevChars = 0;
		for (String chunk : chunks) {
			BabelfyConstraints constraints = new BabelfyConstraints();
			if (mentions != null) {
				for (Mention m : mentions) {
					if (m.getPosition() >= prevChars &&
							m.getPosition()+m.getLength() < prevChars+chunk.length()) {
						constraints.addFragmentToDisambiguate(
								new CharOffsetFragment(m.getPosition()-prevChars,
										m.getPosition()+m.getLength()-1-prevChars));
					}
				}
			}
			List<SemanticAnnotation> bfyAnnotations = sendRequest(bfy, chunk, constraints);

			for (SemanticAnnotation bfyAnn : bfyAnnotations) {
				int wikiID = -1;
                wikiID = DBpediaToWikiId.getId(wikiApi, bfyAnn.getDBpediaURL());
				if (wikiID >= 0) {
					ScoredAnnotation gerbilAnn =
							new ScoredAnnotation(prevChars+bfyAnn.getCharOffsetFragment().getStart(),
									bfyAnn.getCharOffsetFragment().getEnd()-
									bfyAnn.getCharOffsetFragment().getStart()+1,
									wikiID, (float)bfyAnn.getScore());
					annotations.add(gerbilAnn);
				}
			}
			prevChars += chunk.length();
		}

		return annotations;
	}
	
	protected synchronized List<SemanticAnnotation> sendRequest(IBabelfy bfy, String chunk, BabelfyConstraints constraints) {
	    return bfy.babelfy(chunk, Language.EN, constraints);
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
