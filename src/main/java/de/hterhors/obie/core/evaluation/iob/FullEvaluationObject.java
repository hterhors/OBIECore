package de.hterhors.obie.core.evaluation.iob;

public class FullEvaluationObject {

	public String textMention;
	public int onset;
	public int offset;
	public int docIndex;

	public FullEvaluationObject(String textMention, int onset, int offset, int docIndex) {
		this.textMention = textMention;
		this.onset = onset;
		this.offset = offset;
		this.docIndex = docIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docIndex;
		result = prime * result + offset;
		result = prime * result + onset;
		result = prime * result + ((textMention == null) ? 0 : textMention.hashCode());
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
		FullEvaluationObject other = (FullEvaluationObject) obj;
		if (docIndex != other.docIndex)
			return false;
		if (offset != other.offset)
			return false;
		if (onset != other.onset)
			return false;
		if (textMention == null) {
			if (other.textMention != null)
				return false;
		} else if (!textMention.equals(other.textMention))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FullEvaluationObject [textMention=" + textMention + ", onset=" + onset + ", offset=" + offset
				+ ", docIndex=" + docIndex + "]";
	}

}
