package de.hterhors.obie.core.ontology;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;

/**
 * IMPORTANT: USE WITH CAUTION! HAS STILL SOME TODOS TO CONSIDER.
 * 
 * This class holds information about what fields and if the type if the root
 * class type should be investigated by the system.
 * 
 * We can restrict single fields of a specific class or even the class type
 * itself.
 * 
 * The restrictions operate in the sampling procedure as well as in the
 * evaluation procedure. Only restricted fields are sampled / compared.
 * 
 * If the actual class type should be investigated as well (or just by its own)
 * the flag investigateClasstype is set to be true.
 * 
 * 
 * @author hterhors
 *
 * @date Dec 19, 2017
 */
public class InvestigationRestriction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class RestrictedField implements Comparable<RestrictedField>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final public String fieldName;
		final public boolean isMainField;

		public RestrictedField(String fieldName, boolean isMainField) {
			this.fieldName = fieldName;
			this.isMainField = isMainField;
		}

		/**
		 * TODO: What if a subfield has the same name as a main field?
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
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
			RestrictedField other = (RestrictedField) obj;
			if (fieldName == null) {
				if (other.fieldName != null)
					return false;
			} else if (!fieldName.equals(other.fieldName))
				return false;
			return true;
		}

		@Override
		public int compareTo(RestrictedField o) {
			return this.fieldName.compareTo(o.fieldName);
		}

		@Override
		public String toString() {
			return fieldName + "(" + isMainField + ")";
		}

	}

	/**
	 * An instance with no restrictions.
	 */
	public static final InvestigationRestriction noRestrictionInstance = new InvestigationRestriction(
			IOBIEThing.class) {

		/**
				 * 
				 */
		private static final long serialVersionUID = 1L;

		@Override
		public String summarize() {
			return "None";
		}
	};

	/**
	 * This is not the root class type but a specific sub class of the root class
	 * type. This is important as subclasses of the root class type may have
	 * different fields than the root class type itself.
	 */
//	final public Class<? extends IOBIEThing> classType;

	/**
	 * The restrictions for that specific class type. An empty set means no fields
	 * are compared / sampled.
	 */
	final private Set<RestrictedField> fieldNamesRestrictions;

	/**
	 * A set of the fieldnames that are restricted.
	 */
	final private Set<String> restrictedFieldNames;

	/**
	 * Whether the class type itself should be investigated or not. we need this
	 * since the actual object tpye is not within the fieldNamesRestriction.
	 */
	final public boolean investigateClassType;

	/**
	 * Whether the fields of the class type should be investigated or not. If this
	 * flag is set to false the fieldNamesRestrictions is unused.
	 */
	final public boolean noRestrictionsOnFields;

	/**
	 * Investigate field that are in {@link fieldNamesRestrictions} and class type
	 * if {@link InvestigationRestriction}.
	 * @param fieldNamesRestrictions
	 * @param investigateClassType
	 */
	public InvestigationRestriction(Set<RestrictedField> fieldNamesRestrictions, boolean investigateClassType) {
//		this.classType = classType;
		this.fieldNamesRestrictions = Collections.unmodifiableSet(fieldNamesRestrictions);
		this.noRestrictionsOnFields = false;
		this.investigateClassType = investigateClassType;
		this.restrictedFieldNames = this.fieldNamesRestrictions.stream().map(s -> s.fieldName)
				.collect(Collectors.toSet());
	}

	/**
	 * Investigate only fields that are in {@link fieldNamesRestrictions}. Do not
	 * investigate class type.
	 * 
	 * @param classType
	 * @param fieldNamesRestrictions
	 */
	public InvestigationRestriction(Class<? extends IOBIEThing> classType,
			Set<RestrictedField> fieldNamesRestrictions) {
//		this.classType = classType;
		this.fieldNamesRestrictions = Collections.unmodifiableSet(fieldNamesRestrictions);
		this.noRestrictionsOnFields = false;
		this.investigateClassType = false;
		this.restrictedFieldNames = this.fieldNamesRestrictions.stream().map(s -> s.fieldName)
				.collect(Collectors.toSet());
	}

	/**
	 * Investigate all fields and type if {@link investigateClassType}.
	 * 
	 * @param classType
	 * @param investigateClassType
	 */
	public InvestigationRestriction(Class<? extends IOBIEThing> classType, boolean investigateClassType) {
//		this.classType = classType;
		this.fieldNamesRestrictions = null;
		this.noRestrictionsOnFields = true;
		this.investigateClassType = investigateClassType;
		this.restrictedFieldNames = null;
	}

	/**
	 * Investigate all fields if {@link noRestrictionsOnFields} and type if
	 * {@link investigateClassType}.
	 * 
	 * 
	 * @param classType
	 * @param investigateClassType
	 */
	public InvestigationRestriction(Class<? extends IOBIEThing> classType, boolean noRestrictionsOnFields,
			boolean investigateClassType) {
//		this.classType = classType;
		this.fieldNamesRestrictions = new HashSet<>();
		this.noRestrictionsOnFields = noRestrictionsOnFields;
		this.investigateClassType = investigateClassType;
		this.restrictedFieldNames = new HashSet<>();

	}

	/**
	 * Investigate everything.
	 * 
	 * @param classType
	 */
	public InvestigationRestriction(Class<? extends IOBIEThing> classType) {
//		this.classType = classType;
		this.fieldNamesRestrictions = null;
		this.noRestrictionsOnFields = true;
		this.investigateClassType = true;
		this.restrictedFieldNames = null;
	}

	public boolean investigateField(String fieldName) {
		if (noRestrictionsOnFields)
			return true;

		if (restrictedFieldNames == null)
			return true;

		return restrictedFieldNames.contains(fieldName);
	}

	public boolean noRestrictionOnFields() {
		return this.restrictedFieldNames == null;
	}

	public Set<RestrictedField> getFieldNamesRestrictions() {
		return this.fieldNamesRestrictions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldNamesRestrictions == null) ? 0 : fieldNamesRestrictions.hashCode());
		result = prime * result + (investigateClassType ? 1231 : 1237);
		result = prime * result + (noRestrictionsOnFields ? 1231 : 1237);
		result = prime * result + ((restrictedFieldNames == null) ? 0 : restrictedFieldNames.hashCode());
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
		InvestigationRestriction other = (InvestigationRestriction) obj;
		if (fieldNamesRestrictions == null) {
			if (other.fieldNamesRestrictions != null)
				return false;
		} else if (!fieldNamesRestrictions.equals(other.fieldNamesRestrictions))
			return false;
		if (investigateClassType != other.investigateClassType)
			return false;
		if (noRestrictionsOnFields != other.noRestrictionsOnFields)
			return false;
		if (restrictedFieldNames == null) {
			if (other.restrictedFieldNames != null)
				return false;
		} else if (!restrictedFieldNames.equals(other.restrictedFieldNames))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InvestigationRestriction ["
//				+ "classType=" + classType.getSimpleName() + ","
				+ " fieldNamesRestrictions=" + fieldNamesRestrictions + ", restrictedFieldNames=" + restrictedFieldNames
				+ ", investigateClassType=" + investigateClassType + ", noRestrictionsOnFields="
				+ noRestrictionsOnFields + "]";
	}

	public static List<Set<RestrictedField>> getFieldRestrictionCombinations(Class<? extends IOBIEThing> m,
			List<RestrictedField> mainSingleFields) {
		final List<Set<RestrictedField>> fieldNamesRestrictions = new ArrayList<>();
		// Set<RestrictedField> subFields = getSubSingleFieldsRec(m);
		for (int i = 0; i < mainSingleFields.size(); i++) {
			Set<RestrictedField> restriction = new HashSet<>();
			restriction.add(mainSingleFields.get(i));
			// restriction.addAll(subFields);
			fieldNamesRestrictions.add(restriction);
			for (int j = i + 1; j < mainSingleFields.size(); j++) {
				restriction = new HashSet<>();
				restriction.add(mainSingleFields.get(i));
				restriction.add(mainSingleFields.get(j));
				// restriction.addAll(subFields);
				fieldNamesRestrictions.add(restriction);
			}
		}

		return fieldNamesRestrictions;
	}

	public static List<RestrictedField> getMainSingleFields(Class<? extends IOBIEThing> m) {

		if (m.isInterface())
			m = ReflectionUtils.getImplementationClass(m);

		List<RestrictedField> fields = getMainSingleFieldsRec(m);
		Collections.sort(fields);
		return fields;
	}

	/**
	 * Gets a list of field names for the given class type that belong to the
	 * ontological model content.
	 * 
	 * @param m
	 * @return
	 */
	private static List<RestrictedField> getMainSingleFieldsRec(Class<? extends IOBIEThing> m) {

		List<RestrictedField> fields = ReflectionUtils.getSlots(m).stream()
				.map(f -> new RestrictedField(f.getName(), true)).collect(Collectors.toList());

		return fields;
	}

	private static Set<RestrictedField> getSubSingleFieldsRec(Class<? extends IOBIEThing> m) {
		List<RestrictedField> main = getMainSingleFieldsRec(m);
		Set<RestrictedField> all = getAllSingleFieldsRec(new HashSet<>(), m);
		all.removeAll(main);/**
							 * TODO: What if a subfield has the same name as a main field?
							 */
		return all;

	}

	private static Set<RestrictedField> getAllSingleFieldsRec(Set<RestrictedField> fieldNames,
			Class<? extends IOBIEThing> m) {

		ReflectionUtils.getNonDatatypeSlots(m).forEach(field -> {
			field.setAccessible(true);
			fieldNames.add(new RestrictedField(field.getName(), false));

			try {
				if (ReflectionUtils.isAnnotationPresent(field, RelationTypeCollection.class)) {
					Class<? extends IOBIEThing> ct = ((Class<? extends IOBIEThing>) ((ParameterizedType) field
							.getGenericType()).getActualTypeArguments()[0]);
					if (ct.isAnnotationPresent(ImplementationClass.class))
						getAllSingleFieldsRec(fieldNames, ReflectionUtils.getImplementationClass(ct));
				} else {
					if (field.getType().isAnnotationPresent(ImplementationClass.class))
						getAllSingleFieldsRec(fieldNames, (Class<? extends IOBIEThing>) ReflectionUtils
								.getImplementationClass((Class<? extends IOBIEThing>) field.getType()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		for (Class<? extends IOBIEThing> assignableSubClass : ReflectionUtils.getAssignableSubClasses(m)) {
			getAllSingleFieldsRec(fieldNames, assignableSubClass);
		}

		return fieldNames;

	}

	public String summarize() {

		StringBuilder summarize = new StringBuilder("Restricted to: ");
		if (investigateClassType) {
			summarize.append("Template-Type");
		}
		if (restrictedFieldNames == null) {
			if (investigateClassType)
				summarize.append(" & ");
			summarize.append("Field(s): noRestrictions");
		} else {
			if (!restrictedFieldNames.isEmpty()) {
				if (investigateClassType)
					summarize.append(" & ");
				summarize.append("Field(s):");
				for (String fieldName : restrictedFieldNames) {
					summarize.append(fieldName);
					summarize.append(" ");
				}
			}
		}

		return summarize.toString().trim();
	}

}
