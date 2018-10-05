package de.hterhors.obie.core.owlreader;

/**
 * 
 * @author hterhors
 *
 */
public class RDFObject {
	public final String value;
	public final String variableName;
	public final boolean isResource;

	public RDFObject(String variableName, String value, boolean isResource) {
		this.variableName = variableName;
		this.value = value;
		this.isResource = isResource;
	}

	public String getValue() {
		return value;
	}

	public String getVariableName() {
		return variableName;
	}

	public boolean isResource() {
		return isResource;
	}

	public boolean isLiteral() {
		return !isResource();
	}

	@Override
	public String toString() {
		return "RDFObject [value=" + value + ", variableName=" + variableName + ", isResource=" + isResource + "]";
	}

}
