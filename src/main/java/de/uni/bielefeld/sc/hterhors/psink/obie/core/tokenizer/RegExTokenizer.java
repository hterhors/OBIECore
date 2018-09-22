package de.uni.bielefeld.sc.hterhors.psink.obie.core.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTokenizer {

	private static Pattern pattern = Pattern.compile("[a-zA-Z]+|\\d+|[^\\w\\s]");

	public static List<Tokenization> tokenize(List<String> sentences) {
		List<Tokenization> tokenizations = new ArrayList<>();
		int accumulatedSentenceLength = 0;
		int sentenceIndex = 0;
		int index = 0;
		for (String sentence : sentences) {
			Matcher matcher = pattern.matcher(sentence);
			List<Token> tokens = new ArrayList<>();
			while (matcher.find()) {
				String text = matcher.group();
				int from = matcher.start();
				int to = matcher.end();
				tokens.add(new Token(sentenceIndex, index, accumulatedSentenceLength + from
				// + sentenceIndex
						,
						// sentenceIndex +
						accumulatedSentenceLength + to, text));
				index++;
			}
			sentenceIndex++;
			Tokenization tokenization = new Tokenization(tokens, sentence, accumulatedSentenceLength);
			tokenizations.add(tokenization);
			accumulatedSentenceLength += sentence.length();
		}
		return tokenizations;
	}

}