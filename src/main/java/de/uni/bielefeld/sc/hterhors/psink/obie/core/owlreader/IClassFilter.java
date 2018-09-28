package de.uni.bielefeld.sc.hterhors.psink.obie.core.owlreader;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.owlreader.container.OntologyClass;

public interface IClassFilter {
	public boolean matchesCondition(OntologyClass ocd);
}
