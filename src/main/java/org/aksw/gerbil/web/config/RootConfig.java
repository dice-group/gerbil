package org.aksw.gerbil.web.config;

import java.io.PrintStream;

import org.aksw.gerbil.utils.ConsoleLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.web.config")
@PropertySource("gerbil.properties")
public class RootConfig {

    {
        // FIXME this is an extremly ugly workaround to be able to log the stuff coming from the BAT-Framework
        replaceSystemStreams();
    }

    protected static void replaceSystemStreams() {
        System.setOut(new PrintStream(new ConsoleLogger(false), true)); 
        System.setErr(new PrintStream(new ConsoleLogger(true), true)); 
    }
    
    static @Bean public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] {
                new ClassPathResource("gerbil.properties"),
        };
        p.setLocations(resourceLocations);
        return p;
    }
}
