package ch.kochse.tools.asyncapi.codegen.generator;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import ch.kochse.tools.asyncapi.codegen.base.GeneratorContext;
import ch.kochse.tools.asyncapi.codegen.model.ClassDescriptor;
import ch.kochse.tools.asyncapi.codegen.model.ClassProperty;
import ch.kochse.tools.asyncapi.codegen.model.DescriptorType;
import ch.kochse.tools.asyncapi.codegen.model.PropType;


public class JavaCodeGenerator
implements ICodeGenerator {
    private final static String GENERATION_NOTE = "*** THIS IS GENERATED CODE. DO NOT CHANGE IT, AS IT MIGHT GET OVERRIDEN WITH NEXT BUILD! ***";
    private final static String DTOSFX = "DTO";
    private final static Logger log = LoggerFactory.getLogger(JavaCodeGenerator.class);

    private GeneratorContext ctx;
    private Map<String, ClassDescriptor> index;
    private Map<String, String> msgTypes;

    @Override
    public void generate(List<ClassDescriptor> pModel, GeneratorContext pCtx) {
        ctx = pCtx;
        prepare(pModel);
        generate(pModel);
    }

    private void generate(List<ClassDescriptor> pModel) {
        for (ClassDescriptor cld : pModel) {
            switch(cld.descriptorType()) {
                case DTO: generateDTO(cld, pModel); break;
                case MESSAGE: generateMessage(cld, pModel); break;
                case CLASS: generateClass(cld, pModel); break;
                case ENUM: generateEnum(cld, pModel); break;
                default: {
                    log.warn("Cannot generate Code for '{}' of type '{}'", cld.name(), cld.descriptorType());
                }
            }
        }
    }

    private void prepare(List<ClassDescriptor> pModel) {
        index = new LinkedHashMap<>();
        for (ClassDescriptor cld : pModel) {
            index.put(cld.name(),cld);
            switch(cld.descriptorType()) {
                case DTO: cld.packageName(ctx.dtoPackage()); break;
                case MESSAGE: cld.packageName(ctx.msgPackage()); break;
                case CLASS: cld.packageName(ctx.contractPackage()); break;
                case ENUM: cld.packageName(ctx.enumPackage()); break;
                default: {
                    log.warn("Cannot prepare Code for '{}' of type '{}'", cld.name(), cld.descriptorType());
                }
            }
        }
        createMessageTypeFile(pModel);
    }

    /**
     * Create a MessageType Class and put it into the model
     * @param pModel List<ClassDescriptor> model
     * Create
     *
     * <code>
    package ch.ost.rj.masse.ads.plantit.<domain>.contract;

    import static ch.ost.rj.masse.ads.plantit.common.base.IMessageType.messageType;

    import ch.ost.rj.masse.ads.plantit.common.base.IMessageType;

    public interface ServiceMsgType extends IMessageType {
      IMessageType SIMPLE_REQUEST_MESSAGE = messageType("SIMPLE_REQUEST_MESSAGE", SimpleRequestMsg.class);
    }
    </code>
     */
    private void createMessageTypeFile(List<ClassDescriptor> pModel) {
        msgTypes = new HashMap<>();
        String interfaceName = StringUtils.capitalize(ctx.domainName()).concat("MsgType");
        ClassName msgEleType = ClassName.get(ctx.msgPackage(), "IMessageType");
        try {
            if (StringUtils.hasLength(ctx.msgTypeParentInterface())) {
                String parentClass = ctx.msgTypeParentInterface().substring(ctx.msgTypeParentInterface().lastIndexOf('.') + 1);
                String parentPackage = ctx.msgTypeParentInterface().substring(0, ctx.msgTypeParentInterface().lastIndexOf('.'));
                msgEleType = ClassName.get(parentPackage, parentClass);
            }
            TypeSpec.Builder bldr = TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(msgEleType);

            for (ClassDescriptor cld : pModel) {

                if (DescriptorType.MESSAGE == cld.descriptorType()) {
                    Optional<String> payloadFld = Optional.empty();
                    for (ClassProperty f : cld.fields()) {
                        if (f.refType().endsWith(DTOSFX)) {
                            payloadFld = Optional.of(f.refType());
                            break;
                        }
                    }
                    String msgTypeName = generateUpperCaseName(cld.name());
                    msgTypes.put(cld.name(), interfaceName.concat(".").concat(msgTypeName));
                    FieldSpec element = FieldSpec.builder(msgEleType, msgTypeName).addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("messageType(\""
                                    .concat(msgTypeName)
                                    .concat("\", ")
                                    .concat(cld.name())
                                    .concat(".class, ")
                                    .concat(payloadFld.isPresent()? String.join(".", ctx.basePackage(), ctx.dtoSubPackage(), payloadFld.get(),"class") :"null")
                                    .concat(")"))
                            .build();
                    bldr.addField(element);
                }
            }
            /**
             * Add registerFactory method:
             *     public static boolean registerWithFactory() {
             *             return true;
             *     }
             */
            bldr.addMethod(MethodSpec.methodBuilder("registerWithFactory")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(TypeName.BOOLEAN)
                    .addStatement("return true")
                    .build());
            ;
            JavaFile javaFile = JavaFile.builder(ctx.msgPackage(), bldr.build())
                    .addFileComment(GENERATION_NOTE, "")
                    .addStaticImport(msgEleType, "messageType")
                    .build();
            javaFile.writeTo(new File(ctx.outputDir()));
            log.debug("{}/{}/{}.java created", ctx.outputDir(), StringUtils.replace(ctx.msgPackage(), ".", "/"), interfaceName);
        } catch (Exception pEx) {
            log.error("Error generating MessageType file for {}", interfaceName);
        }

    }
    private void generateDTO(ClassDescriptor pClsDsc, List<ClassDescriptor> pModel) {
        try {
            TypeSpec.Builder bldr = TypeSpec
                    .classBuilder(pClsDsc.name())
                    .addModifiers(Modifier.PUBLIC);
            if (StringUtils.hasLength(ctx.dtoParentInterface())) {
                String parentClass = ctx.dtoParentInterface().substring(ctx.dtoParentInterface().lastIndexOf('.') + 1);
                String parentPackage = ctx.dtoParentInterface().substring(0, ctx.dtoParentInterface().lastIndexOf('.'));
                bldr.addSuperinterface((ClassName.get(parentPackage, parentClass)));
            }
            String docu = pClsDsc.description();
            if ((docu != null) && (!docu.isEmpty())) {
                bldr.addJavadoc("DTO: $L", docu);
            }
            bldr.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build());

            for (ClassProperty p : pClsDsc.fields() ) {
                String fieldSuffix = StringUtils.capitalize(p.name());
                TypeName fldType = getClassNameFor(p);
                String fldDoc = p.description();
                if (fldDoc == null) {
                    fldDoc = fieldSuffix;
                }
                // import com.fasterxml.jackson.annotation.JsonProperty;
                // @JsonProperty("<name>")
                AnnotationSpec annotS =   AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty"))
                        .addMember("value", "$S", p.name())
                        .build();

                bldr.addField(fldType, p.name(), Modifier.PRIVATE);

                bldr.addMethod(MethodSpec
                        .methodBuilder("get".concat(fieldSuffix))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(fldType)
                        .addAnnotation(annotS)
                        .addJavadoc("$L", fldDoc)
                        .addStatement("return this.".concat(p.name()))
                        .build());
                bldr.addMethod(MethodSpec
                        .methodBuilder("set".concat(fieldSuffix))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(fldType, "p".concat(fieldSuffix))
                        .addJavadoc("$L", fldDoc)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(p.name().concat(" = p".concat(fieldSuffix)))
                        .build());
            }
            bldr.addMethod(generateToString(pClsDsc));
            JavaFile javaFile = JavaFile.builder(pClsDsc.packageName(), bldr.build())
                    .addFileComment(GENERATION_NOTE, "")
                    .build();
            javaFile.writeTo(new File(ctx.outputDir()));
            log.debug("{}/{}/{}.java created", ctx.outputDir(), StringUtils.replace(pClsDsc.packageName(), ".", "/"), pClsDsc.name());
        } catch (Exception pEx) {
            log.error("Error generating code dto-class for {}", pClsDsc.id());
        }
    }

    private void generateMessage(ClassDescriptor pClsDsc, List<ClassDescriptor> pModel) {
        try {
            String msgVersion = StringUtils.hasLength(ctx.version()) ? ctx.version().strip() : "0.0.0";
            TypeSpec.Builder bldr = TypeSpec
                    .classBuilder(pClsDsc.name())
                    .addModifiers(Modifier.PUBLIC);
            Optional<ClassProperty> payloadFld = pClsDsc.fields().stream().filter(p->"payload".equalsIgnoreCase(p.name())).findFirst();
            if(StringUtils.hasLength(ctx.msgPackage())) {
                String parentClass = ctx.msgParentClass().substring(ctx.msgParentClass().lastIndexOf('.') + 1);
                String parentPackage = ctx.msgParentClass().substring(0, ctx.msgParentClass().lastIndexOf('.'));
                if (payloadFld.isPresent()) {
                    bldr.superclass(ParameterizedTypeName.get(ClassName.get(parentPackage,parentClass),  getClassNameFor(payloadFld.get())));
                } else {
                    bldr.superclass(ClassName.get(parentPackage, parentClass));
                }
            }
            String docu = pClsDsc.description();
            if ((docu != null) && (!docu.isEmpty())) {
                bldr.addJavadoc("Message: $L", docu);
            }

            Builder mbldr = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super($L, \"$L\")", msgTypes.get(pClsDsc.name()), msgVersion);
            bldr.addMethod(mbldr.build()).build();

            boolean hasPayload = false;
            ClassProperty lastField = null;
            for (ClassProperty p : pClsDsc.fields() ) {
                lastField = p;
                String fieldSuffix = StringUtils.capitalize(p.name());
                TypeName fldType = getClassNameFor(p);
                String fldDoc = p.description();
                if (fldDoc == null) {
                    fldDoc = fieldSuffix;
                }
                bldr.addField(fldType, p.name(), Modifier.PRIVATE);
                if (p.name().equalsIgnoreCase("payload")) {
                    hasPayload = true;
                    // import com.fasterxml.jackson.annotation.JsonProperty;
                    // @JsonProperty("<name>")
                    AnnotationSpec annotS =   AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty"))
                            .addMember("value", "$S", p.name()).build();
                    bldr.addMethod(MethodSpec
                            .methodBuilder("payload")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addAnnotation(annotS)
                            .returns(fldType)
                            .addStatement("return this.".concat(p.name()))
                            .build());
                    bldr.addMethod(MethodSpec
                            .methodBuilder("payload")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addParameter(fldType, "pPayload")
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(p.name().concat(" = (").concat(simpleName(fldType)).concat(") pPayload"))
                            .build());
                } else  {
                    AnnotationSpec annotS =   AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty"))
                            .addMember("value", "$S", p.name()).build();

                    bldr.addMethod(MethodSpec
                            .methodBuilder("get".concat(fieldSuffix))
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(annotS)
                            .returns(fldType)
                            .addJavadoc("$L", fldDoc)
                            .addStatement("return this.".concat(p.name()))
                            .build());
                    bldr.addMethod(MethodSpec
                            .methodBuilder("set".concat(fieldSuffix))
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(fldType, "p".concat(fieldSuffix))
                            .addJavadoc("$L", fldDoc)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(p.name().concat(" = p".concat(fieldSuffix)))
                            .build());
                }
            }
            if (!hasPayload) {
                hasPayload = true;
                AnnotationSpec annotS =   AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty"))
                        .addMember("value", "$S", "payload").build();
                bldr.addMethod(MethodSpec
                        .methodBuilder("payload")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addAnnotation(annotS)
                        .returns(getClassNameFor(lastField))
                        .addStatement("return this.".concat(lastField.name()))
                        .build());
                bldr.addMethod(MethodSpec
                        .methodBuilder("payload")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(getClassNameFor(lastField), "pPayload")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(lastField.name().concat(" = pPayload"))
                        .build());
            }
            bldr.addMethod(generateToString(pClsDsc));
            JavaFile javaFile = JavaFile.builder(pClsDsc.packageName(), bldr.build())
                    .addFileComment(GENERATION_NOTE, "")
                    .build();
            javaFile.writeTo(new File(ctx.outputDir()));
            log.debug("{}/{}/{}.java created", ctx.outputDir(), StringUtils.replace(pClsDsc.packageName(), ".", "/"), pClsDsc.name());
        } catch (Exception pEx) {
            log.error("Error generating code msg-class for {}", pClsDsc.id());
        }
    }

    private void  generateClass(ClassDescriptor pClsDsc, List<ClassDescriptor> pModel) {
        try {

            TypeSpec.Builder bldr = TypeSpec
                    .classBuilder(pClsDsc.name())
                    .addModifiers(Modifier.PUBLIC);
            String docu = pClsDsc.description();
            if ((docu != null) && (!docu.isEmpty())) {
                bldr.addJavadoc("Class: $L", docu);
            }
            bldr.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build());

            for (ClassProperty p : pClsDsc.fields() ) {
                String fieldSuffix = StringUtils.capitalize(p.name());
                TypeName fldType = getClassNameFor(p);
                String fldDoc = p.description();
                if (fldDoc == null) {
                    fldDoc = fieldSuffix;
                }

                bldr.addField(fldType, p.name(), Modifier.PRIVATE)
                .addJavadoc("$L", fldDoc);
                bldr.addMethod(MethodSpec
                        .methodBuilder("get".concat(fieldSuffix))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(fldType)
                        .addJavadoc("$L", fldDoc)
                        .addStatement("return this.".concat(p.name()))
                        .build());
                bldr.addMethod(MethodSpec
                        .methodBuilder("set".concat(fieldSuffix))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(fldType, "p".concat(fieldSuffix))
                        .addJavadoc("$L", fldDoc)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(p.name().concat(" = p".concat(fieldSuffix)))
                        .build());
            }
            bldr.addMethod(generateToString(pClsDsc));
            JavaFile javaFile = JavaFile.builder(pClsDsc.packageName(), bldr.build())
                    .addFileComment(GENERATION_NOTE, "")
                    .build();
            javaFile.writeTo(new File(ctx.outputDir()));
            log.debug("{}/{}/{}.java created", ctx.outputDir(), StringUtils.replace(pClsDsc.packageName(), ".", "/"), pClsDsc.name());
        } catch (Exception pEx) {
            log.error("Error generating code schema-class for {}", pClsDsc.id());
        }
    }

    private void generateEnum(ClassDescriptor pClsDsc, List<ClassDescriptor> pModel) {
        try {
            String interfaceSuperClass = null;
            String interfaceSuperPackage = null;
            boolean hasUNDEFINED = false;

            TypeSpec.Builder bldr = TypeSpec
                    .enumBuilder(pClsDsc.name())
                    .addModifiers(Modifier.PUBLIC);
            if (StringUtils.hasLength(ctx.enumParentInterface())) {
                interfaceSuperClass = ctx.enumParentInterface().substring(ctx.enumParentInterface().lastIndexOf('.') + 1);
                interfaceSuperPackage = ctx.enumParentInterface().substring(0, ctx.enumParentInterface().lastIndexOf('.'));
                bldr.addSuperinterface((ClassName.get(interfaceSuperPackage, interfaceSuperClass)));
            }
            String docu = pClsDsc.description();
            if ((docu != null) && (!docu.isEmpty())) {
                bldr.addJavadoc("Enum: $L", docu);
            }

            for (ClassProperty p : pClsDsc.fields() ) {
                bldr.addEnumConstant(p.id());
                hasUNDEFINED = (!hasUNDEFINED) && ("UNDEFINED".equalsIgnoreCase(p.id()));
            }

            if (!hasUNDEFINED) {
                bldr.addEnumConstant("UNDEFINED");
            }
            bldr.build();

            // Generate convert-Method
            if ((interfaceSuperClass != null) && (interfaceSuperPackage != null)) {
                bldr.addMethod(MethodSpec
                        .methodBuilder("convert")
                        .addModifiers(Modifier.STATIC)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(interfaceSuperPackage, interfaceSuperClass), "pFrom")
                        .returns(ClassName.get(pClsDsc.packageName(), pClsDsc.name()))
                        .addStatement(pClsDsc.name().concat(" retS = ").concat(pClsDsc.name()).concat(".UNDEFINED"))
                        .beginControlFlow("try")
                        .addStatement("retS = valueOf(pFrom.name())")
                        .nextControlFlow("catch($T pEx)", Exception.class)
                        .endControlFlow()
                        .addStatement("return retS")
                        .build());
            }
            JavaFile javaFile = JavaFile.builder(pClsDsc.packageName(), bldr.build())
                    .addFileComment(GENERATION_NOTE, "")
                    .build();
            javaFile.writeTo(new File(ctx.outputDir()));
            log.debug("{}/{}/{}.java created", ctx.outputDir(), StringUtils.replace(pClsDsc.packageName(), ".", "/"), pClsDsc.name());
        } catch (Exception pEx) {
            log.error("Error generating code for {}", pClsDsc.id());
        }
    }

    private MethodSpec generateToString(ClassDescriptor pClsDsc) {
        MethodSpec.Builder bldr = MethodSpec
                .methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class);

        bldr.addStatement("StringBuilder sb = new StringBuilder(".concat("\"").concat(pClsDsc.name().concat(":\\n\")")));

        for (ClassProperty fld: pClsDsc.fields()) {
            String getMethod = null;
            if (!"payload".equalsIgnoreCase(fld.name())) {
                getMethod = "get".concat(StringUtils.capitalize(fld.name()));
            } else {
                getMethod = "payload";
            }
            bldr.addStatement("sb.append(".concat("\"").concat(fld.name()).concat("=\").append(")
                    .concat("String.valueOf(").concat(getMethod).concat("())).append('\\n')"));
        }
        bldr.addStatement("return sb.toString()");
        return bldr.build();
    }

    /**
     * Determine the class name of the field
     * @param pProp ClassProperty field descriptor
     * @return ClassName of the field passed
     */
    private TypeName getClassNameFor(ClassProperty pProp) {
        TypeName fldType = null;
        if (pProp.propertyType() == PropType.ARRAY) {
            if (pProp.refType() != null) {
                ClassDescriptor cld = index.get(pProp.refType());
                ClassName clsName = ClassName.get(cld.packageName(),  cld.name());
                fldType = ParameterizedTypeName.get(ClassName.get(List.class), clsName);
            } else {
                fldType = ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get( pProp.fieldType().typeClass()));
            }

        } else if (pProp.refType() != null) {
            ClassDescriptor cld = index.get(pProp.refType());
            fldType = ClassName.get(cld.packageName(),  cld.name());
        } else {
            fldType = ClassName.get(pProp.fieldType().typeClass());
        }
        return fldType;
    }

    private String simpleName(TypeName pTName) {
        return pTName.toString().substring(pTName.toString().lastIndexOf('.') + 1);
    }

    private String generateUpperCaseName(String pName) {
        String retStr = pName;
        if ((pName != null) && (!pName.isEmpty())) {
            char c = pName.charAt(0);
            boolean wasLower = Character.isLowerCase(c);
            StringBuilder sb = new StringBuilder();
            sb.append(Character.toUpperCase(pName.charAt(0)));
            for (int ix = 1; ix < pName.length(); ix++) {
                c = pName.charAt(ix);
                if (Character.isUpperCase(c)) {
                    if (wasLower) {
                        sb.append('_');
                    }
                    sb.append(Character.toUpperCase(c));
                    wasLower = false;
                } else {
                    wasLower = true;
                    sb.append(Character.toUpperCase(c));

                }
            }
            retStr = sb.toString();
        }
        return retStr;
    }
}
