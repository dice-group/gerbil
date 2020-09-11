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

import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.NLG.NLGEvaluator;
import org.aksw.gerbil.evaluate.impl.NLG.RDFToTextEvaluator;
import org.aksw.gerbil.evaluate.impl.webnlg.IREvaluator;
import org.aksw.gerbil.evaluate.impl.webnlg.TextToRDFEvaluator;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.web.config.RootConfig;
import org.apache.jena.rdf.model.ModelFactory;

public class EvaluatorFactory {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(EvaluatorFactory.class);

    protected UriKBClassifier globalClassifier = null;
    protected SubClassInferencer inferencer = null;

    public EvaluatorFactory() {
        this(null, null);
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier) {
        this(globalClassifier, null);
    }

    public EvaluatorFactory(SubClassInferencer inferencer) {
        this(null, inferencer);
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        if (globalClassifier != null) {
            this.globalClassifier = globalClassifier;
        } else {
            this.globalClassifier = RootConfig.createDefaultUriKBClassifier();
        }
        if (inferencer != null) {
            this.inferencer = inferencer;
        } else {
            this.inferencer = new SimpleSubClassInferencer(ModelFactory.createDefaultModel());
        }
    }

    @SuppressWarnings("rawtypes")
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset) {
        return createEvaluator(type, configuration, dataset, globalClassifier, inferencer);
    }

    @SuppressWarnings({ "rawtypes" })
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier classifier, SubClassInferencer inferencer) {
        switch (type) {
       // case MT:
            case NLG:
                return new NLGEvaluator();
            case IR:
                return new IREvaluator(configuration);
        case WebNLG_RDF2Text:
            return new RDFToTextEvaluator();
        case WebNLG_Text2RDF:
            return new TextToRDFEvaluator(configuration);
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }

    protected void addSubTaskEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
//        ExperimentTaskConfiguration subTaskConfig;
        switch (configuration.type) {
       // case MT:
        case WebNLG_Text2RDF:

            case Ent_Type:
            case Partial:
            case Strict:
            case Exact:

        case WebNLG_RDF2Text:
            case NLG:
            case IR:
            return;
        default: {
            throw new RuntimeException();
        }
        }
    }

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
}
