package de.hterhors.obie.core.owlreader;

public enum ECardinalityType {

	UNDEFINED("?:?"), SINGLE("1:1"), COLLECTION("1:m");

	final public String simpleName;

	private ECardinalityType(final String simpleName) {
		this.simpleName = simpleName;
	}

	public static ECardinalityType map(int i) {
		return ECardinalityType.values()[i];
	}

}
