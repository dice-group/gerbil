/**
 * (C) Copyright 2012-2013 A-cube lab - Universit?? di Pisa - Dipartimento di Informatica. 
 * BAT-Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * BAT-Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with BAT-Framework.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.aksw.gerbil.bat;

import it.acubelab.batframework.cache.BenchmarkCache;
import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.datasetPlugins.IITBDataset;
import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.MetricsResultSet;
import it.acubelab.batframework.metrics.StrongAnnotationMatch;
import it.acubelab.batframework.problems.D2WDataset;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.systemPlugins.AgdistisAnnotator;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.DumpResults;
import it.acubelab.batframework.utils.Pair;
import it.acubelab.batframework.utils.RunExperiments;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.HashMap;
import java.util.Vector;

import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;

public class D2wBatchMain {

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        // Use system DNS
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");

        // Cache retrieved annotations here
        BenchmarkCache.useCache("benchmark/cache/results.cache");

        System.out.println(BenchmarkCache.getCacheInfo());

        System.out.println("Creating the API to wikipedia...");
        WikipediaApiInterface wikiApi = new WikipediaApiInterface("benchmark/cache/wid.cache",
                "benchmark/cache/redirect.cache");
        DBPediaApi dbpApi = new DBPediaApi();

        System.out.println("Creating the taggers...");
        // D2WSystem tagme = new TagmeAnnotator("benchmark/configs/tagme/config.xml");
        // D2WSystem illinois = new IllinoisAnnotator_Server();
        // D2WSystem miner = new
        // WikipediaMinerAnnotator("/home/benchmark/bat-framework/dist/benchmark/configs/wikipediaminer/config.xml");
        // D2WSystem aidaLocal = new
        // AIDALocalAnnotator("/home/benchmark/bat-framework/dist/benchmark/configs/aida/config.xml", wikiApi);
        // D2WSystem aidaPrior = new
        // AIDAPriorityOnlyAnnotator("/home/benchmark/bat-framework/dist/benchmark/configs/aida/config.xml", wikiApi);
        // D2WSystem aidaCocktail = new
        // AIDACockailPartyAnnotator("/home/benchmark/bat-framework/dist/benchmark/configs/aida/config.xml", wikiApi);
        // D2WSystem spotLight = new SpotlightAnnotator(dbpApi, wikiApi);
        D2WSystem agdistis = new AgdistisAnnotator(wikiApi);
        D2WSystem babelfy = new BabelfyAnnotator();

        System.out.println("Loading the datasets...");
        // D2WDataset aquaintDs = new AQUAINTDataset("benchmark/datasets/AQUAINT/RawTexts",
        // "/home/benchmark/bat-framework/dist/benchmark/datasets/AQUAINT/Problems", wikiApi);
        // D2WDataset aidaDs = new
        // ConllAidaDataset("/home/benchmark/bat-framework/dist/benchmark/datasets/aida/AIDA-YAGO2-dataset.tsv",
        // wikiApi);
        // D2WDataset msnbcDs = new MSNBCDataset("benchmark/datasets/MSNBC/RawTextsSimpleChars_utf8",
        // "/home/benchmark/bat-framework/dist/benchmark/datasets/MSNBC/Problems", wikiApi);
        D2WDataset iitbDs = new IITBDataset("benchmark/datasets/iitb/crawledDocs",
                "benchmark/datasets/iitb/CSAW_Annotations.xml", wikiApi);
        // D2WDataset reuters = new NIFDataset("benchmark/nif/Reuters.ttl", "reuters");
        // D2WDataset rss = new NIFDataset("/home/benchmark/bat-framework/benchmark/nif/RSS.ttl", "rss");
        // D2WDataset rss = new NIFDataset("/home/benchmark/bat-framework/benchmark/nif/News.ttl", "news");

        /** Create a vector containing all the A2W datasets */
        Vector<D2WDataset> dssA2W = new Vector<D2WDataset>();
        dssA2W.add(iitbDs);
        // dssA2W.add(msnbcDs);
        // dssA2W.add(aquaintDs);
        // dssA2W.add(aidaDs);
        // dssA2W.add(reuters);
        // dssA2W.add(rss);
        // dssA2W.add(rss);

        /** Create a vector containing all the D2W annotators */
        Vector<D2WSystem> d2wAnnotators = new Vector<D2WSystem>();
        // sa2wAnnotators.add(aidaLocal);
        // d2wAnnotators.add(tagme);
        // d2wAnnotators.add(illinois);
        // sa2wAnnotators.add(aidaCocktail);
        // sa2wAnnotators.add(aidaPrior);
        // d2wAnnotators.add(spotLight);
        // d2wAnnotators.add(miner);
        d2wAnnotators.add(agdistis);
        d2wAnnotators.add(babelfy);

        /** Create the match relations */
        // MatchRelation<Annotation> wam = new WeakAnnotationMatch(wikiApi);
        MatchRelation<Annotation> sam = new StrongAnnotationMatch(wikiApi);
        // MatchRelation<Annotation> cam = new ConceptAnnotationMatch(wikiApi);
        // MatchRelation<Annotation> mam = new MentionAnnotationMatch();

        /*********** A2W experiments ************/

        /** Create a vector containing the match relations we want to base our measurements for the A2W Experiment on. */
        Vector<MatchRelation<Annotation>> matchRelationsA2W = new Vector<MatchRelation<Annotation>>();
        // matchRelationsA2W.add(wam);
        matchRelationsA2W.add(sam);
        // matchRelationsA2W.add(cam);
        // matchRelationsA2W.add(mam);

        /**
         * Hashmap for saving the measurements results.
         * The mapping will be: metric name -> tagger name -> dataset name -> threshold -> results set
         */
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> resD2W;

        /** Run the experiments for varying thresholds, store the resulting measures to resD2W */
        resD2W = RunExperiments.performD2WExpVarThreshold(d2wAnnotators, null, dssA2W, wikiApi);

        Pair<Float, MetricsResultSet> p = RunExperiments.getBestRecord(resD2W, sam.getName(), agdistis.getName(),
                iitbDs.getName());
        System.out.printf("The best micro-F1 for %s on %s is achieved with a threshold of $.3f. Its value is %.3f.%n",
                agdistis.getName(), iitbDs.getName(), p.first, p.second.getMicroF1());

        /** Print the results about correctness (F1, precision, recall) to the screen */
        DumpResults.printCorrectnessPerformance(matchRelationsA2W, null, null, null, d2wAnnotators, dssA2W, resD2W);

        /** Output the results in a gnuplot data .dat file that can then be given to Gnuplot */
        // DumpResults.gnuplotCorrectnessPerformance(matchRelationsA2W, d2wAnnotators, dssA2W, wikiApi, resD2W);

        /** Timing tables in two forms */
        Vector<D2WDataset> dss = new Vector<D2WDataset>();
        dss.addAll(dssA2W);
        // DumpResults.latexTimingPerformance(null, d2wAnnotators, null, dss);
        // DumpResults.latexTimingPerformance2(d2wAnnotators, null, dss);

        /** F1-runtime plot, just for the IITB dataset. */
        // DumpResults.gnuplotRuntimeF1(wam.getName(), null, d2wAnnotators, iitbDs.getName(), wikiApi, resA2W);

        /** Output annotations similarity in a latex table. */
        // DumpResults.latexSimilarityA2W(dssA2W, d2wAnnotators, resA2W, wikiApi);

        wikiApi.flush();
    }

}
