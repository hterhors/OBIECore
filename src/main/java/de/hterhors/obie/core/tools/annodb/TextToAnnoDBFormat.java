package de.hterhors.obie.core.tools.annodb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

import de.hterhors.obie.core.tokenizer.RegExTokenizer;
import de.hterhors.obie.core.tokenizer.SentenceSplitter;
import de.hterhors.obie.core.tokenizer.Token;
import de.hterhors.obie.core.tokenizer.Tokenization;

/**
 * Converts a given document into the tokenized annodb format.
 * 
 * @author hterhors
 *
 */
public class TextToAnnoDBFormat {

	public static void main(String[] args) throws IOException {

		String r1 = new String("Barack_Obama");
		String r2 = new String("Michael_Jackson");
		String r3 = new String("The_Walt_Disney_Company");
		String r4 = new String("Dirk_Nowitzki");
		String r5 = new String("Babe_Ruth");
		String r6 = new String("Computational_linguistics");
		String r7 = new String("Artificial_intelligence");
		String r8 = new String("Named-entity_recognition");
		String r9 = new String("Apache_Software_Foundation");
		String r10 = new String("Nelson_Mandela");
		String r11 = new String("Lionel_Richie");
		String r12 = new String("Queen_(band)");
		String r13 = new String("Freddie_Mercury");
		String r14 = new String("Australia");
		String r15 = new String("Germany");

		String[] resources = new String[] { r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15 };

		for (String resource : resources) {

			File file = new File("dbpedia/documents/" + resource + ".txt");
			String input = Files.readAllLines(file.toPath()).stream()
					.reduce("", (accumulatedStr, str) -> accumulatedStr + str + " ").trim();
			List<String> sentences = SentenceSplitter.extractSentences(input);
			List<Tokenization> tokens = RegExTokenizer.tokenize(sentences);
			PrintStream ps = new PrintStream(new File("dbpedia/annodb/" + resource + ".csv"));

			for (Tokenization tokenization : tokens) {
				int tokenIndex = 1;
				ps.println("#" + tokenization.originalSentence);
				for (Token token : tokenization.tokens) {
					ps.println(token.getSentenceIndex() + 1 + "\t" + tokenIndex + "\t" + token.getFromCharPosition()
							+ "\t" + token.getToCharPosition() + "\t" + token.getText());
					tokenIndex++;
				}

			}
		}
	}

}
