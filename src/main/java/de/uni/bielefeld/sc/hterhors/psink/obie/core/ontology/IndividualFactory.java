package de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.OWLReader;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;

public class IndividualFactory<I extends AbstractOBIEIndividual> {

	private static final int CAN_NOT_INSTANTIATE_INDIVIDUAL = -12345678;

	final private Map<String, I> possibleInstances = new HashMap<>();

	public Collection<AbstractOBIEIndividual> getIndividuals() {
		return Collections.unmodifiableCollection(possibleInstances.values());
	}

	public I getIndividualByURI(String URI) {
		if (!possibleInstances.containsKey(URI)) {
			throw new IllegalStateException("Illegal individual type for class.");
		}
		return possibleInstances.get(URI);
	}

	public Set<String> getIndividualURIs() {
		return Collections.unmodifiableSet(possibleInstances.keySet());
	}

	/**
	 * This method is only accessible via Java-Reflections! Initializes the
	 * individual-factory for a class.
	 * 
	 * @param individualClassType
	 * @param ontologicalClass
	 * @param owlReader
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unused")
	private void sys_init(Class<I> individualClassType, OntologyClass ontologicalClass, OWLReader owlReader) {

		if (!possibleInstances.isEmpty())
			throw new IllegalStateException("Already initialized...");

		owlReader.classes.stream().filter(c -> c.isNamedIndividual)
				.filter(c -> c.superclasses.contains(ontologicalClass)).forEach(individual -> {
					try {
						Constructor<I> c = individualClassType.getDeclaredConstructor(String.class, String.class);
						c.setAccessible(true);

						possibleInstances.put(individual.fullyQualifiedOntolgyName,
								c.newInstance(individual.namespace, individual.ontologyClassName));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
						System.exit(CAN_NOT_INSTANTIATE_INDIVIDUAL);
					}
				});

	}
}
