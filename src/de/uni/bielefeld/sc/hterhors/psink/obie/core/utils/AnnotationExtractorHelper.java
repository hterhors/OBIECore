package de.uni.bielefeld.sc.hterhors.psink.obie.core.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.OntologyModelContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.RelationTypeCollection;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class AnnotationExtractorHelper {

	private static Logger log = LogManager.getFormatterLogger(AnnotationExtractorHelper.class);

	/**
	 * Applies the limit to properties of type OneToManyRelation. If a list has more
	 * elements than the given limit, the rest will be removed.
	 * 
	 * @param annotation the annotation.
	 * @param limit      the given limit to apply.
	 */
	public static boolean testLimitToAnnnotationElementsRecursively(IOBIEThing annotation, final int limit) {

		if (annotation == null)
			return true;

		List<Field> fields = Arrays.stream(annotation.getClass().getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(OntologyModelContent.class)).collect(Collectors.toList());

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.isAnnotationPresent(RelationTypeCollection.class)) {

					@SuppressWarnings("unchecked")
					List<IOBIEThing> elements = (List<IOBIEThing>) field.get(annotation);

					if (elements.size() > limit) {
						log.warn("Found property that elements in field: " + field.getName() + " exceeds given limit: "
								+ elements.size() + " > " + limit);
						return false;
					}
					for (IOBIEThing element : elements) {
						final boolean m = testLimitToAnnnotationElementsRecursively(element, limit);
						if (!m)
							return false;
					}
				} else {
					final boolean m = testLimitToAnnnotationElementsRecursively((IOBIEThing) field.get(annotation),
							limit);
					if (!m)
						return false;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Checks at compile time whether the assignment can be done or not.
	 * 
	 * @param typeClass
	 * @param type
	 * @return
	 */
	public static <T extends IOBIEThing> boolean isAssignAbleFrom(Class<T> typeClass, T type) {
		return type != null && typeClass.isAssignableFrom(type.getClass());
	}
}
