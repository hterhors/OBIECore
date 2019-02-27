package de.hterhors.obie.core.tools.annodb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.owlreader.ECardinalityType;
import de.hterhors.obie.core.owlreader.OWLReader;
import de.hterhors.obie.core.owlreader.container.OntologyClass;
import de.hterhors.obie.core.owlreader.container.OntologySlotData;
import de.hterhors.obie.core.tools.JavaClassNamingTools;

import java.util.Set;

public class OWLToAnnoDBConfigurationConverter {

	private static final List<String> DEFAULT_DESCRIPTION = Arrays.asList("No description provided");
	private static final Object DOCUMENTATION_DESCRIPTION_IDENTIFIER = "http://www.w3.org/2000/01/rdf-schema#description";
	private static final String SPLITTER = "\t";

	private final OWLReader dataProvider;

	private final File parent;

	private final AbstractOntologyEnvironment environment;

	public OWLToAnnoDBConfigurationConverter(File parentDirectory, AbstractOntologyEnvironment environment) {

		this.environment = environment;
		this.parent = new File(parentDirectory, "version" + environment.getOntologyVersion() + "/");

		if (!parent.exists())
			parent.mkdirs();

		this.dataProvider = new OWLReader(environment);
		try {
			write();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void write() throws FileNotFoundException {

		writeClassesFile();

		writeSubClassesFile();

		writeRelationsFile();

	}

	private void writeClassesFile() throws FileNotFoundException {
		PrintStream ps = new PrintStream(
				parent.getPath() + "/scio_v_" + environment.getOntologyVersion() + "_classes.csv");

		ps.println("#class" + SPLITTER + "isNamedIndividual" + SPLITTER + "Description");
		for (OntologyClass dataClass : dataProvider.classes) {

			final String description;

			if (!dataClass.documentation.isEmpty()) {
				if (dataClass.documentation.containsKey(DOCUMENTATION_DESCRIPTION_IDENTIFIER)) {

					description = docToString(dataClass.documentation.get(DOCUMENTATION_DESCRIPTION_IDENTIFIER));
				} else {
					System.err.println("WARN! No description found for class: " + dataClass.ontologyClassName);
					description = DEFAULT_DESCRIPTION.get(0);
				}
			} else {
				System.err.println("WARN! Class not found in ontology: " + dataClass.ontologyClassName);
				description = DEFAULT_DESCRIPTION.get(0);
			}
			boolean isNamedIndividual = dataClass.isNamedIndividual;// dataProvider.namedIndividuals.contains(dataClass);
			ps.println(dataClass.ontologyClassName + "" + SPLITTER + "" + (isNamedIndividual ? "true" : "false") + ""
					+ SPLITTER + description);
		}
		ps.close();
	}

	private void writeSubClassesFile() throws FileNotFoundException {
		PrintStream ps2 = new PrintStream(
				parent.getPath() + "/scio_v_" + environment.getOntologyVersion() + "_subclasses.csv");

		ps2.println("#superClass" + SPLITTER + "subClass");

		for (OntologyClass superClass : dataProvider.classes) {

			if (superClass.subclasses.isEmpty())
				continue;

			// }
			// for (Entry<DataClass, Set<DataClass>> superClass :
			// dataProvider.subclasses.entrySet()) {

			for (OntologyClass subClass : superClass.subclasses) {

				ps2.println(superClass.ontologyClassName + "" + SPLITTER + "" + subClass.ontologyClassName);

			}
		}

		ps2.close();
	}

	private void writeRelationsFile() throws FileNotFoundException {
		PrintStream ps3 = new PrintStream(
				parent.getPath() + "/scio_v_" + environment.getOntologyVersion() + "_relations.csv");

		ps3.println("#domainClass" + SPLITTER + "relation" + SPLITTER + "rangeClass" + SPLITTER + "from" + SPLITTER
				+ "to" + SPLITTER + "isDataTypeProperty" + SPLITTER + "mergedName" + SPLITTER + "description");

		for (OntologyClass domain : dataProvider.classes) {

			if (domain.domainRangeRelations.isEmpty())
				continue;

			for (Entry<OntologySlotData, Set<OntologyClass>> rangeRelations : domain.domainRangeRelations.entrySet()) {

				ECardinalityType cardinality = rangeRelations.getKey().cardinalityType;
				cardinality = cardinality == null ? ECardinalityType.UNDEFINED : cardinality;
				for (OntologyClass range : rangeRelations.getValue()) {
					ps3.println(domain.ontologyClassName + "" + SPLITTER + ""
							+ rangeRelations.getKey().ontologyPropertyName + "" + SPLITTER + ""
							+ range.ontologyClassName + "" + SPLITTER + "" + cardinality.simpleName.split(":")[0] + ""
							+ SPLITTER + "" + cardinality.simpleName.split(":")[1] + "" + SPLITTER + ""
/**
 * TODO: data type is buggy for SCIo because weight etc extends QUDT quantity but should be datatype classes.
 */
							
							
							+ (range.isDataType ? "true" : "false") + "" + SPLITTER + ""
							+ (cardinality == ECardinalityType.SINGLE || cardinality == ECardinalityType.UNDEFINED
									? JavaClassNamingTools.combineRelationWithClassNameAsClassName(
											rangeRelations.getKey().javaClassPropertyName, range.ontologyClassName)
									: JavaClassNamingTools.combineRelationWithClassNameAsPluralClassName(
											rangeRelations.getKey().javaClassPropertyName, range.ontologyClassName))
							+ "" + SPLITTER + "" + docToString(rangeRelations.getKey().documentation
									.getOrDefault(DOCUMENTATION_DESCRIPTION_IDENTIFIER, DEFAULT_DESCRIPTION)))

					;
				}
			}
		}

		ps3.close();
	}

	/**
	 * Converts documentation to simple String. If a doc type has multiple values
	 * they are concatenated with <code>num</code> +")"
	 * 
	 * @param docs
	 * @return
	 */
	private String docToString(List<String> docs) {

		StringBuilder docBuilder = new StringBuilder();
		Iterator<String> docIt = docs.iterator();
		int num = 1;
		while (docIt.hasNext()) {
			docBuilder.append(docIt.next().replaceAll("\\s", " "));
			if (docIt.hasNext())
				docBuilder.append(num + ")");
			num++;
		}
		return docBuilder.toString();

	}

}
