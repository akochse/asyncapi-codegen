package ch.kochse.tools.asyncapi.codegen.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneratorContext {
    private final static String PACKAGE_PREFIX_DEFAULT = "ch.kochse.app.test";

    private String     inputFilename;
    @Value("${CodeGen.Default.OutputDir:}")
    private String     outputDir;
    @Value("${CodeGen.BasePackage:}")
    private String     basePackage;
    @Value("${CodeGen.Contract.Package:contract}")
    private String     contractSubPackage;
    @Value("${CodeGen.DTO.Package:}")
    private String     dtoSubPackage;
    @Value("${CodeGen.Message.Package:}")
    private String     msgSubPackage;
    @Value("${CodeGen.Enum.Package:}")
    private String     enumSubPackage;
    @Value("${CodeGen.DateType:java.time.LocalDateTime}")
    private String     dateTimeClass;
    @Value("${CodeGen.Message.ParentClass:}")
    private String     msgParentClass;
    @Value("${CodeGen.Enum.ParentInterface:}")
    private String     enumParentInterface;
    @Value("${CodeGen.DTO.ParentInterface:}")
    private String     dtoParentInterface;
    @Value("${CodeGen.MessageType.ParentInterface:}")
    private String     msgTypeParentInterface;
    private String     domainName;
    private String     contractPackage;
    private String     dtoPackage;
    private String     msgPackage;
    private String     enumPackage;
    private String     version;
    private String     target;

    private GeneratorContext() {
    }

    public void parseArguments(String[] pArgs) {
        for (int i = 0; i < pArgs.length; i++) {
            String arg = pArgs[i];
            if ("--input".equals(arg) || "-i".equals(arg)) {
                inputFilename = pArgs[++i];
            } else if ("--output".equals(arg) || "-o".equals(arg)) {
                outputDir = pArgs[++i];
            } else if ("--package".equals(arg) || "-p".equals(arg)) {
                basePackage = pArgs[++i];
            } else if ("--dto-package".equals(arg) || "-d".equals(arg)) {
                dtoPackage = pArgs[++i];
            } else if ("--msg-package".equals(arg) || "-m".equals(arg)) {
                msgPackage = pArgs[++i];
            } else if ("--contract-package".equals(arg) || "-c".equals(arg)) {
                contractPackage = pArgs[++i];
            }  else if ("--domain".equals(arg)) {
                domainName = pArgs[++i];
            }  else if ("--target".equals(arg)) {
                target = pArgs[++i];
            }  else if ("--version".equals(arg)) {
                version = pArgs[++i];
            }
        }
        assemblePackages();
    }

    private void assemblePackages() {
        if (StringUtils.isEmpty(basePackage)) {
            basePackage = PACKAGE_PREFIX_DEFAULT;
        }
        if (!StringUtils.isEmpty(contractSubPackage)) {
            contractPackage = basePackage.concat(".").concat(contractSubPackage);
        } else {
            contractPackage = basePackage.concat(".contract");
        }
        if (!StringUtils.isEmpty(dtoSubPackage)) {
            dtoPackage = basePackage.concat(".").concat(dtoSubPackage);
        } else {
            dtoPackage = contractPackage;
        }
        if (!StringUtils.isEmpty(msgSubPackage)) {
            msgPackage = basePackage.concat(".").concat(msgSubPackage);
        } else {
            msgPackage = contractPackage;
        }
        if (!StringUtils.isEmpty(enumSubPackage)) {
            enumPackage = basePackage.concat(".").concat(enumSubPackage);
        } else {
            enumPackage = contractPackage;
        }

        if (StringUtils.isEmpty(domainName)) {
            domainName = createDomainName();
        }
    }

    /**
     * generates the domain name from the package
     * @return
     */
    private String createDomainName() {
        String domainN = "unkown";
        if(!StringUtils.isEmpty(basePackage)) {
            String parts[] = basePackage.split("\\.");
            if (parts.length > 2) {
                domainN = parts[parts.length - 1];
            }
        }
        return domainN;
    }


    public static String defaultPackagePrefix() {
        return PACKAGE_PREFIX_DEFAULT;
    }


    public String inputFilename() {
        return inputFilename;
    }


    public String outputDir() {
        return outputDir;
    }


    public String basePackage() {
        return basePackage;
    }


    public String contractSubPackage() {
        return contractSubPackage;
    }


    public String dtoSubPackage() {
        return dtoSubPackage;
    }


    public String msgSubPackage() {
        return msgSubPackage;
    }


    public String enumSubPackage() {
        return enumSubPackage;
    }


    public String dateTimeClass() {
        return dateTimeClass;
    }


    public String msgParentClass() {
        return msgParentClass;
    }


    public String enumParentInterface() {
        return enumParentInterface;
    }


    public String dtoParentInterface() {
        return dtoParentInterface;
    }


    public String msgTypeParentInterface() {
        return msgTypeParentInterface;
    }


    public String domainName() {
        return domainName;
    }


    public String contractPackage() {
        return contractPackage;
    }


    public String dtoPackage() {
        return dtoPackage;
    }


    public String msgPackage() {
        return msgPackage;
    }


    public String enumPackage() {
        return enumPackage;
    }


    public String target() {
        return target;
    }


    public String version() {
        return version;
    }

    private String norm(String pTxt) {
        if (StringUtils.isEmpty(pTxt)) {
            return ("--");
        }
        return (pTxt);
    }

    @Override
    public String toString() {
        return new StringBuilder("Generator Context: \n")
                .append(" inputFilename=").append(norm(inputFilename)).append('\n')
                .append(" outputDir=").append(norm(outputDir)).append('\n')
                .append(" target=").append(norm(target)).append('\n')
                .append(" domainName=").append(norm(domainName)).append('\n')
                .append(" version=").append(norm(version)).append('\n')
                .append(" basePackage=").append(norm(basePackage)).append('\n')
                .append(" contractSubPackage=").append(norm(contractSubPackage)).append('\n')
                .append(" dtoSubPackage=").append(norm(dtoSubPackage)).append('\n')
                .append(" msgSubPackage=").append(norm(msgSubPackage)).append('\n')
                .append(" enumSubPackage=").append(norm(enumSubPackage)).append('\n')
                .append(" dateTimeClass=").append(norm(dateTimeClass)).append('\n')
                .append(" msgParentClass=").append(norm(msgParentClass)).append('\n')
                .append(" enumParentInterface=").append(norm(enumParentInterface)).append('\n')
                .append(" dtoParentInterface=").append(norm(dtoParentInterface)).append('\n')
                .append(" msgTypeParentInterface=").append(norm(msgTypeParentInterface)).append('\n')
                .append(" contractPackage=").append(norm(contractPackage)).append('\n')
                .append(" dtoPackage=").append(norm(dtoPackage)).append('\n')
                .append(" msgPackage=").append(norm(msgPackage)).append('\n')
                .append(" enumPackage=").append(norm(enumPackage)).append('\n')
                .toString();
    }
}
