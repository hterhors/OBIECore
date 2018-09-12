package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOntologyBuilderEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.SuperRootClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractOBIEProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.JavaClassNamingTools;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.OWLReader;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.ClassWithDataTypeProperties;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.ClassWithOutDataTypeProperties;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.DataTypeClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.GroupNodes;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.IGraphMLContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.NamedIndividual;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.ObjectTypePropertyEdge;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates.SubClassEdge;

public class OWL2GraphmlConverter {

	class Triple {
		final String t1;
		final String t2;
		final String t3;

		private Triple(String t1, String t2, String t3) {
			this.t1 = t1;
			this.t2 = t2;
			this.t3 = t3;
		}

	}

	class Tuple {
		final String t1;
		final String t2;

		private Tuple(String t1, String t2) {
			this.t1 = t1;
			this.t2 = t2;
		}

	}

	protected AbstractOntologyBuilderEnvironment env;
	private int version = env.getOntologyVersion();

	protected OWLReader dataProvider = new OWLReader(env.getOwlClassFilter(), env.getAdditionalPropertyNames(),env.getAdditionalPrefixes(),
			env.getOntologyFile());

	public OWL2GraphmlConverter(AbstractOntologyBuilderEnvironment env) throws IOException, Exception {

		this.env = env;

		List<IGraphMLContent> listOfUnGroupedContent = new ArrayList<>();

		String csvClasses = "csv/version" + version + "/scio_v_" + version + "_classes.csv";
		List<String> classes = Files.readAllLines(new File(csvClasses).toPath()).stream().skip(1)
				.collect(Collectors.toList());
		/*
		 * classname, vis_group
		 */
		Map<String, String> classGroups = new HashMap<>();

		/*
		 * vis_group,ClassNames
		 */
		Map<String, Set<String>> visGroups = new HashMap<>();

		Set<String> classesWithoutDataTypeProperties = new HashSet<>();
		Set<String> namedIndividuals = new HashSet<>();
		for (String scioClass : classes) {
			String[] data = scioClass.split("\t");
			final String className = data[0];

			for (OntologyClass dataClass : dataProvider.classes) {

				if (dataClass.documentation.isEmpty())
					continue;

				if (dataClass.javaClassName.equals(className)
						&& dataClass.documentation.containsKey("http://psink.de/scio/visualization_container")) {
					classGroups.put(className,
							dataClass.documentation.get("http://psink.de/scio/visualization_container").get(0));
					visGroups.putIfAbsent(
							dataClass.documentation.get("http://psink.de/scio/visualization_container").get(0),
							new HashSet<>());
					visGroups.get(dataClass.documentation.get("http://psink.de/scio/visualization_container").get(0))
							.add(className);
					break;
				}
			}

			if (data[1].equals("true")) {
				namedIndividuals.add(className);
			} else {
				classesWithoutDataTypeProperties.add(className);
			}

		}

		/*
		 * COMPLETE VIS GROUPS
		 */
		for (String scioClass : classes) {
			String[] data = scioClass.split("\t");
			final String className = data[0];
			Class<? extends IOBIEThing>[] superRootClasses = Class
					.forName(env.OBIE_CLASSES_PACKAGE_NAME + JavaClassNamingTools.normalizeClassName(className))
					.getAnnotation(SuperRootClasses.class).get();

			if (superRootClasses.length > 1) {
				throw new NotImplementedException(
						"Can not handle classes that have more than 1 superRootClass: " + className);
			}

			final String rootSuperClass =

					superRootClasses[0]

							.getSimpleName();

			final String group = classGroups.get(rootSuperClass);
			if (group != null) {
				/*
				 * No multiple groups for single classes
				 */
				if (!classGroups.containsKey(className)) {
					visGroups.get(group).add(className);
					classGroups.put(className, group);
				}
			}

		}

		String csvProperties = "csv/version" + version + "/scio_v_" + version + "_relations.csv";
		List<String> properties = Files.readAllLines(new File(csvProperties).toPath()).stream().skip(1)
				.collect(Collectors.toList());

		Map<String, Set<String>> classesWithDataTypeProperties = new HashMap<>();

		Set<Triple> objectTypeProperties = new HashSet<>();
		Set<String> dataTypeClasses = new HashSet<>();
		for (String property : properties) {
			String[] data = property.split("\t");

			final boolean isDataTypeProperty = data[5].equals("true");
			if (isDataTypeProperty) {
				classesWithDataTypeProperties.putIfAbsent(data[0], new HashSet<>());
				classesWithDataTypeProperties.get(data[0]).add(data[1]);
				/*
				 * Remove Data Type property classes.
				 */
				classesWithoutDataTypeProperties.remove(data[2]);
				dataTypeClasses.add(data[2]);
			} else {
				objectTypeProperties.add(new Triple(data[0], data[1], data[2]));
			}
		}

		String csvSubClasses = "csv/version" + version + "/scio_v_" + version + "_subclasses.csv";
		List<String> subClasses = Files.readAllLines(new File(csvSubClasses).toPath()).stream().skip(1)
				.collect(Collectors.toList());

		Set<Tuple> subClassRelations = new HashSet<>();
		for (String string : subClasses) {
			String[] data = string.split("\t");
			subClassRelations.add(new Tuple(data[0], data[1]));
		}

		/*
		 * groupname, GroupContent
		 */
		Map<String, List<IGraphMLContent>> groupedContent = new HashMap<>();

		for (String className : classesWithoutDataTypeProperties) {
			IGraphMLContent content = new ClassWithOutDataTypeProperties.Builder(getIDForName(className))
					.setName(className).build();

			if (classGroups.containsKey(className)) {
				final String groupName = classGroups.get(className);
				groupedContent.putIfAbsent(groupName, new ArrayList<>());
				groupedContent.get(groupName).add(content);
			} else {
				listOfUnGroupedContent.add(content);
			}

		}
		for (String className : namedIndividuals) {
			IGraphMLContent content = new NamedIndividual.Builder(getIDForName(className)).setName(className).build();
			if (classGroups.containsKey(className)) {
				final String groupName = classGroups.get(className);
				groupedContent.putIfAbsent(groupName, new ArrayList<>());
				groupedContent.get(groupName).add(content);
			} else {
				listOfUnGroupedContent.add(content);
			}

		}

		for (String className : dataTypeClasses) {
			IGraphMLContent content = new DataTypeClass.Builder(getIDForName(className)).setName(className).build();
			if (classGroups.containsKey(className)) {
				final String groupName = classGroups.get(className);
				groupedContent.putIfAbsent(groupName, new ArrayList<>());
				groupedContent.get(groupName).add(content);
			} else {
				listOfUnGroupedContent.add(content);
			}
		}

		for (Entry<String, Set<String>> c : classesWithDataTypeProperties.entrySet()) {
			final String className = c.getKey();
			List<String> atts = new ArrayList<>(c.getValue());
			Collections.sort(atts);
			IGraphMLContent content = new ClassWithDataTypeProperties.Builder(getIDForName(className))
					.setName(className).setAttributes(atts).build();

			if (classGroups.containsKey(className)) {
				final String groupName = classGroups.get(className);
				groupedContent.putIfAbsent(groupName, new ArrayList<>());
				groupedContent.get(groupName).add(content);
			} else {
				listOfUnGroupedContent.add(content);
			}
		}

		for (Triple t : objectTypeProperties) {
			final String className = t.t1;
			final String relationName = t.t2;
			final String rangeName = t.t3;
			listOfUnGroupedContent.add(
					new ObjectTypePropertyEdge.Builder(relationID++, getIDForName(className), getIDForName(rangeName))
							.setLabel(relationName).build());
		}

		for (Tuple sc : subClassRelations) {
			final String scn = sc.t1;
			final String cn = sc.t2;

			listOfUnGroupedContent
					.add(new SubClassEdge.Builder(relationID++, getIDForName(cn), getIDForName(scn)).build());
		}

		for (Entry<String, List<IGraphMLContent>> grouped : groupedContent.entrySet()) {
			IGraphMLContent gC = new GroupNodes.Builder(getIDForName(grouped.getKey())).setContent(grouped.getValue())
					.setName(grouped.getKey()).build();
			listOfUnGroupedContent.add(gC);

		}

		listOfUnGroupedContent.forEach(System.out::println);
		GraphML graph = new GraphML(listOfUnGroupedContent);
		PrintStream ps = new PrintStream(new File("graphml/scio_v_" + version + ".graphml"));
		ps.println(graph);
		ps.close();
	}

	int relationID = 0;

	private Map<String, Integer> nameToIDMap = new HashMap<>();

	private int getIDForName(final String name) {
		final int id = nameToIDMap.getOrDefault(name, nameToIDMap.size());
		nameToIDMap.putIfAbsent(name, id);
		System.out.println("n" + id + " -- " + name);
		return id;
	}

}
