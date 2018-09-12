package de.uni.bielefeld.sc.hterhors.psink.obie.core.utils;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class OBIEUtils {

	/**
	 * Performs a deep clone on the given object using the constructor. This clones
	 * only the ontology model fields.
	 */
	public static IOBIEThing deepConstructorClone(IOBIEThing scioClass) {
		if (scioClass == null)
			return null;
		try {
			return scioClass.getClass().getDeclaredConstructor(scioClass.getClass()).newInstance(scioClass);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(scioClass);
			System.exit(1);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
