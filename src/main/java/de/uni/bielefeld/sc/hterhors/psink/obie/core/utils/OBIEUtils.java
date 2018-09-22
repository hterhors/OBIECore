package de.uni.bielefeld.sc.hterhors.psink.obie.core.utils;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class OBIEUtils {

	/**
	 * TODO: put in each OBIE class. Override clone() function. 
	 * Performs a deep clone on the given object using the constructor.
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
