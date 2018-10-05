package de.hterhors.obie.core.tools.annodb;

public class AnnoDBAnnotation {

	public int annotationID;
	public String classType;
	public int onset;
	public int offset;
	public String tokenValue;
	public String additionalInfo;

	public AnnoDBAnnotation(int annotationID, String classType, int onset, int offset, String tokenValue,
			final String E, final String R) {
		this.annotationID = annotationID;
		this.classType = classType;
		this.onset = onset;
		this.offset = offset;
		this.tokenValue = tokenValue;
		this.additionalInfo = E == null && R == null ? "" : E == null ? R : R == null ? E : E + " / " + R;
	}

	@Override
	public String toString() {
		return "AnnoDBAnnotation [annotationID=" + annotationID + ", classType=" + classType + ", onset=" + onset
				+ ", offset=" + offset + ", tokenValue=" + tokenValue + ", additionalInfo=" + additionalInfo + "]";
	}

}
