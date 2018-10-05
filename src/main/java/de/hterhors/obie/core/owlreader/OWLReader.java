package de.hterhors.obie.core.owlreader;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.owlreader.container.OntologyClass;
import de.hterhors.obie.core.owlreader.container.OntologySlotData;
import de.hterhors.obie.core.tools.JavaClassNamingTools;

/**
 * Reads the OWL file and converts it into java classes for further processing.
 * 
 * @author hterhors
 *
 * @date Nov 20, 2017
 */
public class OWLReader implements Serializable {

	/**
	 * 
	 */
	final private static long serialVersionUID = 1L;

	public static Logger log = LogManager.getFormatterLogger(OWLReader.class.getSimpleName());

	final private static Map<String, OntologyClass> classFactory = new HashMap<>();
	final private static Map<String, OntologySlotData> slotFactory = new HashMap<>();

	/**
	 * Captures the annotation if the class is of type clinicalTerm only or
	 * pre-clinical term only.
	 * 
	 * If the value is true this means its only a clinical term, if the value is
	 * false it means it is only a pre clinical term. If the class is not presented
	 * in this map it means the class can be used in both ways.
	 * 
	 */
	final private static Pattern IRI_PATTERN = Pattern.compile("(http.*/)(.*)");

	final static String GROUP_CONCAT_SEPARATOR = ",";

	private final static String VARIABLE_NAME_DOMAIN = "domain";
	private final static String VARIABLE_NAME_PROPERTY = "property";
	private final static String VARIABLE_NAME_RANGE = "range";

	private final static String VARIABLE_NAME_CLASS = "class";

	private final static String VARIABLE_NAME_CARDINALITY = "cardinality";

	private final static String VARIABLE_NAME_SUBCLASS = "subclass";
	private final static String VARIABLE_NAME_SUPERCLASS = "superclass";

	private static final String VARIABLE_NAME_ADDITIONAL_INFO = "additionalInfo";
	private final static String VARIABLE_NAME_RELATION_TYPE = "relationType";

	private static final String VARIABLE_NAME_DT_RESTRICTION = "restriction";

	final private static String stdPrefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + "PREFIX scio: <http://psink/scio>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>";

	final private String allPrefixes;

	private static final String OWL_CLASS_NAME = "http://www.w3.org/2002/07/owl#Class";

	private static final String OWL_NAMED_INDIVIDUAL_NAME = "http://www.w3.org/2002/07/owl#NamedIndividual";

	public boolean irgnoreOWLClasses = true;

	private final List<String> additionalInfoVariableNames;

	public Set<OntologyClass> classes = new HashSet<>();

	public final IClassFilter classFilter;

	private String defaultPrefix;

	/**
	 * 
	 * @param classFilter
	 * @param additionalInfoVariableNames
	 * @param additionalPrefixes          TODO: as List
	 * @param ontologyFile
	 */
	public OWLReader(IClassFilter classFilter, List<String> additionalInfoVariableNames, String additionalPrefixes,
			File ontologyFile, final String defaultPrefix) {

		Objects.requireNonNull(defaultPrefix);

		this.defaultPrefix = defaultPrefix;
		this.classFilter = classFilter;
		this.additionalInfoVariableNames = additionalInfoVariableNames;
		this.allPrefixes = stdPrefixes + " " + additionalPrefixes;
		log.info("Read owl file...");
		ApacheJenaDatabase db = new ApacheJenaDatabase(ontologyFile);
		log.info("Successfully read the owl file!");

		collectClasses(db);

		collectDataTypeProperties(db);

		collectSubClasses(db);

		collectDomainUnionOfObjectProperties(db);

		collectObjectProperties(db);

		collectDomainUnionOfDatatypeProperties(db);

		collectNamedIndividuals(db);

		collectMinCardinalityProperties(db);

		collectExactCardinalityProperties(db);

		collectAdditionalInfoForClasses(db);

		collectAdditionalInfoForProperties(db);

		applyFilter();

	}

	public OWLReader(AbstractOntologyEnvironment environment) {
		this(environment.getOwlClassFilter(), environment.getAdditionalPropertyNames(),
				environment.getAdditionalPrefixes(), environment.getOntologyFile(), environment.getDataNameSpace());
	}

	private void applyFilter() {
		for (Iterator<OntologyClass> classIterator = this.classes.iterator(); classIterator.hasNext();) {

			OntologyClass ontologyClassData = classIterator.next();

			if (!classFilter.matchesCondition(ontologyClassData))
				classIterator.remove();
			else {

				for (Iterator<OntologyClass> subClassIterator = ontologyClassData.subclasses
						.iterator(); subClassIterator.hasNext();) {

					if (!classFilter.matchesCondition(subClassIterator.next()))
						subClassIterator.remove();

				}
			}
		}
	}

	private void collectAdditionalInfoForClasses(ApacheJenaDatabase db) {
		for (String property : additionalInfoVariableNames) {

			for (Map<String, RDFObject> data : extractAdditionalInfoForClasses(db, property).queryData) {
				final RDFObject doc = data.get(VARIABLE_NAME_ADDITIONAL_INFO);

				if (doc == null)
					continue;

				OntologyClass c = dataClassFactory(data.get(VARIABLE_NAME_DOMAIN).value, defaultPrefix);

				c.documentation.putIfAbsent(property, new ArrayList<>());
				c.documentation.get(property).add(doc.value);

			}
		}
	}

	private void collectAdditionalInfoForProperties(ApacheJenaDatabase db) {

		/**
		 * Ugly do it twice wit h true and false as default.
		 */
		for (String property : additionalInfoVariableNames) {
			for (Map<String, RDFObject> data : extractAdditionalInfoForProperties(db, property).queryData) {

				final RDFObject doc = data.get(VARIABLE_NAME_ADDITIONAL_INFO);

				if (doc == null)
					continue;

				final ECardinalityType cardinality = getCardinalityFromRelationType(data);

				OntologySlotData defaultSlot = propertyFactory(data.get(VARIABLE_NAME_DOMAIN).value, cardinality, true);

				defaultSlot.documentation.putIfAbsent(property, new ArrayList<>());
				defaultSlot.documentation.get(property).add(doc.value);

				OntologySlotData nonDefault = propertyFactory(data.get(VARIABLE_NAME_DOMAIN).value, cardinality, false);

				nonDefault.documentation.put(property, new ArrayList<>());
				nonDefault.documentation.get(property).add(doc.value);
			}
		}

	}

	private void collectObjectProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractObjectProperties(db).queryData) {

			OntologyClass domainClass = classFactory(data.get(VARIABLE_NAME_DOMAIN).value, false, defaultPrefix);
			OntologyClass rangeClass = classFactory(data.get(VARIABLE_NAME_RANGE).value, false, defaultPrefix);

			final ECardinalityType cardinality = getCardinalityFromRelationType(data);

			OntologySlotData relation = propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, cardinality, true);

			this.classes.add(domainClass);
			this.classes.add(rangeClass);

			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
			domainClass.domainRangeRelations.get(relation).add(rangeClass);
		}
	}

	private void collectExactCardinalityProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractExactCardinalities(db).queryData) {
			OntologyClass domainClass = classFactory(data.get(VARIABLE_NAME_DOMAIN).value, false, defaultPrefix);

			OntologyClass rangeClass = classFactory(data.get(VARIABLE_NAME_RANGE).value, false, defaultPrefix);

			final int cardinality = Integer.parseInt(data.get(VARIABLE_NAME_CARDINALITY).value);

			ECardinalityType cardinalityType;

			if (cardinality == 1)
				cardinalityType = ECardinalityType.SINGLE;
			else if (cardinality > 1)
				cardinalityType = ECardinalityType.COLLECTION;
			else
				throw new IllegalStateException("The cardinality: " + cardinality + " is not valid.");

			this.classes.add(domainClass);
			this.classes.add(rangeClass);

			OntologySlotData relation = propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, cardinalityType, false);
			/**
			 * Remove if same property was already set to single-cardinality by default
			 * object type property.
			 */
			domainClass.domainRangeRelations
					.remove(propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, ECardinalityType.SINGLE, true));

			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
			domainClass.domainRangeRelations.get(relation).add(rangeClass);
		}
	}

	private void collectMinCardinalityProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractMinCardinalities(db).queryData) {
			OntologyClass domainClass = classFactory(data.get(VARIABLE_NAME_DOMAIN).value, false, defaultPrefix);
			OntologySlotData relation = propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value,
					ECardinalityType.COLLECTION, false);
			OntologyClass rangeClass = classFactory(data.get(VARIABLE_NAME_RANGE).value, false, defaultPrefix);

			this.classes.add(domainClass);
			this.classes.add(rangeClass);

			/**
			 * Remove if same property was already set to single-cardinality by default
			 * object type property.
			 */
			domainClass.domainRangeRelations
					.remove(propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, ECardinalityType.SINGLE, true));

			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
			domainClass.domainRangeRelations.get(relation).add(rangeClass);
		}
	}

	private void collectNamedIndividuals(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractRDFType(db).queryData) {
			OntologyClass subClass = classFactory(data.get(VARIABLE_NAME_SUBCLASS).value, false, defaultPrefix);
			OntologyClass superClass = classFactory(data.get(VARIABLE_NAME_SUPERCLASS).value, false, defaultPrefix);

			if (includeOntologyClass(superClass))
				continue;

			this.classes.add(superClass);
			this.classes.add(subClass);

			subClass.isNamedIndividual = true;

			superClass.subclasses.add(subClass);
			subClass.superclasses.add(superClass);
		}
	}

	private boolean includeOntologyClass(OntologyClass superClass) {
		return irgnoreOWLClasses && (superClass.fullyQualifiedOntolgyName.equals(OWL_CLASS_NAME)
				|| superClass.fullyQualifiedOntolgyName.equals(OWL_NAMED_INDIVIDUAL_NAME));
	}

	private void collectDomainUnionOfDatatypeProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractDomainUnionOfDatatypeProperties(db).queryData) {
//			System.err.println(data);
			throw new NotImplementedException("extractDomainUnionOfDatatypeProperties is not implemented yet.");
//
//			DataClass domainClass = dataClassFactory(list.get(2).value, false);
//			DataClass rangeClass = dataClassFactory(list.get(1).value, false);
//			DataRelation relation = dataRrelationFactory(list.get(0).value);
//
//			this.classes.add(domainClass);
//			this.classes.add(rangeClass);
//
//			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
//			domainClass.domainRangeRelations.get(relation).add(rangeClass);
		}
	}

	private void collectDomainUnionOfObjectProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractDomainUnionOfObjectProperties(db).queryData) {

			OntologyClass domainClass = classFactory(data.get(VARIABLE_NAME_DOMAIN).value, false, defaultPrefix);
			OntologyClass rangeClass = classFactory(data.get(VARIABLE_NAME_RANGE).value, false, defaultPrefix);

			final ECardinalityType cardinality = getCardinalityFromRelationType(data);

			OntologySlotData relation = propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, cardinality, true);

			this.classes.add(domainClass);
			this.classes.add(rangeClass);

			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
			domainClass.domainRangeRelations.get(relation).add(rangeClass);
		}
	}

	private void collectSubClasses(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> scr : extractSubClassOf(db).queryData) {
			OntologyClass subClass = classFactory(scr.get(VARIABLE_NAME_SUBCLASS).value, false, defaultPrefix);
			OntologyClass superClass = classFactory(scr.get(VARIABLE_NAME_SUPERCLASS).value, false, defaultPrefix);

			if (includeOntologyClass(superClass))
				continue;

			this.classes.add(superClass);
			this.classes.add(subClass);

			superClass.subclasses.add(subClass);
			subClass.superclasses.add(superClass);

		}
	}

	private void collectDataTypeProperties(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> data : extractDatatypeProperties(db).queryData) {

			final EDatatypeRestriction restriction = mapRestriction(data.get(VARIABLE_NAME_DT_RESTRICTION));

			final OntologyClass domainClass = dataClassFactory(data.get(VARIABLE_NAME_DOMAIN).value, defaultPrefix);

			final ECardinalityType cardinality = getCardinalityFromRelationType(data);

			final OntologySlotData relation = propertyFactory(data.get(VARIABLE_NAME_PROPERTY).value, cardinality,
					true);
			/*
			 * Create artificial class for datatype properties.
			 */
			final OntologyClass datatypeRangeClass = classFactory(data.get(VARIABLE_NAME_PROPERTY).value, true,
					defaultPrefix);

			datatypeRangeClass.restriction = restriction;

			this.classes.add(domainClass);
			this.classes.add(datatypeRangeClass);

			domainClass.domainRangeRelations.putIfAbsent(relation, new HashSet<>());
			domainClass.domainRangeRelations.get(relation).add(datatypeRangeClass);

		}
	}

	private ECardinalityType getCardinalityFromRelationType(Map<String, RDFObject> data) {
		final ECardinalityType cardinality;
		if (data.containsKey(VARIABLE_NAME_RELATION_TYPE)) {
			final int value = Integer.parseInt(data.get(VARIABLE_NAME_RELATION_TYPE).value);
			cardinality = ECardinalityType.map(value);
		} else {
			/*
			 * TODO: DEFAULT
			 */
			cardinality = ECardinalityType.SINGLE;
		}
		return cardinality;
	}

	private void collectClasses(ApacheJenaDatabase db) {
		for (Map<String, RDFObject> list : extractAllClasses(db).queryData) {
			this.classes.add(classFactory(list.get(VARIABLE_NAME_CLASS).value, false, defaultPrefix));
		}
	}

	private OntologySlotData propertyFactory(String relation, ECardinalityType cardinality, boolean setByDefault) {
		final String key = relation + cardinality;

		if (slotFactory.containsKey(key)) {
			return slotFactory.get(key);
		} else {

			Matcher m = IRI_PATTERN.matcher(relation);

			m.find();

			final String IRI = m.group(1);
			final String relationName = m.group(2);

			try {
				OntologySlotData dr;
				dr = new OntologySlotData(URLDecoder.decode(IRI, "UTF-8"), URLDecoder.decode(relationName, "UTF-8"),
						cardinality, setByDefault);

				slotFactory.put(key, dr);
				return dr;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			return null;
		}
	}

	public static OntologyClass dataClassFactory(String owlClass, String defaultPrefix) {
		return classFactory(owlClass, false, defaultPrefix);
	}

	public static OntologyClass classFactory(String ontologyClassName, boolean artificialClass,
			final String defaultPrefix) {

		if (classFactory.containsKey(ontologyClassName)) {
			return classFactory.get(ontologyClassName);
		} else {

			Matcher m = IRI_PATTERN.matcher(ontologyClassName);
			m.find();

			final String IRI;
			final String name;
			if (artificialClass) {
				IRI = defaultPrefix;
				name = JavaClassNamingTools.normalizeClassName(JavaClassNamingTools.getVariableName(m.group(2)));
			} else {
				IRI = m.group(1);
				name = m.group(2);
			}

			OntologyClass dc;
			try {
				dc = new OntologyClass(URLDecoder.decode(IRI, "UTF-8"), URLDecoder.decode(name, "UTF-8"));
				dc.isDataType = artificialClass;

				classFactory.put(ontologyClassName, dc);
				return dc;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			return null;
		}

	}

	private static EDatatypeRestriction mapRestriction(final RDFObject rdfObject) {

		if (rdfObject == null) {
			return EDatatypeRestriction.STRING;
		}

		switch (rdfObject.value.toLowerCase()) {
		case "xsd:string":
			return EDatatypeRestriction.STRING;
		case "xsd:integer":
			return EDatatypeRestriction.INTEGER;
		case "xsd:float":
			return EDatatypeRestriction.FLOAT;
		default:
			log.warn("Can not interprete datatype restriction for value: " + rdfObject
					+ " return string as defautl value.");
			return EDatatypeRestriction.STRING;
		}

	}

	private QueryResult extractAllClasses(ApacheJenaDatabase db) {
		log.debug("_________Extract all classes__________");
		return db.select(allPrefixes +
//				
				"select distinct ?" + VARIABLE_NAME_CLASS + " where {"
//
				+ "?" + VARIABLE_NAME_CLASS + " a owl:Class . "
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_CLASS
//
				+ "), \"http:\"))" + "}");
	}

//	private QueryResult extractAdditionalInfoForClasses(ApacheJenaDatabase db, final String property) {
//		log.debug("_________Extract additional info for classes__________");
//		
//		return db.select(allPrefixes +
////				
//				"select distinct ?" + VARIABLE_NAME_DOMAIN + " (GROUP_CONCAT(distinct STR(?addInfo) ; separator = \""
//				+ GROUP_CONCAT_SEPARATOR + "\") as ?" + VARIABLE_NAME_ADDITIONAL_INFO + ") where { "
////				
//				+ " ?" + VARIABLE_NAME_DOMAIN + " ?x ?y."
////
//				+ " { ?" + VARIABLE_NAME_DOMAIN + " a owl:Class.} UNION {?" + VARIABLE_NAME_DOMAIN
//				+ " a owl:NamedIndividual.}"
////				
//				+ " ?" + VARIABLE_NAME_DOMAIN + " " + property + " ?addInfo ."
////				
//				+ "FILTER(STRSTARTS(STR(?y), \"http:\"))"
////
//				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))" + "}"
////						
//+ " GROUP BY ?" + VARIABLE_NAME_DOMAIN);
//		
//	}
	private QueryResult extractAdditionalInfoForClasses(ApacheJenaDatabase db, final String property) {
		log.debug("_________Extract additional info for classes__________");

		return db.select(allPrefixes +
//				
				"select distinct ?" + VARIABLE_NAME_DOMAIN + " (STR(?addInfo) as ?" + VARIABLE_NAME_ADDITIONAL_INFO
				+ ") where { "
//				
				+ " ?" + VARIABLE_NAME_DOMAIN + " ?x ?y."
//
				+ " { ?" + VARIABLE_NAME_DOMAIN + " a owl:Class.} UNION {?" + VARIABLE_NAME_DOMAIN
				+ " a owl:NamedIndividual.}"
//				
				+ " ?" + VARIABLE_NAME_DOMAIN + " " + property + " ?addInfo ."
//				
				+ "FILTER(STRSTARTS(STR(?y), \"http:\"))"
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))" + "}"
//						
		);

	}

	private QueryResult extractAdditionalInfoForProperties(ApacheJenaDatabase db, final String property) {
		log.debug("_________Extract additional info for properties__________");
		return db.select(allPrefixes +
//				
				"select distinct ?" + VARIABLE_NAME_DOMAIN + " (STR(?addInfo) as ?" + VARIABLE_NAME_ADDITIONAL_INFO
				+ ") (STR(?relTyp) AS ?" + VARIABLE_NAME_RELATION_TYPE + ") where { "
//				
				+ " ?" + VARIABLE_NAME_DOMAIN + " ?x ?y."
//
				+ " { ?" + VARIABLE_NAME_DOMAIN + " a owl:ObjectProperty.} UNION {?" + VARIABLE_NAME_DOMAIN
				+ " a owl:DatatypeProperty.}"
//				
				+ " ?" + VARIABLE_NAME_DOMAIN + " " + property + " ?addInfo ."
//						
				+ "OPTIONAL {?" + VARIABLE_NAME_PROPERTY + " scio:relationType ?relTyp} ."

				+ "FILTER(STRSTARTS(STR(?y), \"http:\"))"
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))" + "}"
//						
		);
	}

	private QueryResult extractExactCardinalities(ApacheJenaDatabase db) {
		log.debug("_________Extract exact cardinality restrictions__________");
		return db.select(allPrefixes
//
				+ "select ?" + VARIABLE_NAME_DOMAIN + " (str(?card) as ?" + VARIABLE_NAME_CARDINALITY + ") ?"
				+ VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE + " where {"
//
				+ " ?" + VARIABLE_NAME_DOMAIN + " rdfs:subClassOf ?artificialSuper ."
//
				+ " ?artificialSuper owl:onProperty ?" + VARIABLE_NAME_PROPERTY + " ."
//
				+ " ?artificialSuper rdf:type owl:Restriction ."
//
				+ " ?artificialSuper owl:onClass ?" + VARIABLE_NAME_RANGE + "."
//
				+ " ?artificialSuper owl:qualifiedCardinality ?card." + "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN
				+ "), \"http:\"))" + "}");
	}

	private QueryResult extractMinCardinalities(ApacheJenaDatabase db) {
		log.debug("_________Extract min cardinality restrictions__________");
		return db.select(allPrefixes
//
				+ "select ?" + VARIABLE_NAME_DOMAIN + " (str(?card) as ?" + VARIABLE_NAME_CARDINALITY + ") ?"
				+ VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE + " where {"
//
				+ " ?" + VARIABLE_NAME_DOMAIN + " rdfs:subClassOf ?artificialSuper ."
//
				+ " ?artificialSuper owl:onProperty ?" + VARIABLE_NAME_PROPERTY + " ."
//
				+ " ?artificialSuper rdf:type owl:Restriction ."
//
				+ " ?artificialSuper owl:onClass ?" + VARIABLE_NAME_RANGE + " ."
//
				+ " ?artificialSuper owl:minQualifiedCardinality ?card."
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))" + "}");
	}

	private QueryResult extractRDFType(ApacheJenaDatabase db) {
		log.debug("_________Extract rdf type relations__________");
		return db.select(allPrefixes
//
				+ "select ?" + VARIABLE_NAME_SUBCLASS + " ?" + VARIABLE_NAME_SUPERCLASS + " where {"
//
				+ "?" + VARIABLE_NAME_SUBCLASS + " a ?" + VARIABLE_NAME_SUPERCLASS + " ."
//
				+ "?" + VARIABLE_NAME_SUBCLASS + " a owl:NamedIndividual ."
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_SUPERCLASS + "), \"http:\"))" + "}");
	}

	private QueryResult extractSubClassOf(ApacheJenaDatabase db) {
		log.debug("_________Extract standard subClass Relations__________");
		return db.select(allPrefixes
//
				+ "select ?" + VARIABLE_NAME_SUBCLASS + " ?" + VARIABLE_NAME_SUPERCLASS + " where {"
//
				+ " ?" + VARIABLE_NAME_SUBCLASS + " rdfs:subClassOf ?" + VARIABLE_NAME_SUPERCLASS + " ."
//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_SUPERCLASS + "), \"http://\"))" + "}");
	}

	private QueryResult extractDomainUnionOfDatatypeProperties(ApacheJenaDatabase db) {
		log.debug("_________Extract domain-unionOf datatypeProperties__________");
		return db.select(allPrefixes
//				
				+ "select ?" + VARIABLE_NAME_DOMAIN + " ?" + VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE
				+ " where {"
//
				+ "?" + VARIABLE_NAME_PROPERTY + " a owl:DatatypeProperty ."
//
				+ " ?" + VARIABLE_NAME_PROPERTY + " rdfs:domain ?l ."
//					
				+ " ?l owl:unionOf ?u ."
//
				+ " ?u rdf:rest*/rdf:first ?" + VARIABLE_NAME_DOMAIN + " ."
//
				+ "OPTIONAL{ ?" + VARIABLE_NAME_PROPERTY + " rdfs:range ?" + VARIABLE_NAME_RANGE + "} ."
//						
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))}");
	}

	private QueryResult extractDomainUnionOfObjectProperties(ApacheJenaDatabase db) {
		log.debug("_________Extract domain-unionOf objectProperties__________");
		return db.select(allPrefixes
//				
				+ "select ?" + VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE + " ?" + VARIABLE_NAME_DOMAIN
				+ "(STR(?relTyp) AS ?" + VARIABLE_NAME_RELATION_TYPE + ") "
//
				+ " where {"
//
				+ " ?" + VARIABLE_NAME_PROPERTY + " a owl:ObjectProperty ."
//
				+ " ?" + VARIABLE_NAME_PROPERTY + " rdfs:range ?" + VARIABLE_NAME_RANGE + " ."
//
				+ " ?" + VARIABLE_NAME_PROPERTY + " rdfs:domain ?l ."
//					
				+ " ?l owl:unionOf ?u ."
//
				+ " ?u rdf:rest*/rdf:first ?" + VARIABLE_NAME_DOMAIN + " ."
//
				+ "OPTIONAL {?" + VARIABLE_NAME_PROPERTY + " scio:relationType ?relTyp} ."
//						
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))" + "}");
	}

	private QueryResult extractDatatypeProperties(ApacheJenaDatabase db) {
		log.debug("_________Extract DatatypeProperties__________");
		return db.select(allPrefixes
				//
				+ "select  ?a ?b ?base ?" + VARIABLE_NAME_DT_RESTRICTION + " ?" + VARIABLE_NAME_DOMAIN + " ?"
				+ VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE
				//
				+ "(STR(?relTyp) AS ?" + VARIABLE_NAME_RELATION_TYPE + ") "
				//
				+ " where {"
				//
				+ "?" + VARIABLE_NAME_PROPERTY + " rdfs:domain ?" + VARIABLE_NAME_DOMAIN + ". "
				//
				+ "?" + VARIABLE_NAME_PROPERTY + " a owl:DatatypeProperty. "
				//
				+ "OPTIONAL {?" + VARIABLE_NAME_PROPERTY + " owl:onDatatype  ?" + VARIABLE_NAME_DT_RESTRICTION + ". "
				+ "} ."
//
				+ "OPTIONAL {?" + VARIABLE_NAME_PROPERTY + " scio:relationType ?relTyp} ."
				//
				+ "OPTIONAL{?" + VARIABLE_NAME_PROPERTY + " rdfs:range ?" + VARIABLE_NAME_RANGE + "}. "
				//
				+ "OPTIONAL{?" + VARIABLE_NAME_PROPERTY + " scio: ?" + VARIABLE_NAME_CARDINALITY + "}. "
				//
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))}");
	}

	private QueryResult extractObjectProperties(ApacheJenaDatabase db) {
		log.debug("__________Extract ObjectType Properties_________");
		return db.select(allPrefixes +
//				
				"select ?" + VARIABLE_NAME_DOMAIN + " ?" + VARIABLE_NAME_PROPERTY + " ?" + VARIABLE_NAME_RANGE
				+ " (STR(?relTyp) AS ?" + VARIABLE_NAME_RELATION_TYPE + ") " + " where {"
//
				+ "?" + VARIABLE_NAME_PROPERTY + " a owl:ObjectProperty. "
//			
				+ "?" + VARIABLE_NAME_PROPERTY + " rdfs:range ?" + VARIABLE_NAME_RANGE + ". "
//			
				+ "?" + VARIABLE_NAME_PROPERTY + " rdfs:domain ?" + VARIABLE_NAME_DOMAIN + ". "
//						
				+ "OPTIONAL {?" + VARIABLE_NAME_PROPERTY + " scio:relationType ?relTyp} ."
//			
				+ "FILTER(STRSTARTS(STR(?" + VARIABLE_NAME_DOMAIN + "), \"http:\"))}");
	}

}
