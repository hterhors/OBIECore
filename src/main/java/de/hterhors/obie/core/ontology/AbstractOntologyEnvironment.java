package de.hterhors.obie.core.ontology;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import de.hterhors.obie.core.owlreader.IClassFilter;

public abstract class AbstractOntologyEnvironment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String PACKAGE_PREFIX = "package ";

	public final String OBIE_CLASSES_PACKAGE_NAME = getBasePackage() + "classes.";
	public final String OBIE_INTERFACES_PACKAGE_NAME = getBasePackage() + "interfaces.";

	/**
	 * Returns a list of fully qualified class names from the ontology that are not
	 * part of the actual ontology model but rather serves as collective class.
	 * 
	 * The set of classes will be partially ignored during the creation of the
	 * ontology.
	 * 
	 * @return a set of fully qualified ontology class names
	 */
	public abstract Set<String> getCollectiveClasses();

	/**
	 * The parend package structure of the ontology java class data.
	 * 
	 * @return
	 */
	public abstract String getBasePackage();

	public abstract String getDataNameSpace();

	public String getOntologyThingClassSimpleName() {
		return "I" + getOntologyName() + "Thing";
	}

	public abstract String getOntologyName();

	public abstract File getOntologyFile();

	/**
	 * A owl class filter that can be used to filter out unwanted classes from the
	 * ontology. E.g. all classes that have a specific property or belongs to a sub
	 * ontology...
	 * 
	 * @return
	 */
	public abstract IClassFilter getOwlClassFilter();

	public abstract String getOntologySourceLocation();

	public abstract int getOntologyVersion();

	public abstract List<String> getAdditionalPropertyNames();

	public abstract String getAdditionalPrefixes();
}