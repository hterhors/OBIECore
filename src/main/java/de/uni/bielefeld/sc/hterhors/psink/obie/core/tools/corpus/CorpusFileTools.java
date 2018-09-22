package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.corpus;

import java.io.File;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.ImplementationClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

public class CorpusFileTools {

	private final static String binarayCorpusNameEnding = ".bin";

	public static File buildRawCorpusFile(File corpusFileDirectory, final String corpusNamePrefix,
			Set<Class<? extends IOBIEThing>> rootClassTypes, final long version) {
		final String rootClassTypeNames = rootClassTypes.stream()
				.map(c -> c.getAnnotation(ImplementationClass.class).get().getSimpleName()).reduce("", String::concat)
				.toLowerCase();
		final String ontologyVersion = String.valueOf(version);
		return new File(corpusFileDirectory,
				corpusNamePrefix + rootClassTypeNames + "_v" + ontologyVersion + binarayCorpusNameEnding);
	}

	public static File buildAnnotatedBigramCorpusFile(File corpusFileDirectory, final String corpusNamePrefix,
			Set<Class<? extends IOBIEThing>> rootClassTypes, final long version) {
		final String rootClassTypeNames = rootClassTypes.stream()
				.map(c -> c.getAnnotation(ImplementationClass.class).get().getSimpleName()).reduce("", String::concat)
				.toLowerCase();
		final String ontologyVersion = String.valueOf(version);
		return new File(corpusFileDirectory,
				corpusNamePrefix + "_" + rootClassTypeNames + "_v" + ontologyVersion + binarayCorpusNameEnding);
	}

}
