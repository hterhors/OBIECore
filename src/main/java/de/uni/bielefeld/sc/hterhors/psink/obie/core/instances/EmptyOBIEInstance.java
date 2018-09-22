package de.uni.bielefeld.sc.hterhors.psink.obie.core.instances;

import org.apache.jena.rdf.model.Model;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class EmptyOBIEInstance implements IOBIEThing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This instance if used to make uneven list comparisons even.
	 */
	final static public EmptyOBIEInstance emptyInstance = new EmptyOBIEInstance();

	private EmptyOBIEInstance() {
	}

	@Override
	public String getAnnotationID() {
		return null;
	}

	@Override
	public Integer getCharacterOffset() {
		return null;
	}

	@Override
	public Integer getCharacterOnset() {
		return null;
	}

	@Override
	public String getONTOLOGY_NAME() {
		return null;
	}

	@Override
	public Model getRDFModel(String resourceIDPrefix) {
		return null;
	}

	@Override
	public String getResourceName() {
		return null;
	}

	@Override
	public String getTextMention() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void setCharacterOffset(Integer offset) {
	}

	@Override
	public void setCharacterOnset(Integer onset) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EmptyOBIEInstance []";
	}

}
