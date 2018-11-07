package de.hterhors.obie.core.ontology;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.owlreader.OWLReader;
import de.hterhors.obie.core.owlreader.container.OntologyClass;

public class OntologyInitializer {
	private static final String INDIVIDUAL_FACTORY_INIT_METHOD_NAME = "sys_init";

	public static final String INDIVIDUAL_FACTORY_FIELD_NAME = "individualFactory";

	public static final String INDIVIDUAL_CLASS_TYPE = "individualClassType";

	public static final String INDIVIDUAL_FIELD_NAME = "individual";

	public static void initializeOntology(AbstractOntologyEnvironment ontologyEnvironment) {

		try {
			final OWLReader owlReader = new OWLReader(ontologyEnvironment);

			Method systemInitMethod = IndividualFactory.class.getDeclaredMethod(INDIVIDUAL_FACTORY_INIT_METHOD_NAME,
					Class.class, OntologyClass.class, OWLReader.class);
			systemInitMethod.setAccessible(true);

			for (OntologyClass oc : owlReader.classes) {

				if (oc.isNamedIndividual)
					continue;

				@SuppressWarnings("unchecked")
				Class<? extends IOBIEThing> individualFactoryClass = (Class<? extends IOBIEThing>) Class
						.forName(ontologyEnvironment.OBIE_CLASSES_PACKAGE_NAME + oc.javaClassName);

				if (individualFactoryClass.isAnnotationPresent(DatatypeProperty.class))
					continue;

				Field individualFactoryField = individualFactoryClass.getField(INDIVIDUAL_FACTORY_FIELD_NAME);

				Field individualClassTypeField = individualFactoryClass.getField(INDIVIDUAL_CLASS_TYPE);

				systemInitMethod.invoke(individualFactoryField.get(null), individualClassTypeField.get(null), oc,
						owlReader);

			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
