package org.aksw.gerbil.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="ch.cherix.f4j.web")
public class WebMvcConfig extends WebMvcConfigurerAdapter{
	
	private static final transient Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
	/**
	 * @return the view resolver
	 */
	@Bean
	public ViewResolver viewResolver() {
		logger.debug("setting up view resolver");
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@Override
	  public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println("BLUB");
	    registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
	  }
}
