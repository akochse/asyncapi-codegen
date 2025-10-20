package ch.kochse.tools.asyncapi.codegen.model;

import java.util.List;

public class ClassDescriptor extends DescriptionBase {
	private String alias;
	private String parentClass;
	private String packageName;
	private String description;
	private Qualifier qualifier;
	private DescriptorType descriptorType;
	private List<ClassProperty> fields;
	private List<ClassProperty> methods;
	private List<ClassProperty> meta;
	
	
	protected ClassDescriptor() {
		super();
	}
	
	public ClassDescriptor(String pId, String pName) {
		super(pId, pName);
	}

	public String id() {
		return getId();
	}

	public void id(String pId) {
		super.setId(pId);
	}

	public String name() {
		return getName();
	}

	public void name(String pName) {
		setName(pName);
	}
	public String alias() {
		return alias;
	}

	public void alias(String pAlias) {
		alias = pAlias;
	}
	

	public DescriptorType descriptorType() {
		return descriptorType;
	}

	public void descriptorType(DescriptorType pDescriptorType) {
		descriptorType = pDescriptorType;
	}

	public String parentClass() {
		return parentClass;
	}

	public void parentClass(String pParentClass) {
		parentClass = pParentClass;
	}

	public String packageName() {
		return packageName;
	}

	public void packageName(String pPackageName) {
		packageName = pPackageName;
	}

	public Qualifier aualifier() {
		return qualifier;
	}

	public void qualifier(Qualifier pQualifier) {
		qualifier = pQualifier;
	}

	public String description() {
		return description;
	}

	public void description(String pDescription) {
		description = pDescription;
	}

	public List<ClassProperty> fields() {
		return fields;
	}

	public void fields(List<ClassProperty> pFields) {
		fields = pFields;
	}

	public List<ClassProperty> methods() {
		return methods;
	}

	public void methods(List<ClassProperty> pMethods) {
		methods = pMethods;
	}

	public List<ClassProperty> meta() {
		return meta;
	}

	public void meta(List<ClassProperty> pMeta) {
		meta = pMeta;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ClassDescriptor [").append(super.toString());
		if (alias != null) sb.append(", alias=").append(alias);
		if (description != null) sb.append( ", description=").append(description);
		if (parentClass != null) sb.append(", parentClass=").append(parentClass);
		if (packageName != null) sb.append( ", packageName=").append(packageName);
		if (qualifier != null) sb.append(", qualifier=").append(qualifier.name());
		if (descriptorType != null) sb.append(", descriptorType=").append(descriptorType.name());
		sb.append("]\n");
		if ((meta != null) && (meta.size() > 0)) {
			sb.append("Meta-Fields {\n");
			for (ClassProperty p : meta) {
				sb.append("  ").append(p.toString()).append('\n');
			}
			sb.append("}\n");
		}
		if ((fields != null) && (fields.size() > 0)) {
			sb.append("Fields {\n");
			for (ClassProperty p : fields) {
				sb.append("  ").append(p.toString()).append('\n');
			}
			sb.append("}\n");
		}
		if ((methods != null) && (methods.size() > 0)) {
			sb.append("Methods\n");
			for (ClassProperty p : methods) {
				sb.append("  ").append(p.toString()).append('\n');
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
	
}
