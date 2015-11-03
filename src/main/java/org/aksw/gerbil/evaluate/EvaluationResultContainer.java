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
package org.aksw.gerbil.evaluate;

import java.util.ArrayList;
import java.util.List;

public class EvaluationResultContainer implements EvaluationResult {

	private List<EvaluationResult> results;

	public EvaluationResultContainer() {
		results = new ArrayList<EvaluationResult>();
	}

	/**
	 * Copy constructor. Makes a shallow copy of the evaluation result list of
	 * the given container.
	 * 
	 * @param container
	 */
	public EvaluationResultContainer(EvaluationResultContainer container) {
		results = new ArrayList<EvaluationResult>(container.results);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getValue() {
		return results;
	}

	public void addResult(EvaluationResult result) {
		results.add(result);
	}

	public void addResults(EvaluationResult... results) {
		for (int i = 0; i < results.length; i++) {
			this.results.add(results[i]);
		}
	}

	public List<EvaluationResult> getResults() {
		return results;
	}

}
