package org.aksw.gerbil.web;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
public class MainController {

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
	 * @param experimentData
	 * @return
	 */
	@RequestMapping("/execute")
	public ModelAndView execute(@RequestParam(value = "experimentData") String experimentData) {
		System.out.println(experimentData);
		ModelAndView model = new ModelAndView();
		model.setViewName("execute");
		return model;
	}

	@RequestMapping("/exptypes")
	public @ResponseBody
	LinkedList<String> expTypes() {
		LinkedList<String> list = new LinkedList<>();
		for (ExperimentType experimentType : ExperimentType.values()) {
			list.add(experimentType.name());
		}
		Collections.sort(list);
		return list;
	}

	@RequestMapping("/datasets")
	public @ResponseBody
	Set<String> datasets(@RequestParam(value = "experimentType") String experimentType) {
		return Sets.newLinkedHashSet(Lists.newArrayList("datasets"));
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
			return Sets.newLinkedHashSet(Lists.newArrayList("Mw - weak annotation match", "Ma - strong annotation match"));
		case Rc2W:
			return Sets.newLinkedHashSet(Lists.newArrayList("Me - strong entity match"));
		case Sc2W:
			return Sets.newLinkedHashSet(Lists.newArrayList("Me - strong entity match"));
		case Sa2W:
			return Sets.newLinkedHashSet(Lists.newArrayList("Mw - weak annotation match", "Ma - strong annotation match"));
		default:
			return Sets.newLinkedHashSet(Lists.newArrayList("None"));
		}
	}

	@RequestMapping("/annotators")
	public @ResponseBody
	Set<String> annotatorsForExpType(@RequestParam(value = "experimentType") String experimentType) {
		ExperimentType type = ExperimentType.valueOf(experimentType);
		switch (type) {
		case D2W:
			return Sets.newLinkedHashSet(Lists.newArrayList("D2w one", "D2W two"));
		case A2W:
			return Sets.newLinkedHashSet(Lists.newArrayList("A2w one", "A2W two"));
		default:
			return Sets.newLinkedHashSet(Lists.newArrayList("one", "two"));
		}
	}
}
