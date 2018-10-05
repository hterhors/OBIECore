package de.hterhors.obie.core.owlreader;

import de.hterhors.obie.core.owlreader.container.OntologyClass;

public interface IClassFilter {
	public boolean matchesCondition(OntologyClass ocd);
}
