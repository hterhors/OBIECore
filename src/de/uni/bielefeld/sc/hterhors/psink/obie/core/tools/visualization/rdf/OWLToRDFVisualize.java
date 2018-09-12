package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.rdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOntologyBuilderEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractOBIEProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.ECardinalityType;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.OWLReader;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologySlotData;

public class OWLToRDFVisualize {

	private AbstractOntologyBuilderEnvironment env;
	private OWLReader dataProvider;

	public OWLToRDFVisualize(AbstractOntologyBuilderEnvironment env, OWLReader dataProvider) throws Exception {
		this.env = env;
		this.dataProvider = new OWLReader(env.getOwlClassFilter(), env.getAdditionalPropertyNames(),
				env.getAdditionalPrefixes(), env.getOntologyFile());

		Model model = ModelFactory.createDefaultModel();

		for (OntologyClass domain : dataProvider.classes) {

			Model classModel = ModelFactory.createDefaultModel();

			final String domainClassName = domain.javaClassName;

			final Resource domainGroup = model.createResource("http://cit-ec.de/scio/" + domainClassName);

			for (Entry<OntologySlotData, Set<OntologyClass>> relations : domain.domainRangeRelations.entrySet()) {
				final String relationName = relations.getKey().ontologyPropertyName;

				ECardinalityType relationType = relations.getKey().cardinalityType;

				if (relationType == null)
					relationType = ECardinalityType.UNDEFINED;

				final String property = "http://cit-ec.de/scio/" + relationType.simpleName + " " + relationName;

				for (OntologyClass range : relations.getValue()) {
					final boolean rangeIsDataTypeProperty = range.isDataType;

					final String rangeClassName = range.javaClassName;

					if (rangeIsDataTypeProperty) {
						classModel.add(domainGroup, model.createProperty(property),
								model.createLiteral(rangeClassName));
					} else {
						classModel.add(domainGroup, model.createProperty(property),
								model.createResource("http://cit-ec.de/scio/" + rangeClassName));

					}
					model.add(classModel);
				}

			}

		}

		for (OntologyClass dataClass : dataProvider.classes) {
			if (dataClass.subclasses.isEmpty())
				continue;

			addSubclassRecursive(model, dataClass);
		}

		// for (DataClass subClass : dataProvider.subclasses.keySet()) {
		// addSubclassRecursive(model, subClass);
		// }

		// writeModel(model, "REGENBASEVisualization_Resolved", Lang.NT, 0);
		writeModel(model, "SCIOVisualization", Lang.NT, env.getOntologyVersion());

	}

	private void addSubclassRecursive(Model model, OntologyClass superClass) {
		for (OntologyClass subClass : superClass.subclasses) {

			final Resource superGroup = model.createResource("http://cit-ec.de/scio/" + superClass.javaClassName);

			final String property = "http://cit-ec.de/scio/is_a";
			final Resource subGroup = model.createResource("http://cit-ec.de/scio/" + subClass.javaClassName);

			model.add(superGroup, model.createProperty(property), subGroup);
			addSubclassRecursive(model, subClass);
		}
	}

	private void writeModel(Model model, final String modelName, Lang l, int scioVersion) throws Exception {

		RDFDataMgr.write(
				new BufferedOutputStream(new FileOutputStream(new File(
						"visualization/gen/" + modelName + "_" + scioVersion + "_" + l.getName().toLowerCase()))),
				model, l);

	}
}
