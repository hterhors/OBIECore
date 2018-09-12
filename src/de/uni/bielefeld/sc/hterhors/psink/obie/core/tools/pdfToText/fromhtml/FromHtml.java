package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pdfToText.fromhtml;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FromHtml {

	public static void main(String[] args) throws IOException {

		final String file = "/home/hterhors/Downloads/pdftotext/test.html";

		Document doc = Jsoup.parse(new File(file), "utf-8");

		// System.out.println(doc.text().replaceAll("(?![a-z])-(?=[1-z])",
		// "").replaceAll("⬃", "~").replaceAll("␤", "β")
		// .replaceAll("⍀", "Ω").replaceAll("⬍", "<").replaceAll("␮",
		// "µ").replaceAll("⫻", "x")
		// .replaceAll("⫽", "="));

		for (Element element : doc.body().getAllElements()) {
			if (element.toString().length() <= 1000) {

				System.out.println(element);
				System.out.println("___________");
			}
		}

		// BufferedReader br = new BufferedReader(new FileReader(new
		// File(file)));
		//
		// String prefToken = "***";
		//
		// String data = "";
		// int i = 0;
		// StringBuffer bf = new StringBuffer();
		// while ((data = br.readLine()) != null) {
		// i++;
		// if (i < 7)
		// continue;
		//
		// String d = data.substring(1, data.length() - 1);
		// bf.append(d);
		//
		// if (!prefToken.trim().isEmpty() && d.isEmpty()) {
		// bf.append(" ");
		// }
		//
		// prefToken = d;
		// }
		//
		// br.close();
		//
		// extractSentences(bf.toString()).forEach(System.out::println);
	}

	private static List<String> extractSentences(final String document) {

		BreakIterator sentenceBoundary = BreakIterator.getSentenceInstance(Locale.ENGLISH);
		sentenceBoundary.setText(document);
		List<Integer> sentenceOffsets = new ArrayList<>();
		List<String> sentences = new ArrayList<>();
		int start = sentenceBoundary.first();
		for (int end = sentenceBoundary.next(); end != BreakIterator.DONE; start = end, end = sentenceBoundary.next()) {
			sentenceOffsets.add(end);
			sentences.add(document.substring(start, end).replaceAll("\n", " "));
		}
		return sentences;

	}

}
