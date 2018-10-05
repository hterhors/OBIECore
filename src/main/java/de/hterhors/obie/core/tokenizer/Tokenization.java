package de.hterhors.obie.core.tokenizer;

import java.util.List;

public class Tokenization {

	public List<Token> tokens;
	public String originalSentence;
	public int absoluteStartOffset;
	public int absoluteEndOffset;

	public Tokenization(List<Token> tokens, String originalSentence, int absoluteStartOffset) {
		this.tokens = tokens;
		this.originalSentence = originalSentence;
		this.absoluteStartOffset = absoluteStartOffset;
		this.absoluteEndOffset = absoluteStartOffset + originalSentence.length();
	}

	@Override
	public String toString() {
		return "Tokenization [" + absoluteStartOffset + "-" + absoluteEndOffset + ": " + originalSentence + "\n\t"
				+ tokens + "]";
	}

}
