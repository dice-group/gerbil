package org.aksw.gerbil.web;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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
		model.addObject("command", new Command());
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

	@RequestMapping("/execute")
	public ModelAndView execute(@ModelAttribute("SpringWeb") Command c) {
		ModelAndView m = new ModelAndView("execute");
		m.addObject("command", c);
		return m;
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

	public static class Command {

		private ExperimentType type;
		private String annotator;
		private String datasets;

		public String getDatasets() {
			return datasets;
		}

		public void setDatasets(String datasets) {
			this.datasets = datasets;
		}

		public String getAnnotator() {
			return annotator;
		}

		public void setAnnotator(String annotator) {
			this.annotator = annotator;
		}

		public ExperimentType getType() {
			return type;
		}

		public void setType(ExperimentType type) {
			this.type = type;
		}

	}

}
