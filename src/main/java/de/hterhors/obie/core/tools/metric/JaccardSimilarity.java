package de.hterhors.obie.core.tools.metric;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity {

	public static void main(String[] args) {
		Set<String> s1 = getBagOfNGram("breast  cancer", 3);
		Set<String> s2 = getBagOfNGram("breast and/or cancer", 3);

		System.out.println(s1.size());
		System.out.println(s2.size());
		System.out.println(jaccardSimilarity(s1, s2));
	}

	public static double jaccardSimilarity(Set<String> s1, Set<String> s2) {

		// double intersection =
		// s1.stream().filter(s2::contains).collect(Collectors.toList()).size();

		double intersection = 0;
		for (String ngram : s1) {
			if (s2.contains(ngram))
				intersection++;
		}

		return intersection / (double) (s1.size() + s2.size() - intersection);

	}

	public static double jaccardSimilarity(BitSet b1, BitSet b2) {
		BitSet bs = new BitSet(b1.size());
		bs.or(b1);
		bs.and(b2);
		double intersection = bs.cardinality();
		bs.clear();
		bs.or(b1);
		bs.or(b2);
		double union = bs.cardinality();
		return intersection / union;
	}

	// public static double jaccardSimilarity(BitSet b1, BitSet b2) {
	// BitSet bs = (BitSet) b1.clone();
	// bs.and(b2);
	// double intersection = bs.cardinality();
	// bs.clear();
	// bs = (BitSet) b1.clone();
	// bs.or(b2);
	// double union = bs.cardinality();
	// return intersection / union;
	// }

	public static Set<String> getBagOfNGram(final String text, final int charachterNGramSize) {

		Set<String> bongam = new HashSet<String>();

		for (int i = 0; i < text.length() - (charachterNGramSize - 1); i++) {
			String nGramS = text.substring(i, i + charachterNGramSize);
			bongam.add(nGramS);
		}

		return bongam;
	}

}
