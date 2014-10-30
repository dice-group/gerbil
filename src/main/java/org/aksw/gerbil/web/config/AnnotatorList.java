package org.aksw.gerbil.web.config;

import java.util.List;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;

@Deprecated
public class AnnotatorList extends AdapterList<AnnotatorConfiguration> {

    private List<AnnotatorConfiguration> configurations;

    public AnnotatorList(List<AnnotatorConfiguration> configurations) {
        super(configurations);
        this.configurations = configurations;
    }

}
