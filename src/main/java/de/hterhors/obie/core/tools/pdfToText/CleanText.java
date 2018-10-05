package de.hterhors.obie.core.tools.pdfToText;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanText {

	public static void main(String[] args) throws IOException {

		Map<String, String> mapping = new LinkedHashMap<>();
		List<String> mappingLines = Files.readAllLines(new File("res/pdfToText/encoding.csv").toPath());

		File dir = new File("res/pdfToText/chondroitinase/pdfs/single/");
		String textFileName = "res/pdfToText/chondroitinase/pdftotext/";

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		for (File documentFile : files) {
			System.out.println("documentFile = " + documentFile);
			System.out.println("documentFile = " + documentFile.getName().split("\\.", 2)[0]);
			File output = new File(
					"gen/pdfToText/chondroitinase/" + documentFile.getName().split("\\.", 2)[0] + ".txt");
			System.out.println("output: " + output);
			PrintStream ps = new PrintStream(output);

			String txt = convertToPlainText(mapping, mappingLines, documentFile);

			File textFile = new File(textFileName + documentFile.getName().split("\\.", 2)[0] + ".txt");

			List<String> doc = filterFromPDFToText(txt, textFile);

			doc.forEach(ps::println);
			ps.close();
		}
	}

	private static List<String> filterFromPDFToText(String txt, File textFile) throws IOException {

		List<String> document = Files.readAllLines(textFile.toPath());
		List<String> filteredDocument = new ArrayList<>();

		for (String line : document) {

			if (txt.contains(line.replaceAll("\\s+", ""))) {
				System.out.println(line);
				filteredDocument.add(line);
			}
		}

		return filteredDocument;
	}

	private static String convertToPlainText(Map<String, String> mapping, List<String> mappingLines, File documentFile)
			throws IOException {
		List<String> document = Files.readAllLines(documentFile.toPath());

		int fontSize = estimateFontSize(document);
		System.out.println("FontSize: " + fontSize);

		String doc = extractTextFromXMLDocument(document, fontSize);
		return doc;
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

	private static String extractTextFromXMLDocument(final List<String> document, final int fontSize)
			throws IOException {

		StringBuffer text = new StringBuffer();

		Pattern hrefPattern = Pattern.compile("font=\"" + fontSize + "\">(.*)<a href=\".*\">(.*)</a>(.*)</text");
		Pattern boldTextPattern = Pattern.compile("<(i|b)>(.*)</(i|b)></text");
		Pattern subTitlePattern = Pattern.compile(">([0-9]+\\.([0-9]\\.?)*.*)(?<!\\.)</text>$");
		Pattern plainTextPattern = Pattern.compile("font=\"" + fontSize + "\">(.*)</text");

		Matcher m;
		for (String line : document) {
			m = hrefPattern.matcher(line);
			if (m.find()) {
				text.append(m.group(1).trim() + m.group(2).trim() + m.group(3).trim());
			} else {
				m = boldTextPattern.matcher(line);
				if (m.find()) {
					text.append(m.group(1).trim().toUpperCase());
				} else {
					m = subTitlePattern.matcher(line);
					if (m.find()) {
						text.append(m.group(1).trim().toUpperCase());
					} else {
						m = plainTextPattern.matcher(line);
						if (m.find()) {
							text.append(m.group(1).trim());
						}
					}
				}
			}

		}

		return text.toString().replaceAll("\\s+", "");
	}

	private static List<Content> cleanEncoding(Map<String, String> mapping, List<String> mappingLines,
			List<Content> doc) {
		Set<String> fails = new HashSet<>();

		Collections.sort(mappingLines);

		for (String mL : mappingLines) {
			if (mL.startsWith("#"))
				continue;

			mapping.put(fromByteArray(mL.split("\t")[0]), mL.split("\t")[1]);
		}

		for (int i = 0; i < doc.size(); i++) {
			for (Entry<String, String> map : mapping.entrySet()) {
				doc.set(i, new Content(doc.get(i).content.replace(map.getKey(), map.getValue())));
			}
		}

		Pattern p = Pattern.compile("[^\\x20-\\x7E]+");

		int c = 0;
		for (Content line : doc) {

			c++;

			Matcher matcher = p.matcher(line.content);
			while (matcher.find()) {

				final String missingEncoding = Arrays.toString(matcher.group().getBytes());

				if (mapping.containsKey(missingEncoding))
					continue;

				if (mapping.containsValue(matcher.group()))
					continue;

				if (matcher.group().trim().isEmpty())
					continue;

				System.err.println("Could not encode:'" + matcher.group() + "' with byte array: "
						+ Arrays.toString(matcher.group().getBytes()) + " : in line: \n" + c + " : " + line.content);

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
