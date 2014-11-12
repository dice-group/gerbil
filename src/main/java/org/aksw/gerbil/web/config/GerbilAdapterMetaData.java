package org.aksw.gerbil.web.config;

import org.aksw.gerbil.datatypes.ExperimentType;

public interface GerbilAdapterMetaData {

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
}
