package de.hterhors.obie.core.projects;

import java.io.File;
import java.io.Serializable;

import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;

public abstract class AbstractProjectEnvironment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected AbstractProjectEnvironment() {
	}

	public final String PACKAGE_PREFIX = "package ";

	public final String OBIE_ANNOTATIONS_PACKAGE_NAME = "de.uni.bielefeld.sc.hterhors.psink.obie.annotations.*";

	public abstract Class<? extends IOBIEThing> getOntologyThingInterface();

	public abstract File getRawCorpusFile();

	public abstract File getBigramCorpusFileDirectory();

	public abstract String getCorpusPrefix();

}