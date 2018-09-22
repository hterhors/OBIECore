package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pdfToText;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SentenceMatcher {

	public static int countMissMatches(final String goldText, final String resultText) {

		AtomicInteger missmatchCounter = new AtomicInteger(0);

		List<String> goldSentences = getSentencesFromText(goldText);
		Set<String> resultSentences = new LinkedHashSet<>(getSentencesFromText(resultText));

		AtomicInteger maxNumOfConsSen = new AtomicInteger(0);
		AtomicInteger numOfConsSen = new AtomicInteger(0);
		goldSentences.stream().forEach(goldSentence -> {
			// for (String goldSentence : goldSentences) {
			// boolean contains = resultSentences.contains(goldSentence);
			boolean contains, match = contains = resultText.contains(goldSentence);

			if (!contains) {
				for (String resultSentence : resultSentences) {
					if (WordMetric.levenshteinSimilarity(goldSentence, resultSentence) >= 0.9) {
						// System.out.println(goldSentence);
						// System.out.println("==========");
						// System.out.println(resultSentence);
						// System.out.println("***********");
						// System.out.println("Similarity score = " + sim);
						// System.out.println("*********************");
						match = true;
						break;
					}
				}
				if (!match) {
					// System.out.println();
					// System.out.println("??????????????????????????????????????");
					// System.out.println("Could not find:");
					// System.out.println(goldSentence);
					// System.out.println("??????????????????????????????????????");
					// System.out.println();
					missmatchCounter.incrementAndGet();
				}
			}

			if (match) {
				numOfConsSen.incrementAndGet();
				// System.out.println("+++" + goldSentence);
			} else {
				// System.out.println("---" + goldSentence);
				maxNumOfConsSen.set(Math.max(maxNumOfConsSen.get(), numOfConsSen.get()));
				System.out.print(numOfConsSen + ",");
				numOfConsSen.set(0);
			}

		});
		// System.out.println(maxNumOfConsSen);
		System.out.println();
		return missmatchCounter.get();
	}

	public static List<String> getSentencesFromText(final String text) {
		List<String> sentences = new ArrayList<String>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			sentences.add(text.substring(start, end).trim());
		}

		return sentences;
	}
}
