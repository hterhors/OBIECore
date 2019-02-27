package de.hterhors.obie.core.owlreader;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TrippleStore implements Serializable {

	/**
	 * 
	 */
	final private static long serialVersionUID = 1L;

	public static Logger log = LogManager.getFormatterLogger(TrippleStore.class.getSimpleName());

	public static void main(String[] args) {
		new TrippleStore();
	}

	/**
	 * 
	 * @param classFilter
	 * @param additionalInfoVariableNames
	 * @param additionalPrefixes          TODO: as List
	 * @param ontologyFile
	 */
	public TrippleStore() {
		TripleStoreDatabase db = new TripleStoreDatabase(
				new File("/home/hterhors/git/OBIECore/mappingbased_objects_en.ttl"));
		QueryResult r = db.select(
				"select distinct * where {<http://dbpedia.org/resource/Gowa_Regency> ?p <http://dbpedia.org/resource/Indonesia>} LIMIT 100");
		r.queryData.forEach(System.out::println);
		System.out.println("----");
		QueryResult r2 = db.select(
				"select distinct * where {<http://dbpedia.org/resource/Indonesia> ?p <http://dbpedia.org/resource/Gowa_Regency>} LIMIT 100");
		r2.queryData.forEach(System.out::println);
		System.out.println("----");
		QueryResult r3 = db.select(
				"select distinct * where {<http://dbpedia.org/resource/PSM_Makassar> ?p ?x. ?x ?p2 <http://dbpedia.org/resource/Indonesia>} LIMIT 100");
		r3.queryData.forEach(System.out::println);
		System.out.println("----");
	}

}
//Gowa_Regency Indonesia 

//PSM_Makassar