package de.hterhors.obie.core.tokenizer;

import java.util.regex.Pattern;

public class ContentCleaner {

	public static boolean ENABLE_PORT_STEMMER = false;
	public static boolean REMOVE_STOPWORDS = false;
	public static boolean TO_LOWER_CASE_IF_NOT_UPPER_CASE = false;
	public static boolean ENABLE_TOKENIZATION_RULES = true;
	public static boolean ENABLE_NUMBER_REPLACEMENT_RULE = false;

	private static final String TOKEN_SPLITTERATOR = " ";
	// private static Pattern p1 = Pattern.compile("\\[[0-9]+\\]");
	// private static Pattern p2 =
	// Pattern.compile("(\\,|\\.|:)(?![0-9])|;|\"|\\?");
	// private static Pattern p3 = Pattern.compile("'s|s'");
	// private static Pattern p4 = Pattern.compile("\\(|\\)|\\{|\\}|'");
	// private static Pattern p4 = Pattern.compile("\\[|\\]|\\(|\\)|\\{|\\}|'");
//	private static Pattern p3 = Pattern.compile("\\]");
//	private static Pattern p4 = Pattern.compile("\\[");
	private static Pattern p5 = Pattern.compile("\\s+");
	private static Pattern p6 = Pattern.compile("_+");
//	private static Pattern p7 = Pattern.compile("([\\ud800-\\udbff\\udc00\\udfff])");
	// private static Pattern p8 = Pattern.compile("");
	// public static Pattern p7 = Pattern.compile("[^\\x20-\\x7E]+");

	public static String bagOfWordsTokenizer(String text) {
		if (ENABLE_TOKENIZATION_RULES) {
			// text = text.replaceAll(p1.pattern(), "");
			// text = text.replaceAll(p2.pattern(), TOKEN_SPLITTERATOR);
			// text = text.replaceAll(p3.pattern(), "s");
			// text = text.replaceAll(p4.pattern(), "");
//			text = text.replaceAll(p3.pattern(), " ] ");
//			text = text.replaceAll(p4.pattern(), " [ ");
			text = text.replaceAll(p6.pattern(), "_");
			// text = text.replaceAll(p8.pattern(), "-");
//			text = text.replaceAll(p7.pattern(), TOKEN_SPLITTERATOR);
			text = text.replaceAll(p5.pattern(), TOKEN_SPLITTERATOR);
		}

		// StringBuffer modifiedText = new StringBuffer();
		// for (String token : text.split(" ")) {
		//
		// if (REMOVE_STOPWORDS)
		// continue;
		//
		// if (TO_LOWER_CASE_IF_NOT_UPPER_CASE) {
		// token = StringUtil.toLowerCaseIfNotUppercase(token);
		// }
		//
		// modifiedText.append(token + " ");
		// }

		// text = modifiedText.toString().trim();

		return text;

	}

	private static ContentCleaner instance = null;

	private ContentCleaner() {
	}

}
