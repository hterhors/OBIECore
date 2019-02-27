package de.hterhors.obie.core.ontology;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.hterhors.obie.core.ontology.annotations.AssignableSubClasses;
import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.DirectInterface;
import de.hterhors.obie.core.ontology.annotations.DirectSiblings;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.core.ontology.annotations.OntologyModelContent;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.annotations.SuperRootClasses;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;

public class ReflectionUtils {

	private static final Map<Class<?>, String> simpleNameChaching = new HashMap<>();

	private static final Map<Class<? extends IOBIEThing>, List<Field>> chachedFields = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, List<Field>> chachedFields_no_DT = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Map<InvestigationRestriction, Set<String>>> chachedFieldNames = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Map<InvestigationRestriction, Set<String>>> chachedFieldNames_no_DT = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Map<InvestigationRestriction, List<Field>>> chachedFields_invest = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Map<InvestigationRestriction, List<Field>>> chachedFields_invest_no_DT = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Map<String, Field>> chachedSingleFieldNames = new HashMap<>();

	private static final Map<Class<? extends IOBIEThing>, Set<Class<? extends IOBIEThing>>> assignableSubClassAnnotationCache = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Set<Class<? extends IOBIEThing>>> assignableSubInterfacesAnnotationCache = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Set<Class<? extends IOBIEThing>>> directSiblings = new HashMap<>();

	private static final Map<Class<? extends IOBIEThing>, Set<Class<? extends IOBIEThing>>> superRootClasses = new HashMap<>();

	private static final Map<Class<? extends IOBIEThing>, Class<? extends IOBIEThing>> directInterfaceAnnotationCache = new HashMap<>();
	private static final Map<Class<? extends IOBIEThing>, Class<? extends IOBIEThing>> implementationClass = new HashMap<>();

	private static final Map<Class<? extends IOBIEThing>, Map<Class<? extends Annotation>, Boolean>> isAnnotationPresent = new HashMap<>();
	private static final Map<Field, Map<Class<? extends Annotation>, Boolean>> isAnnotationPresentField = new HashMap<>();

	public static boolean isAnnotationPresent(Field field, Class<? extends Annotation> annotation) {

		Map<Class<? extends Annotation>, Boolean> annotations;
		if ((annotations = isAnnotationPresentField.get(field)) == null) {
			annotations = new HashMap<>();
			isAnnotationPresentField.put(field, annotations);
		}

		Boolean isPresent;

		if ((isPresent = annotations.get(annotation)) == null) {
			annotations.put(annotation, isPresent = field.isAnnotationPresent(annotation));
		}

		return isPresent;

	}

	public static boolean isAnnotationPresent(Class<? extends IOBIEThing> ontologyClazz,
			Class<? extends Annotation> annotation) {

		Map<Class<? extends Annotation>, Boolean> annotations;
		if ((annotations = isAnnotationPresent.get(ontologyClazz)) == null) {
			annotations = new HashMap<>();
			isAnnotationPresent.put(ontologyClazz, annotations);
		}

		Boolean isPresent;

		if ((isPresent = annotations.get(annotation)) == null) {
			annotations.put(annotation, isPresent = ontologyClazz.isAnnotationPresent(annotation));
		}

		return isPresent;

	}

	public static Class<? extends IOBIEThing> getDirectInterfaces(Class<? extends IOBIEThing> ontologyClazz) {

		Class<? extends IOBIEThing> values;

		if ((values = directInterfaceAnnotationCache.get(DirectInterface.class)) == null) {
			if (ontologyClazz.isAnnotationPresent(DirectInterface.class))
				values = ontologyClazz.getAnnotation(DirectInterface.class).get();
			else
				values = null;

			directInterfaceAnnotationCache.put(ontologyClazz, values);
		}

		return values;

	}

	public static Class<? extends IOBIEThing> getImplementationClass(Class<? extends IOBIEThing> ontologyClazz) {

		if (!ontologyClazz.isInterface())
			return null;

		Class<? extends IOBIEThing> value;
		if ((value = implementationClass.get(ImplementationClass.class)) == null) {
			final ImplementationClass a;
			if ((a = ontologyClazz.getAnnotation(ImplementationClass.class)) != null) {
				value = a.get();
				implementationClass.put(ontologyClazz, value);
			} else
				value = null;

		}

		return value;

	}

	public static Set<Class<? extends IOBIEThing>> getSuperRootClasses(Class<? extends IOBIEThing> ontologyClazz) {

		Set<Class<? extends IOBIEThing>> values;

		if ((values = superRootClasses.get(SuperRootClasses.class)) == null) {
			if (ontologyClazz.isAnnotationPresent(SuperRootClasses.class))
				values = new HashSet<>(Arrays.asList(ontologyClazz.getAnnotation(SuperRootClasses.class).get()));
			else
				values = null;

			superRootClasses.put(ontologyClazz, values);
		}

		return values;

	}

	public static Set<Class<? extends IOBIEThing>> getDirectSiblings(Class<? extends IOBIEThing> ontologyClazz) {

		Set<Class<? extends IOBIEThing>> values;

		if ((values = directSiblings.get(DirectSiblings.class)) == null) {
			if (ontologyClazz.isAnnotationPresent(DirectSiblings.class))
				values = new HashSet<>(Arrays.asList(ontologyClazz.getAnnotation(DirectSiblings.class).get()));
			else
				values = null;

			directSiblings.put(ontologyClazz, values);
		}

		return values;

	}

	public static Set<Class<? extends IOBIEThing>> getAssignableSubClasses(Class<? extends IOBIEThing> ontologyClazz) {

		Set<Class<? extends IOBIEThing>> values;

		if ((values = assignableSubClassAnnotationCache.get(AssignableSubClasses.class)) == null) {
			if (ontologyClazz.isAnnotationPresent(AssignableSubClasses.class))
				values = new HashSet<>(Arrays.asList(ontologyClazz.getAnnotation(AssignableSubClasses.class).get()));
			else
				values = null;

			assignableSubClassAnnotationCache.put(ontologyClazz, values);
		}

		return values;

	}

	public static Set<Class<? extends IOBIEThing>> getAssignableSubInterfaces(
			Class<? extends IOBIEThing> ontologyClazz) {

		Set<Class<? extends IOBIEThing>> values;

		if ((values = assignableSubInterfacesAnnotationCache.get(AssignableSubInterfaces.class)) == null) {
			if (ontologyClazz.isAnnotationPresent(AssignableSubInterfaces.class)) {
				values = new HashSet<>(Arrays.asList(ontologyClazz.getAnnotation(AssignableSubInterfaces.class).get()));
				assignableSubInterfacesAnnotationCache.put(ontologyClazz, values);
			} else
				values = null;

		}

		return values;

	}

	public static List<Field> getNonDatatypeSlots(Class<? extends IOBIEThing> clazz) {

		Objects.requireNonNull(clazz);

		if (isAnnotationPresent(clazz, DatatypeProperty.class))
			return Collections.emptyList();

		List<Field> declaredFields;

		if ((declaredFields = chachedFields_no_DT.get(clazz)) == null) {
			declaredFields = new ArrayList<>();
			for (Field f : clazz.getDeclaredFields()) {
				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				f.setAccessible(true);

				if (isAnnotationPresent(f, DatatypeProperty.class))
					continue;

				declaredFields.add(f);
			}
			chachedFields_no_DT.put(clazz, declaredFields);
		}
		return declaredFields;
	}

	public static List<Field> getSlots(Class<? extends IOBIEThing> clazz) {

		Objects.requireNonNull(clazz);

		if (isAnnotationPresent(clazz, DatatypeProperty.class))
			return Collections.emptyList();

		List<Field> declaredFields;

		if ((declaredFields = chachedFields.get(clazz)) == null) {
			declaredFields = new ArrayList<>();
			for (Field f : clazz.getDeclaredFields()) {
				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				f.setAccessible(true);
				declaredFields.add(f);
			}
			chachedFields.put(clazz, declaredFields);
		}
		return declaredFields;
	}

	public static List<Field> getNonDatatypeSlots(Class<? extends IOBIEThing> clazz,
			InvestigationRestriction investigationRestrictionRestrictions) {

		Objects.requireNonNull(clazz);

		if (isAnnotationPresent(clazz, DatatypeProperty.class))
			return Collections.emptyList();

		List<Field> declaredFields;

		Map<InvestigationRestriction, List<Field>> pntr;

		if ((pntr = chachedFields_invest_no_DT.get(clazz)) == null) {
			pntr = new HashMap<>();
			chachedFields_invest_no_DT.put(clazz, pntr);
		}

		if ((declaredFields = pntr.get(investigationRestrictionRestrictions)) == null) {
			declaredFields = new ArrayList<>();

			for (Field f : clazz.getDeclaredFields()) {

				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				if (!investigationRestrictionRestrictions.investigateField(f.getName()))
					continue;

				if (isAnnotationPresent(f, DatatypeProperty.class))
					continue;

				f.setAccessible(true);

				declaredFields.add(f);
			}
			pntr.put(investigationRestrictionRestrictions, declaredFields);
		}

		return declaredFields;
	}

	public synchronized static List<Field> getSlots(Class<? extends IOBIEThing> clazz,
			InvestigationRestriction investigationRestriction) {

		Objects.requireNonNull(clazz);

		if (isAnnotationPresent(clazz, DatatypeProperty.class))
			return Collections.emptyList();

		List<Field> declaredFields;

		Map<InvestigationRestriction, List<Field>> pntr;

		if ((pntr = chachedFields_invest.get(clazz)) == null) {
			pntr = new HashMap<>();
			chachedFields_invest.put(clazz, pntr);
		}

		if ((declaredFields = pntr.get(investigationRestriction)) == null) {

			declaredFields = new ArrayList<>();

			for (Field f : clazz.getDeclaredFields()) {

				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				if (!investigationRestriction.investigateField(f.getName()))
					continue;

				f.setAccessible(true);
				declaredFields.add(f);
			}
			pntr.put(investigationRestriction, declaredFields);
		}

		return declaredFields;
	}

	public static Set<String> getNonDatatypeSlotNames(Class<? extends IOBIEThing> clazz,
			InvestigationRestriction investigationRestrictionRestrictions) {

		Objects.requireNonNull(clazz);

		if (isAnnotationPresent(clazz, DatatypeProperty.class))
			return Collections.emptySet();

		Set<String> declaredFieldNames;

		Map<InvestigationRestriction, Set<String>> pntr;

		if ((pntr = chachedFieldNames_no_DT.get(clazz)) == null) {
			pntr = new HashMap<>();
			chachedFieldNames_no_DT.put(clazz, pntr);
		}

		if ((declaredFieldNames = pntr.get(investigationRestrictionRestrictions)) == null) {
			declaredFieldNames = new HashSet<>();
			for (Field f : clazz.getDeclaredFields()) {

				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				if (!investigationRestrictionRestrictions.investigateField(f.getName()))
					continue;

				declaredFieldNames.add(f.getName());
			}
			pntr.put(investigationRestrictionRestrictions, declaredFieldNames);
		}

		return declaredFieldNames;
	}

	public static Set<String> getSlotNames(Class<? extends IOBIEThing> clazz,
			InvestigationRestriction investigationRestrictionRestrictions) {

		Objects.requireNonNull(clazz);

//		if (isAnnotationPresent(clazz, DatatypeProperty.class))
//			return Collections.emptySet();
//		
		Set<String> declaredFieldNames;

		Map<InvestigationRestriction, Set<String>> pntr;

		if ((pntr = chachedFieldNames.get(clazz)) == null) {
			pntr = new HashMap<>();
			chachedFieldNames.put(clazz, pntr);
		}

		if ((declaredFieldNames = pntr.get(investigationRestrictionRestrictions)) == null) {
			declaredFieldNames = new HashSet<>();
			for (Field f : clazz.getDeclaredFields()) {

				if (!isAnnotationPresent(f, OntologyModelContent.class))
					continue;

				if (!investigationRestrictionRestrictions.investigateField(f.getName()))
					continue;

				declaredFieldNames.add(f.getName());
			}
			pntr.put(investigationRestrictionRestrictions, declaredFieldNames);
		}

		return declaredFieldNames;
	}

	public static Field getAccessibleFieldByName(Class<? extends IOBIEThing> obieClazz, final String fieldName) {
		Objects.requireNonNull(obieClazz);

		Objects.requireNonNull(fieldName);

		Field declaredField;

		Map<String, Field> pntr;

		if ((pntr = chachedSingleFieldNames.get(obieClazz)) == null) {
			pntr = new HashMap<>();
			chachedSingleFieldNames.put(obieClazz, pntr);
		}

		if ((declaredField = pntr.get(fieldName)) == null) {
			try {
				declaredField = obieClazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				pntr.put(fieldName, null);
				return null;
			}
			declaredField.setAccessible(true);
			pntr.put(fieldName, declaredField);
		}

		Objects.requireNonNull(declaredField);
		return declaredField;
	}

	public static String simpleName(final Class<?> clazz) {

		String simpleName;

		if ((simpleName = simpleNameChaching.get(clazz)) == null) {
			simpleName = clazz.getSimpleName();
			simpleNameChaching.put(clazz, simpleName);
		}

		return simpleName;
	}

	/**
	 * Returns the type of the field. If the field is of type list, the generic type
	 * is returned.
	 * 
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends IOBIEThing> getFieldType(final Field field) {
		try {
			if (isAnnotationPresent(field, RelationTypeCollection.class)) {
				return (Class<? extends IOBIEThing>) ((ParameterizedType) field.getGenericType())
						.getActualTypeArguments()[0];
			} else {
				return (Class<? extends IOBIEThing>) field.getType();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}
