package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.TopicDataset;

import java.util.List;
import java.util.HashSet;


public interface C2WDataset2 extends TopicDataset{

	
	/**Note: this value should not be used as a parameter for the taggers.
	 * @return the number of annotations in the whole dataset.
	 */
	public int getTagsCount();
	
	public List<HashSet<Tag2>> getC2WGoldStandardList();
	
}
