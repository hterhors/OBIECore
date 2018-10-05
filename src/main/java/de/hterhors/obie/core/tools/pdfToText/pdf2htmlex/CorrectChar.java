package de.hterhors.obie.core.tools.pdfToText.pdf2htmlex;

public class CorrectChar {

	final public String correctChar;
	/**
	 * The position of the HTML element in the HTML file. This is needed to
	 * extract the context of this char.
	 */
	final public int elementPosition;

	public CorrectChar(String correctChar, int elementPosition) {
		this.correctChar = correctChar;
		this.elementPosition = elementPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + elementPosition;
		result = prime * result + ((correctChar == null) ? 0 : correctChar.hashCode());
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
		CorrectChar other = (CorrectChar) obj;
		if (elementPosition != other.elementPosition)
			return false;
		if (correctChar == null) {
			if (other.correctChar != null)
				return false;
		} else if (!correctChar.equals(other.correctChar))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CorrectChar [correctChar=" + correctChar + ", charPosition=" + elementPosition + "]";
	}

}
