package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;

public interface IClassFilter {
	public boolean matchesCondition(OntologyClass ocd);
}
