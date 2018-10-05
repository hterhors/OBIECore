package de.hterhors.obie.core.tools.ngram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Distance {

	public static double calculateCosineDistance(List<String> textVector, List<String> contextVector) {

		Set<String> words = new HashSet<String>();

		words.addAll(textVector);
		words.addAll(contextVector);

		HashMap<String, Double> v1 = convertStringVectorToDoubleVector(textVector, words,
				new HashMap<String, Double>());
		HashMap<String, Double> v2 = convertStringVectorToDoubleVector(contextVector, words,
				new HashMap<String, Double>());

		HashMap<String, Double> v1Clone = new HashMap<String, Double>(v1);
		HashMap<String, Double> v2Clone = new HashMap<String, Double>(v2);

		Set<String> filterKeySet = new HashSet<String>();

		for (String key : v2Clone.keySet()) {
			if (v1Clone.get(key) != 0 && v2Clone.get(key) != 0)
				filterKeySet.add(key);
		}
		v1Clone.keySet().retainAll(filterKeySet);
		v2Clone.keySet().retainAll(filterKeySet);
		double numerator = 0;
		double d1 = 0;
		double d2 = 0;
		for (String word : words) {
			numerator += v1.get(word) * v2.get(word);
			d1 += Math.pow(v1.get(word), 2);
			d2 += Math.pow(v2.get(word), 2);
		}
		final double denominator = Math.sqrt(d1) * Math.sqrt(d2);
		final double result = numerator / denominator;
		return result;
	}

	public static double calculateCosineDistance_weighted(List<String> textVector, List<String> contextVector,
			Set<String> englishStopWords, Set<String> goWords, Map<String, Double> weights) {

		Set<String> words = new HashSet<String>();

		textVector.removeAll(englishStopWords);
		contextVector.removeAll(englishStopWords);

		if (!goWords.isEmpty()) {
			textVector.retainAll(goWords);
			contextVector.retainAll(goWords);

		}
		words.addAll(textVector);
		words.addAll(contextVector);

		HashMap<String, Double> v1 = convertStringVectorToDoubleVector_weighted(textVector, words, weights);
		HashMap<String, Double> v2 = convertStringVectorToDoubleVector_weighted(contextVector, words, weights);

		HashMap<String, Double> v1Clone = new HashMap<String, Double>(v1);
		HashMap<String, Double> v2Clone = new HashMap<String, Double>(v2);

		Set<String> filterKeySet = new HashSet<String>();

		for (String key : v2Clone.keySet()) {
			if (v1Clone.get(key) != 0 && v2Clone.get(key) != 0)
				filterKeySet.add(key);
		}

		final double length = filterKeySet.size();

		v1Clone.keySet().retainAll(filterKeySet);
		v2Clone.keySet().retainAll(filterKeySet);

		// System.out.println(filterKeySet.size());
		// System.out.println(v1Clone);
		// System.out.println(v2Clone);
		double numerator = 0;
		double d1 = 0;
		double d2 = 0;

		for (String word : words) {
			numerator += v1.get(word) * v2.get(word);
			d1 += Math.pow(v1.get(word), 2);
			d2 += Math.pow(v2.get(word), 2);
		}
		// System.out.println(numerator);
		// System.out.println();

		// final double denominator = Math.sqrt(d1) * Math.sqrt(d2);
		final double result = numerator * length;
		// / denominator;
		return result;
	}

	public static List<String> toVector(String surfaceForm) {

		String[] array = surfaceForm.split(" ");
		List<String> out = new LinkedList<String>();
		for (int i = 0; i < array.length; i++) {
			if (!array[i].trim().isEmpty())
				out.add(isUpperCase(array[i]) ? array[i] : array[i].toLowerCase());
		}

		return out;
	}

	public static boolean isUpperCase(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLowerCase(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static HashMap<String, Double> convertStringVectorToDoubleVector(List<String> vector, Set<String> words,
			final Map<String, Double> weights) {

		HashMap<String, Double> wordCounts = new HashMap<String, Double>();

		for (String string : vector) {
			wordCounts.put(string, wordCounts.getOrDefault(string, 0d) + 1);
		}

		HashMap<String, Double> outVec = new HashMap<String, Double>();

		for (String word : words) {
			outVec.put(word, wordCounts.getOrDefault(word, 0d) * weights.getOrDefault(word, 1d));
		}

		return outVec;
	}

	private static HashMap<String, Double> convertStringVectorToDoubleVector_weighted(List<String> vector,
			Set<String> words, final Map<String, Double> weights) {

		HashMap<String, Double> wordCounts = new HashMap<String, Double>();

		for (String string : vector) {
			wordCounts.put(string, wordCounts.getOrDefault(string, 0d) + 1);
		}

		HashMap<String, Double> outVec = new HashMap<String, Double>();

		for (String word : words) {
			outVec.put(word, Math.sqrt(wordCounts.getOrDefault(word, 0d)) * weights.getOrDefault(word, 1d));
		}

		return outVec;
	}
}
