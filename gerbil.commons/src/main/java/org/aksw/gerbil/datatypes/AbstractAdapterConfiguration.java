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
package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.utils.ExperimentTypeComparator;

public abstract class AbstractAdapterConfiguration implements AdapterConfiguration {

    private static final ExperimentTypeComparator EXP_TYPE_COMPARATOR = new ExperimentTypeComparator();

    protected String name;
    protected boolean couldBeCached;
    protected ExperimentType applicableForExperiment;

    public AbstractAdapterConfiguration(String name, boolean couldBeCached, ExperimentType applicableForExperiment) {
        this.name = name;
        this.couldBeCached = couldBeCached;
        this.applicableForExperiment = applicableForExperiment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean couldBeCached() {
        return couldBeCached;
    }

    @Override
    public void setCouldBeCached(boolean couldBeCached) {
        this.couldBeCached = couldBeCached;
    }

    @Override
    public boolean isApplicableForExperiment(ExperimentType type) {
        return applicableForExperiment.equalsOrContainsType(type);
    }

    @Override
    public ExperimentType getExperimentType() {
        return applicableForExperiment;
    }

    @Override
    public int compareTo(AdapterConfiguration o) {
        int result = EXP_TYPE_COMPARATOR.compare(this.applicableForExperiment, o.getExperimentType());
        if (result == 0) {
            return this.getName().compareTo(o.getName());
        } else {
            return result;
        }
    }
}
