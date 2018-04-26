package org.aksw.gerbil.annotator.impl.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceListBasedAnnotator extends AbstractAnnotator implements SWCTask1System, SWCTask2System {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(InstanceListBasedAnnotator.class);

    protected Map<String, Model> uriInstanceMapping;

    protected List<Model> model;
    
    
    public InstanceListBasedAnnotator(String annotatorName, List<Model> instances) {
        super(annotatorName);
        this.uriInstanceMapping = new HashMap<String, Model>(instances.size());
        model = instances;
    }


    @Override
    public List<Model> performTask1(Model model) throws GerbilException {
        return this.model;
    }

    @Override
    public List<Model> performTask2(Model model) throws GerbilException {
        return this.model;
    }
}
