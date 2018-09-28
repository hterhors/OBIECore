package de.uni.bielefeld.sc.hterhors.psink.obie.core.owlreader;

import java.util.List;
import java.util.Map;

/**
 * Query-Result. Simple data structure to handle the results of SPARQL-queries.
 */
public class QueryResult {

	public final List<Map<String, RDFObject>> queryData;

	public QueryResult(List<Map<String, RDFObject>> queryData) {
		this.queryData = queryData;
	}

}
