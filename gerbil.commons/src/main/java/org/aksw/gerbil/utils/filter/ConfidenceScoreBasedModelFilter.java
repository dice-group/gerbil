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
package org.aksw.gerbil.utils.filter;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Filters markings based on their confidence score. To pass the filter a given
 * {@link ScoredMarking} must have a confidence score that is higher than the
 * given {@link #threshold}. If a {@link Marking} does not implement the
 * {@link ScoredMarking} interface, it will always pass this filter.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 *            the class of the filtered markings
 */
public class ConfidenceScoreBasedModelFilter<T extends Model> extends AbstractModelFilter<T> {

	/**
	 * The threshold used for the filtering.
	 */
	private double threshold;
	private Property confidenceProperty;
	private double max;

	/**
	 * Constructor.
	 * 
	 * @param threshold
	 *            The threshold used for the filtering
	 */
	public ConfidenceScoreBasedModelFilter(double max, double threshold, Property confidenceProperty) {
		this.threshold = threshold;
		this.confidenceProperty = confidenceProperty;
		this.max = max;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public boolean isEntityGood(Resource stmt) {
		if (stmt.hasProperty(confidenceProperty)) {
			return stmt.getProperty(confidenceProperty).getDouble() > threshold;
		} else if(max>threshold) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Set<Resource> retrieveEntities(Model model) {
		return model.listSubjects().toSet();
	}

}
