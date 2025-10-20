package ch.kochse.tools.asyncapi.codegen.model;

public class ClassProperty extends DescriptionBase{
    private String alias;
    private StdType fieldType;
    private String  refType;
    private String	format;
    private Qualifier qual;
    private String	description;
    private PropType propertyType;
    private String	value;
    private int		minOcc;
    private int		maxOcc;

    /**
     * Describes individual generic properties of a class
     */
    private ClassProperty() {
        super();
    }

    public ClassProperty id(String pId) {
        super.setId(pId);
        minOcc = maxOcc = 1;
        return this;
    }

    public ClassProperty(String pId, String pName) {
        super(pId, pName);
        minOcc = maxOcc = 1;
    }

    public ClassProperty(String pId, String pName, PropType pPropType) {
        this(pId, pName);
        propertyType = pPropType;
    }

    public ClassProperty(String pId, String pName, String pAlias) {
        this(pId, pName);
        alias = pAlias;
    }


    public String id() {
        return getId();
    }


    public String name() {
        return getName();
    }

    public ClassProperty name(String pName) {
        setName(pName);
        return this;
    }

    public String alias() {
        return alias;
    }

    public ClassProperty alias(String pAlias) {
        alias = pAlias;
        return this;
    }

    public StdType fieldType() {
        return fieldType;
    }

    public ClassProperty fieldType(StdType pFieldType) {
        fieldType = pFieldType;
        return this;
    }

    public String refType() {
        return refType;
    }

    public ClassProperty refType(String pRefType) {
        refType = pRefType;
        return this;
    }

    public String format() {
        return format;
    }

    public ClassProperty format(String pFormat) {
        format = pFormat;
        return this;
    }

    public Qualifier qual() {
        return qual;
    }

    public ClassProperty qual(Qualifier pQual) {
        qual = pQual;
        return this;
    }

    public String description() {
        return description;
    }

    public ClassProperty description(String pDescription) {
        description = pDescription;
        return this;
    }

    public PropType propertyType() {
        return propertyType;
    }

    public ClassProperty propertyType(PropType pPropertyType) {
        propertyType = pPropertyType;
        return this;
    }

    public String value() {
        return value;
    }

    public ClassProperty value(String pValue) {
        value = pValue;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("prop[").append(super.toString());
        if (alias != null) sb.append(", alias=").append(alias);
        if (description != null) sb.append(", descriptor=").append(description);
        if (fieldType != null) sb.append(", fieldType=").append(fieldType.name());
        if (format != null) sb.append(", format=").append(format);
        if (propertyType != null) sb.append(", propertyType=").append(propertyType.name());
        if (refType != null) sb.append(", refType=").append(refType);
        if (qual != null) sb.append(", qual=" ).append(qual.name());
        if (value != null) sb.append( ", value=").append(value);
        sb.append(", minOcc=").append(minOcc);
        if (maxOcc > minOcc) sb.append(", maxOcc=").append(maxOcc).append(maxOcc);
        sb.append(']');
        return sb.toString();
    }


}
