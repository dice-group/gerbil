package org.aksw.gerbil.datasets;

/**
 * (C) Copyright 2012-2013 A-cube lab - Università di Pisa - Dipartimento di Informatica. 
 * BAT-Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * BAT-Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with BAT-Framework.  If not, see <http://www.gnu.org/licenses/>.
 */


import it.unipi.di.acube.batframework.problems.C2WDataset;
import it.unipi.di.acube.batframework.problems.D2WDataset;

import java.util.List;
import java.util.HashSet;

public interface A2WDataset2 extends C2WDataset2, D2WDataset2{

	/**
	 * Note: the order of the elements in this list must be the same of those returned by getTextIterator().
	 * @return a list of the annotations of the text included in the dataset.
	 */
	public List<HashSet<Annotation2>> getA2WGoldStandardList();


}
