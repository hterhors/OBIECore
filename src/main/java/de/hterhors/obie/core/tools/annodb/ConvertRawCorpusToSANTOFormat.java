package de.hterhors.obie.core.tools.annodb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hterhors.obie.core.ontology.annotations.OntologyModelContent;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.projects.AbstractProjectEnvironment;
import de.hterhors.obie.core.tokenizer.RegExTokenizer;
import de.hterhors.obie.core.tokenizer.SentenceSplitter;
import de.hterhors.obie.core.tokenizer.Token;
import de.hterhors.obie.core.tokenizer.Tokenization;
import de.hterhors.obie.core.tools.corpus.OBIECorpus;
import de.hterhors.obie.core.tools.corpus.OBIECorpus.Instance;

/**
 * Loads the raw corpus and transforms it into an SANTO format for file writing.
 * 
 * @author hterhors
 *
 */
public class ConvertRawCorpusToSANTOFormat {

	public ConvertRawCorpusToSANTOFormat(final File parentCorpusDirectory, AbstractProjectEnvironment environment) {

		OBIECorpus corpus = OBIECorpus.readRawCorpusData(environment.getRawCorpusFile());

		for (Instance document : corpus.getInstances().values()) {

			File annotationOutputFile = new File(parentCorpusDirectory, document.name + ".annodb");
			File csvOutputFile = new File(parentCorpusDirectory, document.name + ".csv");

			List<AnnoDBAnnotation> annoDBAnnotations = convertAnnotations(document);

			try {
				writeCSVFile(csvOutputFile, document.content);
				writeAnnoDBFile(annotationOutputFile, annoDBAnnotations);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}

	private List<AnnoDBAnnotation> convertAnnotations(Instance document) {
		List<AnnoDBAnnotation> annoDBAnnotations = new ArrayList<>();
		for (List<IOBIEThing> annotations : document.annotations.values()) {
			for (IOBIEThing entityAnnotation : annotations) {
				addRecursiveForAllProperties(document, annoDBAnnotations, entityAnnotation);
			}
		}
		return annoDBAnnotations;
	}

	private void writeCSVFile(File csvOutputFile, final String documentContent) throws IOException {

		List<String> sentences = SentenceSplitter.extractSentences(documentContent);
		List<Tokenization> tokens = RegExTokenizer.tokenize(sentences);
		PrintStream ps = new PrintStream(csvOutputFile);

		for (Tokenization tokenization : tokens) {
			int tokenIndex = 1;
			ps.println("#" + tokenization.originalSentence);
			for (Token token : tokenization.tokens) {
				ps.println(token.getSentenceIndex() + 1 + "\t" + tokenIndex + "\t" + token.getFromCharPosition() + "\t"
						+ token.getToCharPosition() + "\t" + token.getText());
				tokenIndex++;
			}

		}
		ps.close();
	}

	public void writeAnnoDBFile(final File annotationOutputFile, List<AnnoDBAnnotation> annoDBAnnotations)
			throws IOException {

		PrintStream annotationPrintStream = new PrintStream(annotationOutputFile);

		for (AnnoDBAnnotation annotation : annoDBAnnotations) {

			StringBuffer line = new StringBuffer();

			line.append("T" + annotation.annotationID).append("\t");
			line.append(annotation.classType).append("\t");
			line.append(annotation.onset).append("\t");
			line.append(annotation.offset).append("\t");
			line.append("\"" + annotation.tokenValue + "\"").append("\t");
			line.append("\"" + annotation.additionalInfo + "\"");

			annotationPrintStream.println(line);

		}

		annotationPrintStream.close();

	}

	@SuppressWarnings("unchecked")
	private void addRecursiveForAllProperties(Instance document, List<AnnoDBAnnotation> annoDBAnnotations,
			IOBIEThing entityAnnotation) {

		if (entityAnnotation == null)
			return;

		String classType = entityAnnotation.getClass().getSimpleName();
		int onset = entityAnnotation.getCharacterOnset();
		int offset = entityAnnotation.getCharacterOffset();
		String tokenValue = entityAnnotation.getTextMention();

		/**
		 * TODO: dirty Hack! make onset and offset int values instead of long!
		 */
		String word = document.content.substring(Integer.valueOf("" + onset), Integer.valueOf("" + offset));

		if (!word.equals(tokenValue)) {
			throw new IllegalStateException("Annotation onset does not fit documents content.");
		}

		annoDBAnnotations.add(
				new AnnoDBAnnotation(annoDBAnnotations.size() + 1, classType, onset, offset, tokenValue, null, null));

		Arrays.stream(entityAnnotation.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(OntologyModelContent.class)).forEach(field -> {
					field.setAccessible(true);
					if (field.isAnnotationPresent(RelationTypeCollection.class)) {

						try {
							for (IOBIEThing element : (List<IOBIEThing>) field.get(entityAnnotation)) {
								addRecursiveForAllProperties(document, annoDBAnnotations, element);
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							System.exit(1);
						}
					} else {
						try {
							addRecursiveForAllProperties(document, annoDBAnnotations,
									(IOBIEThing) field.get(entityAnnotation));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							System.exit(1);
						}
					}

				});

	}

}
