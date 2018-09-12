package de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces;

import java.io.Serializable;

import org.apache.jena.rdf.model.Model;

public interface IOBIEThing extends Serializable {

	public static class Score {

		public double value, maxValue;

		public Score() {
			value = 0;
			maxValue = 0;
		}

		public Score(double value, double maxValue) {
			this.value = value;
			this.maxValue = maxValue;
		}

		public double get() {
			if (maxValue == 0)
				return 0;
			return value / maxValue;
		}

		@Override
		public String toString() {
			return "Score [value=" + value + ", maxValue=" + maxValue + "]";
		}

	}

	/***/
	public String getAnnotationID();

	/***/
	public Integer getCharacterOffset();

	/***/
	public Integer getCharacterOnset();

	/***/
	public String getONTOLOGY_NAME();

	/***/
	public Model getRDFModel(String resourceIDPrefix);

	/***/
	public String getResourceName();

	/***/
	public String getTextMention();

	/***/
	public boolean isEmpty();

	/***/
	public void setCharacterOffset(Integer offset);

	/***/
	public void setCharacterOnset(Integer onset);

}