package de.uni.bielefeld.sc.hterhors.psink.obie.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.ImplementationClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.OntologyModelContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.RelationTypeCollection;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class OntologyAnalyzer {

	private OntologyAnalyzer() {
	}

	private static Map<Class<? extends IOBIEThing>, Integer> hierachyCache = new HashMap<>();

	@SuppressWarnings("unchecked")
	private static int getMaxHierachyValue(Class<? extends IOBIEThing> ontologyThingClass,
			Class<? extends IOBIEThing> scioClass, int prevHierachyValue) {

		int newHierachyValue = ++prevHierachyValue;

		for (Class<?> i : scioClass.getInterfaces()) {

			if (i == ontologyThingClass) {
			} else if (ontologyThingClass.isAssignableFrom(i)) {
				newHierachyValue = Math.max(newHierachyValue,
						getMaxHierachyValue(ontologyThingClass, (Class<? extends IOBIEThing>) i, newHierachyValue));
			}
		}

		return newHierachyValue;
	}

	public static int getHierarchy(Class<? extends IOBIEThing> ontologyThingClass,
			Class<? extends IOBIEThing> scioClass) {

		if (hierachyCache.containsKey(scioClass)) {
			return hierachyCache.get(scioClass);
		}
		final int h = getMaxHierachyValue(ontologyThingClass, scioClass, -1);
		hierachyCache.put(scioClass, h);
		return h;
	}

//	public static void main(String[] args) {
//		debug = true;
//		System.out.println(getRelatedClassesTypesUnderRoot(IInvestigationMethod.class).size());
//	}

	/**
	 * Returns all related scio classes that are related either due to subclass,
	 * properties or both.
	 *
	 * @param root
	 * @return set of root related scio classes.
	 */
	public static Set<Class<? extends IOBIEThing>> getRelatedClassTypesUnderRoot(Class<? extends IOBIEThing> root) {
		Set<Class<? extends IOBIEThing>> relatedClasses = new HashSet<>();
		getRelatedClassTypesUnderRoot(relatedClasses, root);
		return relatedClasses;
	}

	static int depth = 0;
	public static boolean debug = false;

	/**
	 * Returns all related scio classes that are related either due to subclass,
	 * properties or both.
	 *
	 * @param interfaceType
	 * @return set of root related scio classes.
	 */
	@SuppressWarnings("unchecked")
	private static void getRelatedClassTypesUnderRoot(Set<Class<? extends IOBIEThing>> relatedClasses,
			Class<? extends IOBIEThing> interfaceType) {

		depth++;
		if (interfaceType.isInterface() && interfaceType.isAnnotationPresent(ImplementationClass.class)) {
			if (relatedClasses.contains(interfaceType.getAnnotation(ImplementationClass.class).get())) {
				depth--;
				return;
			}
		} else {
			if (relatedClasses.contains(interfaceType)) {
				depth--;
				return;
			}
		}

		if (debug)
			System.out.println(depth(depth) + interfaceType.getSimpleName());

		try {
			interfaceType.getAnnotation(AssignableSubInterfaces.class).get();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		for (Class<? extends IOBIEThing> assignableSubClass : interfaceType.getAnnotation(AssignableSubInterfaces.class)
				.get()) {
			getRelatedClassTypesUnderRoot(relatedClasses, assignableSubClass);
		}
		if (interfaceType.isInterface()) {
			if (interfaceType.isAnnotationPresent(ImplementationClass.class)) {
				relatedClasses.add(interfaceType.getAnnotation(ImplementationClass.class).get());
				for (Field f : interfaceType.getAnnotation(ImplementationClass.class).get().getDeclaredFields()) {
					f.setAccessible(true);
					if (f.isAnnotationPresent(OntologyModelContent.class)) {
						if (f.isAnnotationPresent(RelationTypeCollection.class)) {

							Class<? extends IOBIEThing> ct = ((Class<? extends IOBIEThing>) ((ParameterizedType) f
									.getGenericType()).getActualTypeArguments()[0]);
							getRelatedClassTypesUnderRoot(relatedClasses, ct);
						} else {
							Class<? extends IOBIEThing> ct = ((Class<IOBIEThing>) f.getType());
							getRelatedClassTypesUnderRoot(relatedClasses, ct);
						}
					}
				}
			}
		}
		depth--;
	}

	private static String depth(int depth2) {
		String depth = "";
		for (int i = 0; i < depth2; i++) {
			depth += "\t";

		}
		return depth;
	}

}
