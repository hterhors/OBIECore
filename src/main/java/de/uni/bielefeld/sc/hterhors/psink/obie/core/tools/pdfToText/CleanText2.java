package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pdfToText;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
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

public class CleanText2 {

	public static void main(String[] args) throws IOException {

		Map<String, String> mapping = new LinkedHashMap<>();
		List<String> mappingLines = Files.readAllLines(new File("res/pdfToText/encoding.csv").toPath());

		// String dirName = "res/pdfToText/chondroitinase/pdftotext/";
		String dirName = "res/pdfToText/knowtator/";
		File dir = new File(dirName);

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		});

		for (File documentFile : files) {
			System.out.println("documentFile = " + documentFile);
			System.out.println("documentFile = " + documentFile.getName().split("\\.", 2)[0]);
			File output = new File("gen/pdfToText/knowtator/" + documentFile.getName().split("\\.", 2)[0] + ".txt");
			System.out.println("output: " + output);
			PrintStream ps = new PrintStream(output);

			List<String> doc = convertToPlainText(mapping, mappingLines, documentFile);

			doc.forEach(ps::println);
			ps.close();
		}
	}

	private static List<String> convertToPlainText(Map<String, String> mapping, List<String> mappingLines,
			File documentFile) throws IOException {
		List<String> document = Files.readAllLines(documentFile.toPath());

		List<String> encodedText = cleanEncoding(mappingLines, document);
		return encodedText;
	}

	private static List<String> cleanEncoding(List<String> mappingLines, List<String> doc) {
		Set<String> fails = new HashSet<>();
		Map<String, String> mapping = new HashMap<>();
		Collections.sort(mappingLines);

		for (String mL : mappingLines) {
			if (mL.startsWith("#"))
				continue;

			mapping.put(fromByteArray(mL.split("\t")[0]), mL.split("\t")[1]);
		}

		for (int i = 0; i < doc.size(); i++) {
			for (Entry<String, String> map : mapping.entrySet()) {
				String pre = doc.get(i);
				doc.set(i, doc.get(i).replace(map.getKey(), map.getValue()));
				String post = doc.get(i);

				if (!pre.equals(post)) {
					// System.err.println("Apply encoding: \n" + pre + "\n" +
					// post);
				}
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
