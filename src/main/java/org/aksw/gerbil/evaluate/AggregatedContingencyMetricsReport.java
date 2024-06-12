/**
 * Represents an aggregate report of evaluation results containing multiple extended contingency metrics.
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

/**
 * Represents an aggregate report of evaluation results containing multiple extended contingency metrics.
 */
public class AggregatedContingencyMetricsReport implements EvaluationResult{

    /** The name of the evaluation result. */
    private String name;

    /** The list of extended contingency metrics comprising the evaluation result. */
    private List<ExtendedContingencyMetrics> value;

    /**
     * Constructs an instance of AggregateContingencyMetricsReport with the given name.
     *
     * @param name The name of the evaluation result.
     */
    public AggregatedContingencyMetricsReport(String name) {
        this.name = name;
        this.value = new ArrayList<ExtendedContingencyMetrics>();
    }

    /**
     * Constructs an instance of AggregateContingencyMetricsReport with the given name and initial set of extended metrics.
     *
     * @param name The name of the evaluation result.
     * @param value An array of ExtendedContingencyMetrics representing the initial set of extended metrics.
     */
    public AggregatedContingencyMetricsReport(String name, ExtendedContingencyMetrics[] value) {
        this(name);
        this.addMetrics(value);
    }

    /**
     * Retrieves the name of the evaluation result.
     * @return The name of the evaluation result.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of extended metrics comprising the evaluation result.
     * @return The list of extended metrics.
     */
    @Override
    public List<ExtendedContingencyMetrics> getValue() {
        return value;
    }

    /**
     * Adds one or more extended metrics to the evaluation result.
     * @param metrics One or more ExtendedContingencyMetrics objects to be added to the evaluation result.
     */
    public void addMetrics(ExtendedContingencyMetrics... metrics) {
        for(ExtendedContingencyMetrics metric : metrics){
            this.value.add(metric);
        }
    }
}
