package de.hterhors.obie.core.owlreader;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.ReflectionUtils;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.OntologyModelContent;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;

public class TripleStoreDatabase {
	private final Model model;

	public static Logger log = LogManager.getFormatterLogger(OWLReader.class.getSimpleName());

	public TripleStoreDatabase(File kbFile) {
		model = ModelFactory.createDefaultModel();
		try {
//			model.read(new FileInputStream(new File("/home/hterhors/GenericDBPedia/mappingbased_objects_en.ttl")), null, "TTL");
			model.read(new FileInputStream(kbFile), null, "TTL");
		} catch (Exception je) {
			je.printStackTrace();
		}
	}

	public TripleStoreDatabase() {
		model = ModelFactory.createDefaultModel();
		try {
//			model.read(new FileInputStream(new File("/home/hterhors/GenericDBPedia/mappingbased_objects_en.ttl")), null, "TTL");
//			model.read(new FileInputStream(new File("/home/hterhors/git/OBIECore/mappingbased_objects_en.ttl")), null,
//					"TTL");
		} catch (Exception je) {
			je.printStackTrace();
		}
	}

	public void add(IOBIEThing thing, Field slot, IOBIEThing filler) {
		try {

			Resource subject = model.createResource(thing.getIndividual().nameSpace + thing.getIndividual().name);
			Property property = model.createProperty(slot.getAnnotation(OntologyModelContent.class).ontologyName());
			RDFNode object;

			if (ReflectionUtils.isAnnotationPresent(slot, DatatypeProperty.class)) {
				object = model.createLiteral(filler.getTextMention());
			} else {
				object = model.createResource(filler.getIndividual().nameSpace + filler.getIndividual().name);
			}

			model.add(subject, property, object);
		} catch (Exception e) {
			throw e;
		}
	}

	public QueryResult select(String queryString) {
		try {
			QueryResult r = select(model, queryString);
			
//			log.info("Query\t"+queryString+"\t"+r.queryData.isEmpty());
			return r;
		} catch (Exception e) {
			log.warn(queryString);
			throw e;
		}
	}

	public void delete(IOBIEThing thing, Field slot, IOBIEThing filler) {
		try {

			Resource subject = model.createResource(thing.getIndividual().getURI());
			Property property = model.createProperty(slot.getAnnotation(OntologyModelContent.class).ontologyName());
			
			RDFNode object;

			if (ReflectionUtils.isAnnotationPresent(slot, DatatypeProperty.class)) {
				object = model.createLiteral(filler.getTextMention());
			} else {
				object = model.createResource(filler.getIndividual().getURI());
			}

			model.removeAll(subject, property, object);
		} catch (Exception e) {
			throw e;
		}
	}

	private static QueryResult select(Model model, String queryString) {
		Query query = QueryFactory.create(queryString);
		log.debug("Query: " + query);

		List<Map<String, RDFObject>> data = new LinkedList<>();

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			final Map<String, RDFObject> queryResult = new HashMap<>();
			final QuerySolution solution = results.nextSolution();

			for (Iterator<String> iterator = solution.varNames(); iterator.hasNext();) {
				final String variableName = iterator.next();
				try {
					queryResult.put(variableName,
							new RDFObject(variableName, solution.getResource(variableName).toString(), true));
				} catch (ClassCastException e) {
					queryResult.put(variableName,
							new RDFObject(variableName, solution.getLiteral(variableName).toString(), false));
				}

			}

			data.add(queryResult);
		}
		return new QueryResult(data);
	}

}