package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pdfToText;

public class Content {

	final public String content;
	final public boolean isTitle;
	final public boolean isSubTitle;

	public Content(String content, boolean isTitle, boolean isSubTitle) {
		this.content = content;
		this.isTitle = isTitle;
		this.isSubTitle = isSubTitle;
	}

	public Content(final String content) {
		this.content = content;
		this.isTitle = false;
		this.isSubTitle = false;
	}

	@Override
	public String toString() {
		return "Content [content=" + content + ", isTitle=" + isTitle + ", isSubTitle=" + isSubTitle + "]";
	}

}
