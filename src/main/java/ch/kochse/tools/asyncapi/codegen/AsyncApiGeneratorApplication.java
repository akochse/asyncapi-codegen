package ch.kochse.tools.asyncapi.codegen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.kochse.tools.asyncapi.codegen.ctrl.ArtefaktGenerator;

@SpringBootApplication
public class AsyncApiGeneratorApplication implements CommandLineRunner {
    @Autowired
    ArtefaktGenerator 	generator;

    public static void main(String[] pArgs) {
        SpringApplication app = new SpringApplication(AsyncApiGeneratorApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(pArgs);
    }

    @Override
    public void run(String... args) throws Exception {
        generator.run(args);
    }
}
