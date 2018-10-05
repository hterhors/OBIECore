package de.hterhors.obie.core.tools.pdfToText.pdf2htmlex;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Pdf2HtmlExExtractionInteractive {

	static final Pattern fontSizePattern = Pattern.compile("(fs[0123456789abcdef])");
	static final Pattern fontFamilyPattern = Pattern.compile("(ff[0123456789abcdef])");
	static final Pattern fontColorPattern = Pattern.compile("(fc[0123456789abcdef])");
	static final Pattern spanTextPattern = Pattern.compile("<span class=\"(?<class>.*?)\">(?<text>.*?)<");

	final static private String BAD_CHAR = "[^\\x20-\\x7E]+";

	/**
	 * font name to base64
	 * 
	 * ff0 = H4O2gKHjhFfjO489250//hfjgkdoj5638badlg ....
	 */
	static final private Map<String, String> fontToBase64 = new HashMap<>();

	/**
	 * base64 to char mapping;
	 */
	static final private Map<String, Map<String, String>> base64ToCharMapping = new HashMap<>();
	static private String textFontFamily;

	public static void main(String[] args) throws IOException, InterruptedException {

		final String file = "pdf2htmlex/res/html/Okutan et al.html";

		final String mappingFileName = "pdf2htmlex/res/base64Mapping";

		List<String> mappingList = Files.readAllLines(new File(mappingFileName).toPath());

		mappingListToMap(mappingList);

		Document doc = Jsoup.parse(new File(file), "utf-8");

		extractFonts(fontToBase64, doc.html());

		for (String thisBase64 : fontToBase64.values()) {

			if (!base64ToCharMapping.containsKey(thisBase64)) {
				base64ToCharMapping.put(thisBase64, new HashMap<>());
				System.out.println("Add new font to mapping file... " + thisBase64.substring(0, 50) + "[...]");
			} else {
				System.out.println("Found font in mapping file... " + thisBase64.substring(0, 50) + "[...]");
			}

		}

		textFontFamily = getTextSpecs(fontFamilyPattern, doc);
		String textFontSize = getTextSpecs(fontSizePattern, doc);
		String textFontColor = getTextSpecs(fontColorPattern, doc);

		String currentFontFamily = null;
		String currentFontSize = null;
		String currentFontColor = null;

		String lastNonTextFontSize = null;
		String lastTextFontSize = null;

		final StringBuffer documentText = new StringBuffer();

		List<Element> elements = doc.body().getAllElements();

		List<String> documentSentences = new ArrayList<>();

		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);

			Matcher fontFamily = fontFamilyPattern.matcher(element.attr("class"));

			if (fontFamily.find()) {
				currentFontFamily = fontFamily.group();
			}
			String text = listToString(cleanedTextChunks(currentFontFamily, element));
			documentSentences.add(text);
			if (text.trim().isEmpty())
				continue;

			System.out.println(text);

		}

		System.out.println("######DONE######");

		Thread.sleep(1000);

		for (int i = 0; i < elements.size(); i++) {

			Element element = elements.get(i);

			Matcher fontFamily = fontFamilyPattern.matcher(element.attr("class"));
			Matcher fontSize = fontSizePattern.matcher(element.attr("class"));
			Matcher fontColor = fontColorPattern.matcher(element.attr("class"));

			if (fontFamily.find()) {
				currentFontFamily = fontFamily.group();
			}
			if (fontSize.find()) {
				currentFontSize = fontSize.group();
			}
			if (fontColor.find()) {
				currentFontColor = fontColor.group();
			}

			if (currentFontFamily == null)
				continue;

			if (element.textNodes().isEmpty())
				continue;

			// System.out.println("element class types: " +
			// element.attr("class"));
			// System.out.println(element);
			// System.out.println("___________");

			// if (!currentFontFamily.equals(textFontFamily)) {
			// documentText.append("");
			// continue;
			// }
			// if (!currentFontSize.equals(textFontSize)) {
			// documentText.append("");
			// continue;
			// }
			String text = documentSentences.get(i);
			// listToString(textNodes(currentFontFamily, element));

			if (!text.trim().isEmpty()) {
				// System.out.println("current line: " + currentFontFamily + " :
				// " + text);

				String delimiter = "";
				String preDelimiter = "";
				final String nextText = documentSentences.get(i + 1);
				if (text.endsWith("-")) {
					text = text.substring(0, text.length() - 1);
					delimiter = "";
				} else {
					if (text.length() <= 3) {
						// final String nextText =
						// listToString(textNodes(currentFontFamily,
						// elements.get(i + 1)));

						if (nextText.length() <= 3) {
							if (currentFontColor.equals(textFontColor)) {
								delimiter = "";
							} else {
								delimiter = " ";
							}
						} else {
							delimiter = "\n";
						}
					} else if (currentFontSize != null && currentFontSize.equals(textFontSize)) {

						// final String nextText =
						// listToString(textNodes(currentFontFamily,
						// elements.get(i + 1)));
						if (nextText.length() <= 3) {
							if (currentFontColor.equals(textFontColor)) {
								delimiter = " ";
							} else {
								delimiter = "\n";
							}
						} else {
							delimiter = "\n";
						}
					} else if (currentFontSize != null && !currentFontSize.equals(lastTextFontSize)) {

						preDelimiter = " ";

					} else if (currentFontSize != null && currentFontSize.equals(lastNonTextFontSize)) {
						delimiter = "\n";
					} else {
						delimiter = "\n";
					}
					if (currentFontSize != null && !currentFontSize.equals(textFontSize))
						lastNonTextFontSize = currentFontSize;
				}

				lastTextFontSize = currentFontSize;
				documentText.append(preDelimiter);
				documentText.append(text);
				documentText.append(delimiter);
			}

		}

		System.out.println("Write data to mapping file...");
		PrintStream mappingFilePrintStream = new PrintStream(new File("pdf2htmlex/res/base64Mapping"));
		for (Entry<String, Map<String, String>> string : base64ToCharMapping.entrySet()) {
			System.out.print(".");

			Thread.sleep(80);
			final String base64 = string.getKey();
			mappingFilePrintStream.println("base64\t" + base64);

			for (Entry<String, String> string2 : string.getValue().entrySet()) {
				final String badChar = string2.getKey();
				final String correctChar = string2.getValue();
				mappingFilePrintStream.println(badChar + "\t" + correctChar);
			}
			mappingFilePrintStream.println();
			mappingFilePrintStream.println();

		}
		System.out.println();
		System.out.println("####DONE####");
		System.out.println();
		System.out.println();
		// System.out.println(documentText);

	}

	private static void mappingListToMap(List<String> mappingList) {

		String key = null;
		String bytes = null;
		String charMapping = null;
		for (String line : mappingList) {

			if (line.trim().isEmpty())
				continue;

			if (line.startsWith("base64")) {
				key = line.split("\t")[1];
				base64ToCharMapping.putIfAbsent(key, new HashMap<>());
			} else {
				bytes = line.split("\t")[0];
				charMapping = line.split("\t").length == 2 ? line.split("\t")[1] : "";

				base64ToCharMapping.get(key).put(bytes, charMapping);
			}
		}

	}

	static final private Pattern fontBase64Pattern = Pattern.compile(
			"@font-face\\{font-family:(?<ff>ff[0-9abcdef]);src:url\\('data:application\\/font-woff;base64,(?<base64>.*)'\\)");

	private static void extractFonts(Map<String, String> map, String html) {

		Matcher matcher = fontBase64Pattern.matcher(html);

		while (matcher.find()) {

			final String fontFam = matcher.group("ff");
			final String base64 = matcher.group("base64");

			map.put(fontFam, base64);
		}

	}

	private static String getTextSpecs(Pattern p, Document doc) {
		Map<String, Integer> classCounter = new HashMap<>();

		for (Element element : doc.body().getAllElements()) {

			if (!element.getElementsByTag("div").isEmpty()) {
				Matcher fontFamily = p.matcher(element.attr("class"));

				if (!fontFamily.find())
					continue;

				String classString = fontFamily.group();
				classCounter.put(classString, classCounter.getOrDefault(classString, 0) + 1);
			}
		}

		int maxOccurrence = 0;
		String textFontFamily = null;
		for (Entry<String, Integer> classCount : classCounter.entrySet()) {
			if (classCount.getValue() > maxOccurrence) {
				maxOccurrence = classCount.getValue();
				textFontFamily = classCount.getKey();
			}
		}

		System.out.println("maxOccuringClass = " + textFontFamily);

		classCounter.entrySet().forEach(System.out::println);
		return textFontFamily;
	}

	private static String listToString(List<String> textNodes) {
		final StringBuffer text = new StringBuffer();
		for (String string : textNodes) {
			text.append(Normalizer.normalize(string, Form.NFKC));
		}
		return text.toString();
	}

	public static List<String> cleanedTextChunks(final String currentFontFamily, Element element) {
		List<String> textNodes = new ArrayList<>();
		for (final Node node : element.childNodes()) {
			if (node instanceof TextNode) {
				// System.out.println("TextNode = " + node);
				final String text = ((TextNode) node).getWholeText().replaceAll(" ", "");
				if (text.matches(BAD_CHAR)) {
					textNodes.add(mapChars(textNodes, currentFontFamily, text));
				} else {
					textNodes.add(text);
				}

			} else if (node instanceof Element) {

				String text = ((Element) node).text();

				if (!text.isEmpty()) {
					final String fontSpecs = ((Element) node).attr("class");
					Matcher m = fontFamilyPattern.matcher(fontSpecs);
					if (m.find()) {
						if (text.matches(BAD_CHAR)) {
							textNodes.add(mapChars(textNodes, m.group(), text));
						} else if (text.trim().length() == 1 && !m.group().equals(textFontFamily)) {
							textNodes.add(mapChars(textNodes, m.group(), text));
						} else {
							textNodes.add(text);
						}
					} else {
						textNodes.add(text);
					}
				} else {
					Matcher textMatcher = spanTextPattern.matcher(((Element) node).toString());

					final String fontSpecs = ((Element) node).attr("class");
					Matcher m = fontFamilyPattern.matcher(fontSpecs);

					final String fontFamily;

					if (m.find()) {
						fontFamily = m.group();
					} else {
						fontFamily = currentFontFamily;
					}

					if (textMatcher.find()) {

						text = textMatcher.group("text");

						if (text.isEmpty())
							continue;

						if (text.matches(BAD_CHAR)) {
							textNodes.add(mapChars(textNodes, fontFamily, text));
						} else if (text.trim().length() == 1 && !fontFamily.equals(textFontFamily)) {
							textNodes.add(mapChars(textNodes, fontFamily, text));
						} else {
							textNodes.add(text);
						}
					}

				}
			}
		}
		return Collections.unmodifiableList(textNodes);
	}

	static boolean waitForInput = false;
	static final Scanner input = new Scanner(System.in);

	private static String mapChars(List<String> textNodes, String ff, String badChar) {
		if (badChar.trim().isEmpty())
			return badChar;
		waitForInput = false;
		if (!fontToBase64.containsKey(ff)) {
			System.out.println("Could not find base64 string for font family name: " + ff);
			waitForInput = true;
			// return "<" + badChar + ">";
		}

		if (!base64ToCharMapping.containsKey(fontToBase64.get(ff))) {
			System.out.println("Could not find base64 in mapping table: " + fontToBase64.get(ff));
			waitForInput = true;
			// return "<" + badChar + ">";
		}

		if (!base64ToCharMapping.get(fontToBase64.get(ff)).containsKey(badChar)) {
			System.out.println("base64\t" + fontToBase64.get(ff));
			System.out.println("Could not find mapping for bad char: " + badChar);
			waitForInput = true;
			// return "<" + badChar + ">";
		}
		final String context = listToString(textNodes);
		System.out.println("In context: " + context);

		if (waitForInput) {

			System.out.println("Enter correct symbol:\n\n");
			while (!input.hasNext()) {
			}
			String correctChar = input.next();

			base64ToCharMapping.get(fontToBase64.get(ff)).put(badChar, correctChar);
			System.out.println("Replace: '" + badChar + "' with " + "'" + correctChar + "'");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return base64ToCharMapping.get(fontToBase64.get(ff)).get(badChar);
	}

}
