package ch.kochse.tools.asyncapi.codegen.generator;

import java.util.List;

import ch.kochse.tools.asyncapi.codegen.base.GeneratorContext;
import ch.kochse.tools.asyncapi.codegen.model.ClassDescriptor;

public interface ICodeGenerator {
    void generate(List<ClassDescriptor> pModel, GeneratorContext pCtx);
}
