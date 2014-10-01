package org.aksw.gerbil.web;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.AnnotatorName2ExperimentTypeMapping;
import org.aksw.gerbil.utils.DatasetName2ExperimentTypeMapping;
import org.aksw.gerbil.utils.IDCreator;
import org.aksw.gerbil.utils.Name2AnnotatorMapping;
import org.aksw.gerbil.utils.Name2DatasetMapping;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "file:src/main/resources/spring/database/database-context.xml" })
@Controller
public class MainController {
    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;

    @RequestMapping("/config")
    public ModelAndView config() {
        ModelAndView model = new ModelAndView();
        model.setViewName("config");
        return model;
    }

    @RequestMapping("/about")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView();
        model.setViewName("about");
        return model;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * expects a string like
     * {"type":"A2W","matching":"Mw - weak annotation match","annotator":["A2w one","A2W two"],"dataset":["datasets"]}
     * 
     * @param experimentData
     * @return
     */

    @RequestMapping("/execute")
    public @ResponseBody
    String execute(@RequestParam(value = "experimentData") String experimentData) {
        System.out.println(experimentData);
        Object obj = JSONValue.parse(experimentData);
        JSONObject configuration = (JSONObject) obj;
        String type = (String) configuration.get("type");
        String matching = (String) configuration.get("matching");
        JSONArray jsonAnnotators = (JSONArray) configuration.get("annotator");
        String[] annotators = new String[jsonAnnotators.size()];
        for (int i = 0; i < jsonAnnotators.size(); i++) {
            annotators[i] = (String) jsonAnnotators.get(i);
        }
        JSONArray jsonDataset = (JSONArray) configuration.get("dataset");
        String[] datasets = new String[jsonAnnotators.size()];
        for (int i = 0; i < jsonDataset.size(); i++) {
            datasets[i] = (String) jsonDataset.get(i);
        }
        ExperimentTaskConfiguration[] configs = new ExperimentTaskConfiguration[annotators.length * datasets.length];
        int count = 0;
        for (String annotator : annotators) {
            for (String dataset : datasets) {
                configs[count] = new ExperimentTaskConfiguration(Name2AnnotatorMapping.getAnnotatorConfig(annotator),
                        Name2DatasetMapping.getAnnotatorConfig(dataset), ExperimentType.valueOf(type),
                        Matching.valueOf(matching));
            }
        }
        // TODO Micha gib mir ne ID
        String experimentId = IDCreator.getInstance().createID();
        Experimenter exp = new Experimenter(SingletonWikipediaApi.getInstance(), dao, configs, experimentId);
        exp.run();

        return experimentId;
    }

    @RequestMapping("/experiment")
    public ModelAndView experiment(@RequestParam(value = "id") int id) {
        ModelAndView model = new ModelAndView();
        model.setViewName("experiment");
        List<ExperimentTaskResult> tasks = Lists.newArrayList();
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            if (i < 8) {
                tasks.add(new ExperimentTaskResult("annotator1", "dataset" + i, ExperimentType.D2W,
                        Matching.STRONG_ANNOTATION_MATCH, new double[] { random.nextFloat(), random.nextFloat(),
                                random.nextFloat(), random.nextFloat(), random.nextFloat(),
                                random.nextFloat() }, ExperimentDAO.TASK_FINISHED, random.nextInt()));
            } else {
                tasks.add(new ExperimentTaskResult("annotator1", "dataset" + i, ExperimentType.D2W,
                        Matching.STRONG_ANNOTATION_MATCH, new double[6],
                        i == 9 ? ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET : ErrorTypes.UNEXPECTED_EXCEPTION
                                .getErrorCode(), 0));
            }
        }
        model.addObject("tasks", tasks);
        for (ExperimentTaskResult ex : tasks) {
            System.out.println(ex);
        }
        return model;
    }

    @RequestMapping("/exptypes")
    public @ResponseBody
    List<ExperimentType> expTypes() {
        return Arrays.asList(ExperimentType.values());
    }

    @RequestMapping("/matchings")
    public @ResponseBody
    Set<String> matchingsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = ExperimentType.valueOf(experimentType);
        switch (type) {
        case C2W:
            return Sets.newLinkedHashSet(Lists.newArrayList("Me - strong entity match"));
        case D2W:
            // Mw will not be shown since the positions are always exact and thus it works like Ma
            return Sets.newLinkedHashSet(Lists.newArrayList("Ma - strong annotation match"));
        case A2W:
            return Sets.newLinkedHashSet(Lists.newArrayList("Mw - weak annotation match",
                    "Ma - strong annotation match"));
        case Rc2W:
            return Sets.newLinkedHashSet(Lists.newArrayList("Me - strong entity match"));
        case Sc2W:
            return Sets.newLinkedHashSet(Lists.newArrayList("Me - strong entity match"));
        case Sa2W:
            return Sets.newLinkedHashSet(Lists.newArrayList("Mw - weak annotation match",
                    "Ma - strong annotation match"));
        default:
            return Sets.newLinkedHashSet(Lists.newArrayList("None"));
        }
    }

    @RequestMapping("/annotators")
    public @ResponseBody
    Set<String> annotatorsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        return AnnotatorName2ExperimentTypeMapping.getAnnotatorsForExperimentType(ExperimentType
                .valueOf(experimentType));
    }

    @RequestMapping("/datasets")
    public @ResponseBody
    Set<String> datasets(@RequestParam(value = "experimentType") String experimentType) {
        return DatasetName2ExperimentTypeMapping.getDatasetsForExperimentType(ExperimentType.valueOf(experimentType));
    }

}
