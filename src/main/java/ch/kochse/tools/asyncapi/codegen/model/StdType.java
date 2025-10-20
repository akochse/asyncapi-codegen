package ch.kochse.tools.asyncapi.codegen.model;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public enum StdType implements Type {
    INTEGER("integer","int32", Integer.class),
    LONG("long",  "int64",Long.class),
    FLOAT("float",   "float", Float.class),
    DOUBLE("double", "double", Double.class),
    NUMBER("number", "double", Double.class),
    STRING("string",  "", String.class),
    BYTE("byte",     "byte", Byte.class),
    BINARY("binary",  "binary", Byte.class),
    BOOLEAN("boolean", "", Boolean.class),
    DATE("date",     "date", LocalDate.class),
    DATETIME("datetime", "date-time", LocalDateTime.class),
    PASSWORD("password", "password", String.class),
    ARRAY("array", "", ArrayList.class),
    OBJECT("object", "", Object.class),
    UNDEFIED("", "", Object.class),
    ;

    private String asyncapiName;
    private String typeFormat;
    private Class<?> clazz;

    StdType(String pName, String pFormat, Class<?> pClazz) {
        asyncapiName = pName;
        typeFormat = pFormat;
        clazz = pClazz;
    }

    public Class<?> typeClass() {
        return clazz;
    }

    public String asyncapiNameName() {
        return asyncapiName;
    }

    public String typeFormat() {
        return typeFormat;
    }

    public static StdType from(String pTypeName) {
        StdType retT = UNDEFIED;
        for (StdType t: values()) {
            if(t.asyncapiName.equals(pTypeName)) {
                retT = t;
                break;
            }
        }
        return retT;
    }


    public static StdType byFormat(String pTypeFormat) {
        StdType retT = UNDEFIED;
        for (StdType t: values()) {
            if(t.typeFormat.equals(pTypeFormat)) {
                retT = t;
                break;
            }
        }
        return retT;
    }

    @Override
    public String getTypeName() {
        return clazz.getSimpleName();
    }

    @Override
    public String toString() {
        return getTypeName();
    }


}
