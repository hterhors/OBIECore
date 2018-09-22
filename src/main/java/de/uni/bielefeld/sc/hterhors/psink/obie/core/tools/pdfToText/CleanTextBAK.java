package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pdfToText;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanTextBAK {

	private static final String TITLE_IDENTIFYER = "#####";

	public static void main(String[] args) throws IOException {

		Map<String, String> mapping = new LinkedHashMap<>();
		List<String> mappingLines = Files.readAllLines(new File("res/pdfToText/encoding.csv").toPath());

		final String paperName = "A-combination-of-keratan-sulfate-digestion-and-rehabilitation-promotes-anatomical-plasticity-after-rat-spinal-cord-injury_2015_Neuroscience-Letters";
		// final String paperName =
		// "Antisense-vimentin-cDNA-combined-with-chondroitinase-ABC-promotes-axon-regeneration-and-functional-recovery-following-spinal-cord-injury-in-rats_2015_Neuroscience-Letters";

		File dir = new File("res/pdfToText/chondroitinase/pdfs/single/");

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		for (File documentFile : files) {

			File output = new File(
					"gen/pdfToText/chondroitinase/" + documentFile.getName().split("\\.", 1)[0] + ".txt");
			System.out.println("output: " + output);
			PrintStream ps = new PrintStream(output);

			List<String> doc = convertToPlainText(mapping, mappingLines, documentFile);

			doc.forEach(ps::println);
		}
	}

	private static List<String> convertToPlainText(Map<String, String> mapping, List<String> mappingLines,
			File documentFile) throws IOException {
		List<String> document = Files.readAllLines(documentFile.toPath());

		int fontSize = estimateFontSize(document);
		System.out.println("FontSize: " + fontSize);

		List<String> doc = extractTextFromDocument(document, fontSize);

		doc = mergeLines(doc);
		doc = sentenceExtraction(doc);
		doc = mergeTitles(doc);

		doc = cleanEncoding(mapping, mappingLines, doc);
		return doc;
	}

	private static List<String> mergeTitles(List<String> doc) {
		List<String> mergedDocumentText = new ArrayList<>();

		StringBuffer line = new StringBuffer();
		for (int i = 0; i < doc.size() - 1; i++) {

			if (doc.get(i).startsWith(TITLE_IDENTIFYER)) {
				line.append("\n");
				line.append(doc.get(i));
				i++;
				line.append(doc.get(i));
				line.delete(1, TITLE_IDENTIFYER.length() + 1);
				line.deleteCharAt(line.length() - 2);
				line.append("\n");
			} else {
				line.append(doc.get(i));
			}
			mergedDocumentText.add(line.toString());
			line.setLength(0);

		}

		return mergedDocumentText;
	}

	private static List<String> sentenceExtraction(List<String> doc) {
		List<String> sentences = new ArrayList<>();

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		StringBuffer documentContent = new StringBuffer();
		for (String line : doc) {
			documentContent.append(line);
			documentContent.append(" ");
		}
		iterator.setText(documentContent.toString());
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {

			final String sentence = documentContent.substring(start, end);

			if (sentence.contains(TITLE_IDENTIFYER)) {
				String[] subSentences = sentence.split(TITLE_IDENTIFYER);
				sentences.add(subSentences[0]);
				sentences.add(TITLE_IDENTIFYER + subSentences[1]);

			} else {
				sentences.add(sentence);
			}

		}

		return sentences;

	}

	private static List<String> mergeLines(List<String> doc) {
		List<String> mergedDocumentText = new ArrayList<>();

		StringBuffer line = new StringBuffer();
		for (int i = 0; i < doc.size() - 1; i++) {

			line.append(doc.get(i));
			if (doc.get(i).endsWith("-")) {
				line.deleteCharAt(line.length() - 1);
				i++;
				line.append(doc.get(i));
			}
			mergedDocumentText.add(line.toString());
			line.setLength(0);

		}

		return mergedDocumentText;
	}

	private static int estimateFontSize(List<String> document) {

		Map<Integer, Integer> fontCounter = new HashMap<>();
		Pattern p = Pattern.compile("font=\"([0-9]+)\">");

		for (String line : document) {
			Matcher matcher = p.matcher(line);

			while (matcher.find()) {
				final int fontSize = Integer.parseInt(matcher.group(1));
				fontCounter.put(fontSize, fontCounter.getOrDefault(fontSize, 0) + 1);
			}

		}
		return fontCounter.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
				.get().getKey();
	}

	private static List<String> extractTextFromDocument(final List<String> document, final int fontSize)
			throws IOException {
		List<String> filteredDocumentText = new ArrayList<>();

		Pattern hrefPattern = Pattern.compile("font=\"" + fontSize + "\">(.*)<a href=\".*\">(.*)</a>(.*)</text");
		Pattern boldTextPattern = Pattern.compile("<b>(.*)</b></text");
		Pattern subTitlePattern = Pattern.compile(">([0-9]+\\.([0-9]\\.?)*.*)(?<!\\.)</text>$");
		Pattern plainTextPattern = Pattern.compile("font=\"" + fontSize + "\">(.*)</text");

		Matcher m;
		for (String line : document) {
			m = hrefPattern.matcher(line);
			if (m.find()) {
				filteredDocumentText.add(m.group(1).trim() + m.group(2).trim() + m.group(3).trim());
			} else {
				m = boldTextPattern.matcher(line);
				if (m.find()) {
					filteredDocumentText
							.add(TITLE_IDENTIFYER + "Title: " + m.group(1).trim().toUpperCase() + ". Abrakadara");
				} else {
					m = subTitlePattern.matcher(line);
					if (m.find()) {
						filteredDocumentText
								.add(TITLE_IDENTIFYER + "Subtitle: " + m.group(1).trim().toUpperCase() + ".");
					} else {
						m = plainTextPattern.matcher(line);
						if (m.find()) {
							filteredDocumentText.add(m.group(1).trim());
						}
					}
				}
			}

		}

		return filteredDocumentText;
	}

	private static List<String> cleanEncoding(Map<String, String> mapping, List<String> mappingLines,
			List<String> doc) {
		Set<String> fails = new HashSet<>();

		Collections.sort(mappingLines);

		for (String mL : mappingLines) {
			if (mL.startsWith("#"))
				continue;

			mapping.put(fromByteArray(mL.split("\t")[0]), mL.split("\t")[1]);
		}

		for (int i = 0; i < doc.size(); i++) {
			for (Entry<String, String> map : mapping.entrySet()) {
				doc.set(i, doc.get(i).replace(map.getKey(), map.getValue()));
			}
		}

		Pattern p = Pattern.compile("[^\\x20-\\x7E]+");

		int c = 0;
		for (String line : doc) {

			c++;

			Matcher matcher = p.matcher(line);
			while (matcher.find()) {

				final String missingEncoding = Arrays.toString(matcher.group().getBytes());

				if (mapping.containsKey(missingEncoding))
					continue;

				if (mapping.containsValue(matcher.group()))
					continue;

				if (matcher.group().trim().isEmpty())
					continue;

				System.err.println("Could not encode:'" + matcher.group() + "' with byte array: "
						+ Arrays.toString(matcher.group().getBytes()) + " : in line: \n" + c + " : " + line);

				fails.add(Arrays.toString(matcher.group().getBytes()) + "\t");
			}
		}

		fails.forEach(System.out::println);

		return doc;
	}

	private static String fromByteArray(String byteArrayAsString) {
		final String[] data = byteArrayAsString.replaceAll("\\[|\\]", "").split(",");
		final Byte[] bytes = Arrays.stream(data).map(d -> Integer.valueOf(d.trim()).byteValue()).toArray(Byte[]::new);
		return new String(toPrimitives(bytes));
	}

	private static byte[] toPrimitives(Byte[] oBytes) {
		byte[] bytes = new byte[oBytes.length];

		for (int i = 0; i < oBytes.length; i++) {
			bytes[i] = oBytes[i];
		}

		return bytes;
	}
}
