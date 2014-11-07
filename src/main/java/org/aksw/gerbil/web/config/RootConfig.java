package org.aksw.gerbil.web.config;

import java.io.PrintStream;

import org.aksw.gerbil.utils.ConsoleLogger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.web.config")
public class RootConfig {

    {
        // FIXME this is an extremly ugly workaround to be able to log the stuff coming from the BAT-Framework
        replaceSystemStreams();
    }

    protected static void replaceSystemStreams() {
        System.setOut(new PrintStream(new ConsoleLogger(false), true)); 
        System.setErr(new PrintStream(new ConsoleLogger(true), true)); 
    }
}
