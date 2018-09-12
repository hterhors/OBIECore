package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.visualization.graphml.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GroupNodes implements IGraphMLContent {

	final private int id;
	final String name;
	final private List<IGraphMLContent> content;

	final public static String template = readTemplate();

	private static String readTemplate() {
		try {
			return Files
					.readAllLines(new File("graphml/templates/group_nodes.tmplt").toPath(), Charset.defaultCharset())
					.stream().reduce("", String::concat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private GroupNodes(int id, String name, List<IGraphMLContent> content) {
		this.id = id;
		this.content = content;
		this.name = name;

	}

	public static class Builder {
		final private int id;
		private String name;
		private List<IGraphMLContent> content = new ArrayList<>();

		public Builder(int id) {
			this.id = id;
		}

		public List<IGraphMLContent> getContent() {
			return content;
		}

		public Builder setContent(List<IGraphMLContent> content) {
			this.content = content;
			return this;
		}

		public Builder addContent(IGraphMLContent content) {
			this.content.add(content);
			return this;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public GroupNodes build() {
			return new GroupNodes(id, name, content);
		}

	}

	private String toContentString(List<IGraphMLContent> attributeList) {
		StringBuilder sb = new StringBuilder();
		for (IGraphMLContent content : attributeList) {
			sb.append(content.toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		String contentAsString = toContentString(content);

		return String.format(template, id, name, name, (id + name), contentAsString);
	}

}
