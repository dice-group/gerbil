/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.dataset.impl.t4n;

import org.aksw.gerbil.dataset.impl.generic.GenericDataset;
import org.aksw.gerbil.dataset.impl.generic.XML_DS_TagDef;
import org.aksw.gerbil.exceptions.GerbilException;

public class Tweet4NeedDataset extends GenericDataset {
	private static final String dsName = "tweet4need";
	private static final XML_DS_TagDef TAG_DEF;
	static {
		TAG_DEF = new XML_DS_TagDef("Tweet", "TweetText", "TweetId", "Mentions", "Mention", "StartIndx", null, "Text",
				"Entity");
	}

	public Tweet4NeedDataset(String datasetDir) throws GerbilException {
		super(datasetDir, dsName, TAG_DEF);
	}

}