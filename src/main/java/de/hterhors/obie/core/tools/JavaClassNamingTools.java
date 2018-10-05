package de.hterhors.obie.core.tools;

public class JavaClassNamingTools {

	private static final String INTERFACE_NAME_PREFIX = "I";

	public static String normalizeClassName(String className) {

		className = className.replaceAll("#", "_").replaceAll("[^_a-zA-Z0-9]", "");

		className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
		if (className.substring(0, 1).matches("[0-9]"))
			className = "_" + className;
		return className;
	}

	public static String getVariableName(final String normalizedClassName) {
		final String baseLabel = normalizedClassName.replaceFirst("has", "");
		return Character.toLowerCase(baseLabel.charAt(0)) + baseLabel.substring(1);
	}

	/**
	 * Uses simple heuristics to transform singular in plural.
	 * 
	 * @param singularForm
	 * @return pluralForm
	 */
	public static String toPluralForm(final String singularForm) {

		if (singularForm.endsWith("s")) {
			return singularForm;
		}
		if (singularForm.endsWith("y")) {
			return singularForm.substring(0, singularForm.length() - 1) + "ies";
		} else {
			return singularForm + "s";
		}

	}

	/**
	 * Adds interface prefix to given class name.
	 * 
	 * @return the interface name of the given class.
	 */
	public static String toInterfaceName(final String className) {
		return INTERFACE_NAME_PREFIX + className;
	}

	/**
	 * 
	 * Merges two names. If there is an overlap, it is removed by a simple
	 * heuristic. e.g. hasReferenceGroup + ExperimentalGroup have an overlap in
	 * "Group" at the end, so we remove "Group" from the relation.
	 * 
	 * @param relation
	 * @param scioClass
	 * @return
	 */
	public static String combineRelationWithClassNameAsPluralClassName(String relation, String scioClass) {
		// return toPluralForm(relation);
		return toPluralForm(combineRelationWithClassNameAsClassName(relation, scioClass));
	}

	public static String combineRelationWithClassNameAsClassName(String relation, String scioClass) {
		// return relation;
		// System.out.print("Combine: " + relation + " with " + scioClass + " to
		// : ");
		final String result;
		if (relation.equals(scioClass)) {
			result = relation;
		} else if (relation.startsWith(scioClass)) {
			result = relation;
		} else if (scioClass.startsWith(relation)) {
			result = scioClass;
		} else if (scioClass.endsWith(relation)) {
			result = scioClass;
		} else if (relation.endsWith(scioClass)) {
			result = relation;
		} else {

			boolean upperCaseLetter = false;

			int lastUpperCaseIndex = 0;
			int index = 0;
			for (int i = scioClass.length() - 1; i >= 0; i--) {
				int relationIndex = relation.length() - 1 - index;
				if (relationIndex >= 0) {
					if (scioClass.charAt(i) == relation.charAt(relationIndex)) {
						index++;
						if (Character.isUpperCase(scioClass.charAt(i))) {
							upperCaseLetter = true;
							lastUpperCaseIndex = index;
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}

			if (lastUpperCaseIndex == relation.length()) {
				result = scioClass;
			} else if (upperCaseLetter && lastUpperCaseIndex > 2) {
				result = relation.substring(0, relation.length() - lastUpperCaseIndex) + scioClass;
			} else {
				result = relation + scioClass;
			}
		}
		// System.out.println(result);

		return result;
	}

	public static String combineRelationWithClassNameAsVariableName(String relation, String scioClass) {
		// return getVariableName(relation);
		return getVariableName(combineRelationWithClassNameAsClassName(relation, scioClass));
	}

	public static String combineRelationWithClassNameAsPluralVariableName(String relation, String scioClass) {
		// return toPluralForm(getVariableName(relation));
		return toPluralForm(combineRelationWithClassNameAsVariableName(relation, scioClass));
	}

}
