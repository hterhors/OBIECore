package de.hterhors.obie.core.tools.visualization.graphml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import de.hterhors.obie.core.tools.visualization.graphml.templates.IGraphMLContent;

public class GraphML {

	final private String content;

	final public static String template = readTemplate();

	private static String readTemplate() {
		try {
			return Files
					.readAllLines(new File("graphml/templates/empty_graph.tmplt").toPath(), Charset.defaultCharset())
					.stream().reduce("", String::concat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public GraphML(List<IGraphMLContent> listOfContent) {
		this.content = toContentString(listOfContent);

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
		return String.format(template, content);
	}

}
