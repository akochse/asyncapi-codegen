package ch.kochse.tools.asyncapi.codegen.ctrl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.kochse.tools.asyncapi.codegen.base.GeneratorContext;
import ch.kochse.tools.asyncapi.codegen.generator.GeneratorFactory;
import ch.kochse.tools.asyncapi.codegen.generator.ICodeGenerator;
import ch.kochse.tools.asyncapi.codegen.model.ClassDescriptor;
import ch.kochse.tools.asyncapi.codegen.parser.YamlParser;

@Service
public class ArtefaktGenerator {
    private final static Logger log = LoggerFactory.getLogger(ArtefaktGenerator.class);
    @Autowired
    private GeneratorContext ctx;
    private YamlParser  parser;

    public ArtefaktGenerator() {
    }


    /**
     *
     * --input <file> // asyncapi.yaml with the async API specification default: src/resource/test/api/asyncapi.yaml
     * --output  <directory> // root-Path for the generated java code
     * --package <package>  // package prefix for the generated data default: ch.kochse.app.example.contract
     * --dto-package <package> // package suffix for the generated dto default: contract
     * --msg-package <package>
     * @param pArgs
     */
    public void run(String... pArgs) {
        ctx.parseArguments(pArgs);
        parser = new YamlParser(ctx);

        try {

            List<ClassDescriptor> model = parser.parse();
            ICodeGenerator gen = GeneratorFactory.getGeneratorFor(ctx.target());
            gen.generate(model, ctx);
            System.out.printf("AsyncApiGenerator: Successfully generated code for %s to %s\n", ctx.inputFilename(), ctx.outputDir());
        } catch(Exception pEx) {
            log.error("Problem in code Generator err={}\n", pEx);
            log.error("GeneratorContext used={}\n", ctx.toString());
        }
    }


}
