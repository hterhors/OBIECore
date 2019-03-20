package de.hterhors.obie.core.ontology;

import java.io.Serializable;

public class AbstractIndividual implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final public String name;
	final public String nameSpace;

	public AbstractIndividual(final String nameSpace, final String name) {
		this.nameSpace = nameSpace;
		this.name = name;
	}

	public String getURI() {
		return nameSpace + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractIndividual other = (AbstractIndividual) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameSpace == null) {
			if (other.nameSpace != null)
				return false;
		} else if (!nameSpace.equals(other.nameSpace))
			return false;
		return true;
	}

}
