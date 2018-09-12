package de.uni.bielefeld.sc.hterhors.psink.obie.core.projects;

import java.io.File;
import java.io.Serializable;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public abstract class AbstractOBIEProjectEnvironment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected AbstractOBIEProjectEnvironment() {
	}

	public final String PACKAGE_PREFIX = "package ";

	public final String OBIE_ANNOTATIONS_PACKAGE_NAME = "de.uni.bielefeld.sc.hterhors.psink.obie.annotations.*";

	public final String OBIE_CLASSES_PACKAGE_NAME = getOntologyBasePackage() + "classes.";
	public final String OBIE_INTERFACES_PACKAGE_NAME = getOntologyBasePackage() + "interfaces.";

	public abstract int getOntologyVersion();

	public abstract String getOntologyBasePackage();

	public abstract Class<? extends IOBIEThing> getOntologyThingInterface();

	public abstract File getRawCorpusFile();

	public abstract File getBigramCorpusFileDirectory();

}