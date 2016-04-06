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

/**
 * Interface of an adpater configuration of the GERBIL system. It represents the
 * adapter and is able to create an adapter instance.
 * 
 * @author Michael Röder
 * 
 */
public interface AdapterConfiguration extends Comparable<AdapterConfiguration>{

    /**
     * Getter of the adapters name.
     * 
     * @return The name of the adapter.
     */
    public String getName();

    /**
     * Setter of the adapters name.
     * 
     * @param name
     *            The name of the adapter.
     */
    public void setName(String name);

    /**
     * Returns true if the system is allowed to cache the results of experiments
     * in which this adapter has been involved.
     * 
     * @return true if the results could be cached inside the database.
     *         Otherwise false is returned.
     */
    public boolean couldBeCached();

    /**
     * Setter for the caching flag which should be set to true if the system is
     * allowed to cache the results of experiments in which this adapter has
     * been involved.
     * 
     * @param couldBeCached
     */
    public void setCouldBeCached(boolean couldBeCached);

    /**
     * Returns true if this adapter can be used for an experiment of the given
     * type.
     * 
     * @param type
     *            the experiment type that should be checked
     * @return true if this adapter can be used for an experiment of the given
     *         type.
     */
    public boolean isApplicableForExperiment(ExperimentType type);
    
    public ExperimentType getExperimentType();
}
