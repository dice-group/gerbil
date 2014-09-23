package org.aksw.gerbil.bat.annotator;


import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.systemPlugins.TimingCalibrator;
import it.acubelab.batframework.utils.AnnotationException;
import it.uniroma1.lcl.babelfy.Babelfy;
import it.uniroma1.lcl.babelfy.Babelfy.AccessType;
import it.uniroma1.lcl.babelfy.Babelfy.Matching;
import it.uniroma1.lcl.babelfy.BabelfyKeyNotValidOrLimitReached;
import it.uniroma1.lcl.babelfy.data.BabelSynsetAnchor;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;

import com.google.common.collect.Sets;

public class BabelfyAnnotator implements D2WSystem {

	private long calib = -1;
	private long lastTime = -1;


	public String getName() {
		return "BabelFy";
	}


	public long getLastAnnotationTime() {
		if (calib == -1)
			calib = TimingCalibrator.getOffset(this);
		return lastTime - calib > 0 ? lastTime - calib : 0;
	}

	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
			throws AnnotationException {
		Babelfy bfy = Babelfy.getInstance(AccessType.ONLINE);
		HashSet<Annotation> annotations = Sets.newHashSet();
		try {
			it.uniroma1.lcl.babelfy.data.Annotation babelAnnotations = bfy.babelfy("", text, Matching.EXACT, Language.EN);
			for(BabelSynsetAnchor anchor:babelAnnotations.getAnnotations()){
				List<String> uri = anchor.getBabelSynset().getDBPediaURIs(Language.EN);
				int id = DBpediaToWikiId.getId(uri.get(0));
				annotations.add(new Annotation(anchor.getStart(), anchor.getEnd()-anchor.getStart(), id));
			}
			return annotations;
		} catch (IOException | URISyntaxException
				| BabelfyKeyNotValidOrLimitReached e) {
			System.err.printf("Error during usinage of babelfy %s", e);
		}
		return annotations;
	}

}
