package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.problems.TopicDataset;

import java.util.HashSet;
import java.util.List;

public interface D2WDataset2 extends TopicDataset {
	public List<HashSet<Mention>> getMentionsInstanceList();

	public List<HashSet<Annotation2>> getD2WGoldStandardList();
}
