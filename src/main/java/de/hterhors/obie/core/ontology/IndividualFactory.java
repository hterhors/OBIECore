package de.hterhors.obie.core.ontology;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hterhors.obie.core.owlreader.OWLReader;
import de.hterhors.obie.core.owlreader.container.OntologyClass;

public class IndividualFactory<I extends AbstractIndividual> {

	private static final int CAN_NOT_INSTANTIATE_INDIVIDUAL = -12345678;

	final private Map<String, I> possibleInstances = new HashMap<>();

	/**
	 * Flag that tells if this individual factory was initialized or not.
	 */
	private boolean initialized = false;

	public Collection<AbstractIndividual> getIndividuals() {
		if (!initialized) {
			throw new IllegalStateException(
					"IndividualFactory not initialized. Call OntologyInitializer.initializeOntology(ontologyEnvironment);");
		}
		return Collections.unmodifiableCollection(possibleInstances.values());
	}

	public I getIndividualByURI(String URI) {
		if (!initialized) {
			throw new IllegalStateException(
					"IndividualFactory not initialized. Call OntologyInitializer.initializeOntology(ontologyEnvironment);");
		}

		if (URI == null)
			return null;

		if (!possibleInstances.containsKey(URI)) {
			throw new IllegalStateException("Illegal individual:" + URI);
		}
		return possibleInstances.get(URI);
	}

	public Set<String> getIndividualURIs() {
		if (!initialized) {
			throw new IllegalStateException(
					"IndividualFactory not initialized. Call OntologyInitializer.initializeOntology(ontologyEnvironment);");
		}
		return Collections.unmodifiableSet(possibleInstances.keySet());
	}

	/**
	 * This method is only accessible via Java-Reflections! Initializes the
	 * individual-factory for an ontological class.
	 * 
	 * @param individualClassType
	 * @param ontologicalClass
	 * @param owlReader
	 */
	@SuppressWarnings("unused")
	private synchronized void sys_init(Class<I> individualClassType, OntologyClass ontologicalClass,
			OWLReader owlReader) {

		if (initialized)
			return;

		owlReader.classes.stream().filter(c -> c.isNamedIndividual)
				.filter(c -> c.superclasses.contains(ontologicalClass)).forEach(individual -> {
					try {
						Constructor<I> c = individualClassType.getDeclaredConstructor(String.class, String.class);
						c.setAccessible(true);

						final I indInstance = c.newInstance(individual.namespace, individual.ontologyClassName);
						final String key = indInstance.nameSpace + indInstance.name;

						possibleInstances.put(key, indInstance);

					} catch (Exception e) {
						e.printStackTrace();
						System.exit(CAN_NOT_INSTANTIATE_INDIVIDUAL);
					}
				});

		initialized = true;
	}
}
