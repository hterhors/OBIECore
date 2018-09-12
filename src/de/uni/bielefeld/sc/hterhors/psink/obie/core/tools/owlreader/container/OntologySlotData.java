package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.ECardinalityType;

public class OntologySlotData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The java class field
	 */
	public final String javaClassPropertyName;
	/**
	 * The ontology name of this property without the namespace.
	 */
	public final String ontologyPropertyName;

	/**
	 * The name space of this property.
	 */
	public final String namespace;

	/**
	 * The cardinality of this property.
	 */
	public final ECardinalityType cardinalityType;

	/**
	 * If this cardinality was set by default. This means neither by min nor exact
	 * cardinality.
	 */
	public final boolean cardinalitySetByDefault;

	/**
	 * The documentation of this property.
	 */
	public final Map<String, List<String>> documentation = new HashMap<>();

	public OntologySlotData(String IRI, String ontologyName, ECardinalityType cardinalityType,
			final boolean setByDefault) {
		this.ontologyPropertyName = ontologyName;
		this.namespace = IRI;
		this.javaClassPropertyName = JavaClassNamingTools.normalizeClassName(JavaClassNamingTools.getVariableName(ontologyName));
		this.cardinalityType = cardinalityType;
		this.cardinalitySetByDefault = setByDefault;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardinalityType == null) ? 0 : cardinalityType.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((ontologyPropertyName == null) ? 0 : ontologyPropertyName.hashCode());
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
		OntologySlotData other = (OntologySlotData) obj;
		if (cardinalityType != other.cardinalityType)
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (ontologyPropertyName == null) {
			if (other.ontologyPropertyName != null)
				return false;
		} else if (!ontologyPropertyName.equals(other.ontologyPropertyName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataRelation [javaClassPropertyName=" + javaClassPropertyName + ", ontologyPropertyName="
				+ ontologyPropertyName + ", namespace=" + namespace + ", cardinalityType=" + cardinalityType
				+ ", documentation=" + documentation + "]";
	}

}
