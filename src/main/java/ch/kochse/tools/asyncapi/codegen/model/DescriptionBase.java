package ch.kochse.tools.asyncapi.codegen.model;

import java.util.Objects;

public abstract class DescriptionBase implements Comparable<DescriptionBase> {
	private String id;
	private String name;

	protected DescriptionBase() {
		
	}
	
	public DescriptionBase(String pId, String pName) {
		id = pId; 
		name = pName;
	}

	
	@Override
	public int compareTo(DescriptionBase pDesc) {
		return (id.compareTo(pDesc.getId()));
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DescriptionBase other = (DescriptionBase) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name);
	}


	protected String getId() {
		return id;
	}


	protected void setId(String pId) {
		id = pId;
	}


	protected void setName(String pName) {
		name = pName;
	}

	protected String getName() {
		return name;
	}

	@Override
	public String toString() {
		return new StringBuilder("id=").append(id).append(", name=").append(name != null ? name :"").toString();
	}
	
}
