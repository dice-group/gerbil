package org.aksw.gerbil.web.config;

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
@ComponentScan(basePackages = { "org.aksw.gerbil.web", "org.aksw.gerbil.datasets.datahub" })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

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
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/webResources/**").addResourceLocations("classpath:webResources/");
        registry.addResourceHandler("/dataId/corpora/**").addResourceLocations("classpath:dataId/corpora/");
        registry.addResourceHandler("/dataId/annotators/**").addResourceLocations("classpath:dataId/annotators/");
        registry.addResourceHandler("/vocab/**").addResourceLocations("classpath:vocab/");
    }

}
