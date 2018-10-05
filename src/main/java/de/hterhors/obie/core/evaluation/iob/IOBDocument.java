package de.hterhors.obie.core.evaluation.iob;

import java.util.Set;

public class IOBDocument {

	public Set<IOBAnnotation> annotations;
	public String documentName;
	public int index;

	public IOBDocument(Set<IOBAnnotation> annotations, String documentName, int index) {
		this.annotations = annotations;
		this.documentName = documentName;
		this.index = index;
	}

}
