package ch.kochse.tools.asyncapi.codegen.base;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "ch.kochse.tools.asyncapi.codegen")

public class ApplicationConfiguration {	
    @Value("${service.shutdown.key:}")
    private String shutdownkey;

}