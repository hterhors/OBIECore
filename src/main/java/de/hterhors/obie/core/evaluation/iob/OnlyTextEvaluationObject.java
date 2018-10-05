package de.hterhors.obie.core.evaluation.iob;

public class OnlyTextEvaluationObject {

	public String textMention;
	public int docIndex;

	public OnlyTextEvaluationObject(String textMention, int docIndex) {
		this.textMention = textMention;
		this.docIndex = docIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docIndex;
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
		OnlyTextEvaluationObject other = (OnlyTextEvaluationObject) obj;
		if (docIndex != other.docIndex)
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
		return "OnlyTextEvaluationObject [textMention=" + textMention + ", docIndex=" + docIndex + "]";
	}

}
