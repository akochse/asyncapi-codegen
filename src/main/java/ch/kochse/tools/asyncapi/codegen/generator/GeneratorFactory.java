package ch.kochse.tools.asyncapi.codegen.generator;

import org.apache.commons.lang3.StringUtils;

public final class GeneratorFactory {
    public static ICodeGenerator getGeneratorFor(String pLanguage) {
        ICodeGenerator gen = null;
        if (StringUtils.isEmpty(pLanguage)) {
            gen = new JavaCodeGenerator();
        } else {
            gen = switch(pLanguage.toLowerCase()) {
                case "java" -> new JavaCodeGenerator();
                default -> null;
            };
        }
        return gen;
    }
}
