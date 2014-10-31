package org.aksw.gerbil.bat.annotator;

import fr.eurecom.nerd.client.NERD;
import fr.eurecom.nerd.client.schema.Entity;
import fr.eurecom.nerd.client.type.DocumentType;
import fr.eurecom.nerd.client.type.ExtractorType;
import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.systemPlugins.TimingCalibrator;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class NERDAnnotator implements Sa2WSystem{
	
    private static final Logger LOGGER = LoggerFactory.getLogger(NERDAnnotator.class);

	//private final String NERD_API = "http://nerd.eurecom.fr/api";
    private final String NERD_API = "http://localhost:8888/api";
    
    private String key;
	private long lastTime = -1;
	private long calib = -1;
	
	public NERDAnnotator(String key) {
		this.key = key;
	}

	@Override
	public String getName() {
		return "NERD";
	}
	
	@Override
	public HashSet<Annotation> solveA2W(String text) throws AnnotationException 
	{
		return ProblemReduction.Sa2WToA2W(solveSa2W(text), Float.MIN_VALUE);
	}

	@Override
	public HashSet<Tag> solveC2W(String text) throws AnnotationException 
	{
		return ProblemReduction.A2WToC2W(solveA2W(text));
	}

	@Override
	public long getLastAnnotationTime() 
	{
		if (calib == -1)
			calib = TimingCalibrator.getOffset(this);
		return lastTime - calib > 0 ? lastTime - calib  : 0;
	}

	@Override
	public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException 
	{
		return ProblemReduction.Sa2WToSc2W(this.solveSa2W(text));
	}

	@Override
	public HashSet<ScoredAnnotation> solveSa2W(String text)	throws AnnotationException 
	{
		return getNERDAnnotations(text);
	}
	
	@Override
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
	throws AnnotationException 
	{
		HashSet<ScoredAnnotation> anns = getNERDAnnotations(text);
		HashSet<Annotation> result = new HashSet<Annotation>();
		
		//FIXME
		//naive implementation that iterates through the list of mentions and gets,
		//if available, the wiki link for that mention 
		for (Mention m : mentions) {
			for (ScoredAnnotation a : anns) 
			{
				if( m.getPosition() == a.getPosition() )
					result.add(new Annotation(a.getPosition(), a.getLength(), a.getConcept()));
			}
		}

		return result;
	}
	
	/**
	 * Send request to NERD and parse the response as a set of scored annotations. 
	 * 
	 * @param text
	 *            the text to send
	 */
	public HashSet<ScoredAnnotation> getNERDAnnotations(String text) 
	{
		HashSet<ScoredAnnotation> annotations = Sets.newHashSet();
		try{
			lastTime = Calendar.getInstance().getTimeInMillis();
			
			NERD nerd = new NERD(NERD_API, key);
			List<Entity> entities = nerd.annotate(
										ExtractorType.COMBINED, 
										DocumentType.PLAINTEXT, 
										text
									);

			for (Entity e : entities) {
				int id = DBpediaToWikiId.getId(e.getUri());

                annotations.add( new ScoredAnnotation(
                		                e.getStartChar(), 
                		                e.getEndChar()-e.getStartChar(), 
                		                id, 
                		                new Float(e.getConfidence()))
                			    );
			}
		}
		catch (Exception e){
			e.printStackTrace();
			
			//TODO
			//fix the error handling in order to closely check what is the source of the error
			throw new AnnotationException("An error occurred while querying "+
										  this.getName()+ 
										  " API. Message: " + 
										  e.getMessage()
										 );
		}
		
		return annotations;

	}	
}
