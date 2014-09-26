package org.aksw.gerbil.web;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class MainController {
	@RequestMapping("/")
	private ModelAndView index(){
		ModelAndView model = new ModelAndView();
		model.setViewName("config");
		Map<String,Integer> types = Maps.newHashMap();
		types.put( "News",1);
		types.put( "Products",2);
		model.addObject("types", types);
		
		Map<String,String> news =  Maps.newHashMap();
		news.put("news1","news1");
		news.put("news2","news2");
		model.addObject("news", news);
		
		Map<String,String> products =  Maps.newHashMap();
		
		products.put("p1","p1");
		products.put("p2","p2");
		model.addObject("products", products);
		model.addObject("command",new Choice());
		return model;
	}
}
