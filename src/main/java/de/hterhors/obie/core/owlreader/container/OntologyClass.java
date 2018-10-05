package de.hterhors.obie.core.owlreader.container;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.hterhors.obie.core.owlreader.EDatatypeRestriction;
import de.hterhors.obie.core.tools.JavaClassNamingTools;

public class OntologyClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name space of this class from the ontology.
	 */
	final public String namespace;
	/**
	 * The ontology class name from the ontology without the name space.
	 */
	final public String ontologyClassName;

	/**
	 * The fully qualified name of the ontolgy class. This includes namespace and
	 * class name.
	 */
	final public String fullyQualifiedOntolgyName;

	/**
	 * The java class name of this class.
	 */
	final public String javaClassName;

	/**
	 * The java interface name of this class.
	 */
	final public String javaInterfaceName;
	/**
	 * The java field name, if this class is a field in a different java class.
	 */
	final public String javaClassFieldName;

	/**
	 * The plural form of the field name if this class is used as collection.
	 */
	final public String javaClassFieldPluralName;

	/**
	 * Whether this class is the range of a datatype property. Usually these classes
	 * are artificial classes as datatype properties tend to have no range class.
	 */
	public boolean isDataType = false;

	/**
	 * If this class is a datatype property-class, this variable corresponds to the
	 * restriction specified in owl. The value of this datatype class will be
	 * restricted to the provided restriction e.g. string, int or float.
	 *
	 **/
	public EDatatypeRestriction restriction = EDatatypeRestriction.STRING;

	/**
	 * Whether this class is a named individual in the ontolgy.
	 */
	public boolean isNamedIndividual = false;

	/**
	 * Whether this class is an artificial class used for unionOf range classes.
	 */
	public boolean isArtificialUnionOfRangeClass = false;

	/**
	 * The documentations from the ontology.
	 */
	public Map<String, List<String>> documentation = new HashMap<>();

	/**
	 * The map of properties of this class.
	 */
	public Map<OntologySlotData, Set<OntologyClass>> domainRangeRelations = new HashMap<>();

	/**
	 * The set of sub classes.
	 */
	public Set<OntologyClass> subclasses = new HashSet<>();

	/**
	 * The set of super classes.
	 */
	public Set<OntologyClass> superclasses = new HashSet<>();

	/**
	 * Standard constructor.
	 * 
	 * @param namespace    the namespace of this class.
	 * @param ontologyName the ontology class name.
	 */
	public OntologyClass(String namespace, String ontologyName) {
		this.namespace = namespace;
		this.ontologyClassName = ontologyName;
		this.fullyQualifiedOntolgyName = namespace + ontologyName;
		this.javaClassName = JavaClassNamingTools.normalizeClassName(ontologyName);
		this.javaInterfaceName = JavaClassNamingTools.toInterfaceName(this.javaClassName);
		this.javaClassFieldName = JavaClassNamingTools.getVariableName(javaClassName);
		this.javaClassFieldPluralName = JavaClassNamingTools.toPluralForm(javaClassFieldName);
	}

	/**
	 * Copy constructor. This IS NOT a deep clone!
	 * 
	 * @param dataClassToBeCopied
	 */
	public OntologyClass(OntologyClass dataClassToBeCopied) {
		this.namespace = dataClassToBeCopied.namespace;
		this.javaClassName = dataClassToBeCopied.javaClassName;
		this.javaInterfaceName = dataClassToBeCopied.javaInterfaceName;
		this.ontologyClassName = dataClassToBeCopied.ontologyClassName;
		this.fullyQualifiedOntolgyName = dataClassToBeCopied.fullyQualifiedOntolgyName;
		this.javaClassFieldName = dataClassToBeCopied.javaClassFieldName;
		this.javaClassFieldPluralName = dataClassToBeCopied.javaClassFieldPluralName;

		this.isDataType = dataClassToBeCopied.isDataType;
		this.isNamedIndividual = dataClassToBeCopied.isNamedIndividual;
		this.isArtificialUnionOfRangeClass = dataClassToBeCopied.isArtificialUnionOfRangeClass;
		this.documentation = dataClassToBeCopied.documentation;
		this.domainRangeRelations = dataClassToBeCopied.domainRangeRelations;
		this.subclasses = dataClassToBeCopied.subclasses;
		this.superclasses = dataClassToBeCopied.superclasses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullyQualifiedOntolgyName == null) ? 0 : fullyQualifiedOntolgyName.hashCode());
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
		OntologyClass other = (OntologyClass) obj;
		if (fullyQualifiedOntolgyName == null) {
			if (other.fullyQualifiedOntolgyName != null)
				return false;
		} else if (!fullyQualifiedOntolgyName.equals(other.fullyQualifiedOntolgyName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OntologyClassData [namespace=" + namespace + ", ontologyClassName=" + ontologyClassName
				+ ", javaClassName=" + javaClassName + ", javaInterfaceName=" + javaInterfaceName
				+ ", javaClassFieldName=" + javaClassFieldName + ", javaClassFieldPluralName="
				+ javaClassFieldPluralName + ", isDataType=" + isDataType + ", isNamedIndividual=" + isNamedIndividual
				+ ", isArtificialUnionOfRangeClass=" + isArtificialUnionOfRangeClass + ", documentation="
				+ documentation + ", domainRangeRelations="
				+ domainRangeRelations.entrySet().stream()
						.map(d -> d.getKey() + "->"
								+ d.getValue().stream().map(r -> r.ontologyClassName).collect(Collectors.toList())
								+ "\n")
						.collect(Collectors.toList())
				+ ", subclasses=" + subclasses.stream().map(d -> d.ontologyClassName).collect(Collectors.toList())
				+ "]";

	}

}
