package de.hterhors.obie.core.ontology.interfaces;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.IndividualFactory;
import de.hterhors.obie.core.ontology.InvestigationRestriction;
import de.hterhors.obie.core.ontology.ReflectionUtils;
import de.hterhors.obie.core.ontology.container.Slot;

public interface IOBIEThing extends Serializable {

	static final Map<Class<? extends IOBIEThing>, Constructor<? extends IOBIEThing>> cloneConstructor = new HashMap<>();

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

	default public IndividualFactory<? extends AbstractIndividual> getIndividualFactory() {
		return null;
	}

	default public AbstractIndividual getIndividual() {
		return null;
	}

	default public InvestigationRestriction getInvestigationRestriction() {
		return InvestigationRestriction.noRestrictionInstance;
	}

	default public IOBIEThing setInvestigationRestriction(InvestigationRestriction investigationRestriction) {
		if (investigationRestriction != null)
			throw new IllegalStateException(
					"Can not set InvestigationRestriction. Either method is not implemented or class is of datatype.");
		return this;
	}

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
	public void setCharacterOnset(Integer onset);

	public static Constructor<? extends IOBIEThing> getCloneConstructor(Class<? extends IOBIEThing> obieClazz) {
		try {

			Constructor<? extends IOBIEThing> values;

			if ((values = cloneConstructor.get(obieClazz)) == null) {
				values = obieClazz.getDeclaredConstructor(obieClazz);
				cloneConstructor.put(obieClazz, values);
			}

			return values;
		} catch (Exception e) {
			System.err.println(obieClazz);
			e.printStackTrace();
			System.exit(1);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	IOBIEThing getThis();

	public default List<Slot> getInvestigatedSlots() {
		return ReflectionUtils.getFields(getThis().getClass(), getThis().getInvestigationRestriction()).stream()
				.map(f -> new Slot(getThis(), f)).collect(Collectors.toList());
	}

	public default String getLabel() {
		if (getIndividual() == null)
			return getThis().getClass().getSimpleName();// getONTOLOGY_NAME();
		else
			return getIndividual().name;
	}

}
