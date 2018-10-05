package de.hterhors.obie.core.tokenizer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SentenceSplitter {
	public static List<String> extractSentences(String document) {

		document = document.replaceAll("\n", " ");

		BreakIterator sentenceBoundary = BreakIterator.getSentenceInstance(Locale.ENGLISH);
		sentenceBoundary.setText(document);
		List<Integer> sentenceOffsets = new ArrayList<>();
		List<String> sentences = new ArrayList<>();
		int start = sentenceBoundary.first();
		for (int end = sentenceBoundary.next(); end != BreakIterator.DONE; start = end, end = sentenceBoundary.next()) {
			sentenceOffsets.add(end);
			sentences.add(document.substring(start, end));
		}
		return sentences;

	}

}
