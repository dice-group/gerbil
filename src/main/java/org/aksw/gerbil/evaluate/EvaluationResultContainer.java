/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
