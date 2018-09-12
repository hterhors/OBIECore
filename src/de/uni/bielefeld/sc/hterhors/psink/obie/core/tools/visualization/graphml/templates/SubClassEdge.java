package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class SubClassEdge implements IGraphMLContent {

	final private int id;
	final private int sourceId;
	final private int targetId;
	final private String label;

	final public static String template = readTemplate();

	private static String readTemplate() {
		try {
			return Files
					.readAllLines(new File("graphml/templates/sub_class_edge.tmplt").toPath(), Charset.defaultCharset())
					.stream().reduce("", String::concat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private SubClassEdge(int id, int sourceId, int targetId, String label) {
		this.id = id;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.label = label;

	}

	public static class Builder {
		final private int id;
		final private int sourceId;
		final private int targetId;
		private String label = "";

		public Builder(int id, int sourceId, int targetId) {
			this.id = id;
			this.sourceId = sourceId;
			this.targetId = targetId;
		}

		public String getLabel() {
			return label;
		}

		public Builder setLabel(String label) {
			this.label = label;
			return this;
		}

		public int getId() {
			return id;
		}

		public int getSourceId() {
			return sourceId;
		}

		public int getTargetId() {
			return targetId;
		}

		public SubClassEdge build() {
			return new SubClassEdge(id, sourceId, targetId, label);
		}

	}

	@Override
	public String toString() {
		return String.format(template, id, sourceId, targetId, label);
	}

}
