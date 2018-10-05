package de.hterhors.obie.core.tools.pdfToText;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import de.hterhors.obie.core.tools.pdfToText.jbleu.JBLEU;

public class TextComparison {

	final static public Map<String, String> pmc2name = new HashMap<>();

	public static void main(String[] args) throws IOException {

		pmc2name.put("3040708",
				"Reduction-in-antioxidant-enzyme-expression-and-sustained-inflammation-enhance-tissue-damage-in-the-subacute-phase-of-spinal-cord-contusive-injury_2011_Journal-of-Biomedical-Science");
		pmc2name.put("4590527",
				"Chondroitinase-gene-therapy-improves-upper-limb-function-following-cervical-contusion-injury_2015_Experimental-Neurology");
		pmc2name.put("4591338", "");
		pmc2name.put("4211738",
				"Examination-of-the-combined-effects-of-chondroitinase-ABC,-growth-factors-and-locomotor-training-following-compressive-spinal-cord-injury-on-neuroanatomical-plasticity-and-kinematics_2014_PLoS-ONE");
		pmc2name.put("4145889", "");
		pmc2name.put("3831297", "");
		pmc2name.put("3592852", "");
		pmc2name.put("3358255",
				"Chondroitinase-and-growth-factors-enhance-activation-and-Oligodendrocyte-differentiation-of-endogenous-neural-precursor-cells-after-spinal-cord-injury_2012_PLoS-ONE");
		pmc2name.put("3235548",
				"Lentiviral-vectors-express-chondroitinase-ABC-in-cortical-projections-and-promote-sprouting-of-injured-corticospinal-axons_2011_Journal-of-Neuroscience-Methods");

		// final String name =
		// "A-combination-of-keratan-sulfate-digestion-and-rehabilitation-promotes-anatomical-plasticity-after-rat-spinal-cord-injury_2015_Neuroscience-Letters";
		// String name = pmc2name.get("3040708");
		//
		// final String openpdf2textFile_fulltext = Files
		// .readAllLines(FileSystems.getDefault().getPath("res/chondroitinase/la-txt/"
		// + name + "_fullText.txt"))
		// .stream().map(t -> t.toString() + "\n").reduce("",
		// String::concat).trim().replaceAll("\\s+", " ");

		// name = pmc2name.get("4211738");
		// final String openpdf2textFile = Files
		// .readAllLines(FileSystems.getDefault().getPath("res/chondroitinase/la-txt/"
		// + name + ".txt")).stream()
		// .map(t -> t.toString() + "\n").reduce("",
		// String::concat).trim().replaceAll("\\s+", " ");
		//
		// final int maxNgramSize = 20;
		//
		// Map<Integer, List<String>> fullText_ngrams =
		// NGramReader.generateNgramsUpto(openpdf2textFile_fulltext,
		// maxNgramSize);
		// Map<Integer, List<String>> text_ngrams =
		// NGramReader.generateNgramsUpto(openpdf2textFile, maxNgramSize);
		//
		// for (int i = 1; i <= maxNgramSize; i++) {
		// System.out.println(
		// "i = " + i + ": " +
		// Distance.calculateCosineDistance(fullText_ngrams.get(i),
		// text_ngrams.get(i)));
		// }

		// evaluate(openpdf2textFile_fulltext, openpdf2textFile);

		startFullEvaluation();

	}

	private static void startFullEvaluation() throws IOException {
		final String[] pmcids = new String[] { "4591338", "4590527", "4211738", "4145889", "3831297", "3592852",
				"3358255", "3235548", "3040708" };

		for (String PMCID : pmcids) {
			final String name = pmc2name.get(PMCID);
			if (name.isEmpty()) {
				continue;
			}

			final String autoFile = Files
					.readAllLines(FileSystems.getDefault().getPath("gen/files/AutoDownload_PMC" + PMCID)).stream()
					.map(t -> t.toString() + "\n").reduce("", String::concat).trim().replaceAll("\\s+", " ");

			final String openAccessFile = Files
					.readAllLines(FileSystems.getDefault().getPath("gen/files/OpenAccess_PMC" + PMCID)).stream()
					.map(t -> t.toString() + "\n").reduce("", String::concat).trim().replaceAll("\\s+", " ");

			System.out.println("############################################");
			System.out.println("Current PMCID = " + PMCID);

			System.out.println("Evaluate with autofile...");
			evaluate(openAccessFile, autoFile);

			final String lapdfFulltext = Files
					.readAllLines(
							FileSystems.getDefault().getPath("res/chondroitinase/la-txt/" + name + "_fullText.txt"))
					.stream().map(t -> t.toString() + "\n").reduce("", String::concat).trim().replaceAll("\\s+", " ");

			final String lapdf = Files
					.readAllLines(FileSystems.getDefault().getPath("res/chondroitinase/la-txt/" + name + ".txt"))
					.stream().map(t -> t.toString() + "\n").reduce("", String::concat).trim().replaceAll("\\s+", " ");

			final String pdftotext = Files
					.readAllLines(FileSystems.getDefault().getPath("res/chondroitinase/pdftotext/" + name + ".txt"))
					.stream().map(t -> t.toString() + "\n").reduce("", String::concat).trim().replaceAll("\\s+", " ");

			System.out.println("############################################");
			System.out.println("Evaluate with lapdf-text...");
			evaluate(openAccessFile, lapdf);
			System.out.println("############################################");
			System.out.println("Evaluate with lapdf-fulltext...");
			evaluate(openAccessFile, lapdfFulltext);
			System.out.println();
			System.out.println("############################################");
			System.out.println("Evaluate with pdftotext...");
			evaluate(openAccessFile, pdftotext);
			System.out.println();
		}
	}

	private static void evaluate(final String goldFile, final String resultFile) {
		int missmatches = SentenceMatcher.countMissMatches(goldFile, resultFile);
		int numberOfSentences = SentenceMatcher.getSentencesFromText(goldFile).size();
		System.out.println("Sentences match G in R = " + (numberOfSentences - missmatches) + "/" + numberOfSentences
				+ " = " + (1 - ((double) missmatches / (double) numberOfSentences)));
		missmatches = SentenceMatcher.countMissMatches(resultFile, goldFile);
		numberOfSentences = SentenceMatcher.getSentencesFromText(resultFile).size();
		System.out.println("Sentences match R in G = " + (numberOfSentences - missmatches) + "/" + numberOfSentences
				+ " = " + (1 - ((double) missmatches / (double) numberOfSentences)));

		JBLEU bleu = new JBLEU();

		int[] result = new int[JBLEU.getSuffStatCount()];

		List<String> hyp = Lists.newArrayList(Splitter.on(Pattern.compile(" ")).split(resultFile));

		List<List<String>> refs = new ArrayList<List<String>>();
		refs.add(Lists.newArrayList(Splitter.on(Pattern.compile(" ")).split(goldFile)));

		// System.out.println("Levenshtein similarity = " +
		// WordMetric.levenshteinSimilarity(goldFile, resultFile));

		bleu.stats(hyp, refs, result);

		// System.err.println(Arrays.toString(result));
		System.out.println("Bleu score = " + bleu.score(result));
	}

}
