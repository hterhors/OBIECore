package de.hterhors.obie.core.evaluation.iob;

public class IOBAnnotation {

	public String textMention;
	public int onset;
	public int offset;
	public int docIndex;

	public IOBAnnotation(String textMention, int onset, int offset, int docIndex) {
		this.textMention = textMention;
		this.onset = onset;
		this.offset = offset;
		this.docIndex = docIndex;
	}

	@Override
	public String toString() {
		return "IOBAnnotation [textMention=" + textMention + ", onset=" + onset + ", offset=" + offset + ", docIndex="
				+ docIndex + "]";
	}

}
