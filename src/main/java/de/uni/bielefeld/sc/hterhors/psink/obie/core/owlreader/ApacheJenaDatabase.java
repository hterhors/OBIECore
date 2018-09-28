package de.uni.bielefeld.sc.hterhors.psink.obie.core.owlreader;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApacheJenaDatabase {
	private final Model model;

	public static Logger log = LogManager.getFormatterLogger(OWLReader.class.getSimpleName());

	public static Model getOntologyModel(File ontologyFile) {
		Model ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null).getBaseModel();

		try {
			InputStream in = FileManager.get().open(ontologyFile.getAbsolutePath());
			try {
				ontologyModel.read(in, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JenaException je) {
			System.err.println("ERROR" + je.getMessage());
			je.printStackTrace();
			System.exit(0);
		}
		return ontologyModel;
	}

	public ApacheJenaDatabase(File ontologyFile) {
		model = getOntologyModel(ontologyFile);
	}

	public QueryResult select(String queryString) {
		try {
			return select(model, queryString);
		} catch (Exception e) {
			log.warn(queryString);
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