package de.hterhors.obie.core.evaluation.iob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hterhors.obie.core.evaluation.iob.IOBEvaluator.IOBEnum;

public class IOBReader {

	public static List<IOBDocument> readFile(final File iobFile) throws IOException {

		List<IOBDocument> docs = new ArrayList<>();
		List<String> lines = Files.readAllLines(iobFile.toPath());

		int docCounter = 0;
		Set<IOBAnnotation> annotations = new HashSet<>();

		IOBAnnotation annotation = null;

		for (String line : lines) {
			if (line.isEmpty()) {
				try {
					if (annotation != null)
						annotations.add(annotation);

					if (annotations.isEmpty())
						continue;

					annotation = null;
					String documentName = iobFile.getName() + docCounter;
					docs.add(new IOBDocument(annotations, documentName, docCounter));
					// System.out.println(documentName + ": " + annotations);
					annotations = new HashSet<>();
				} finally {
					docCounter++;
				}

			} else {

				String[] lineData = line.split("\t");

				final String text = lineData[0];
				final int onset = Integer.parseInt(lineData[1]);
				final int offset = Integer.parseInt(lineData[2]);
				final IOBEnum label = tolabel(lineData[3]);

				if (label == IOBEnum.B) {
					if (annotation != null)
						annotations.add(annotation);

					annotation = new IOBAnnotation(text, onset, offset, docCounter);
				} else if (label == IOBEnum.I) {
					annotation.textMention += " " + text;
					annotation.offset = offset;
				} else {

				}
			}

		}

		return docs;

	}

	private static IOBEnum tolabel(String string) {
		if (!string.startsWith("|")) {
			return null;
		}
		return IOBEnum.valueOf(string.substring(1, 2));
	}

}
