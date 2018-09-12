package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.ngram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class NGramReader {

	/**
	 * 
	 * @param sentence
	 *            should has at least one string
	 * @param maxGramSize
	 *            should be 1 at least
	 * @return set of continuous word n-grams up to maxGramSize from the
	 *         sentence
	 */
	public static Map<Integer, List<String>> generateNgramsUpto(String str, int maxGramSize) {

		List<String> sentence = Arrays.asList(str.split(" "));

		Map<Integer, List<String>> ngrams = new HashMap<Integer, List<String>>();
		int ngramSize = 0;
		StringBuilder sb = null;

		// sentence becomes ngrams
		for (ListIterator<String> it = sentence.listIterator(); it.hasNext();) {
			String word = (String) it.next();

			// 1- add the word itself
			sb = new StringBuilder(word);
			ngramSize = 1;
			ngrams.putIfAbsent(ngramSize, new ArrayList<>());
			ngrams.get(ngramSize).add(sb.toString());
			it.previous();

			// 2- insert prevs of the word and add those too
			while (it.hasPrevious() && ngramSize < maxGramSize) {
				sb.insert(0, ' ');
				sb.insert(0, it.previous());
				ngramSize++;
				ngrams.putIfAbsent(ngramSize, new ArrayList<>());
				ngrams.get(ngramSize).add(sb.toString());
			}

			// go back to initial position
			while (ngramSize > 0) {
				ngramSize--;
				it.next();
			}
		}
		return ngrams;
	}

}
