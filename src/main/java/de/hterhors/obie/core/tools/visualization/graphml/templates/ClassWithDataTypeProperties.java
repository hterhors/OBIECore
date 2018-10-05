package de.hterhors.obie.core.tools.visualization.graphml.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassWithDataTypeProperties implements IGraphMLContent {

	final private int id;
	final private float height;
	final private float width;
	final private float x;
	final private float y;
	final private String name;
	final private String attributes;

	final public static String template = readTemplate();

	private static String readTemplate() {
		try {
			return Files.readAllLines(new File("graphml/templates/class_with_datatype_properties.tmplt").toPath(),
					Charset.defaultCharset()).stream().reduce("", String::concat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ClassWithDataTypeProperties(int id, float height, float width, float x, float y, String name,
			List<String> attributes) {
		this.id = id;
		this.height = Math.min(60 + 10 * attributes.size(), height);
		this.width = Math.max(width, 10 * Math.max(name.length(), attributes.stream().max(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}).get().length()));
		this.x = x;
		this.y = y;
		this.name = name;

		this.attributes = toAttributeString(attributes);
	}

	private String toAttributeString(List<String> attributeList) {
		StringBuilder sb = new StringBuilder();
		for (String attribute : attributeList) {
			sb.append(attribute).append("\n");
		}
		return sb.toString();
	}

	public static class Builder {
		final private int id;
		private float height = 100;
		private float width = 100;
		private float x = 0;
		private float y = 0;
		private String name = "UnnamedClass";
		private List<String> attributes = new ArrayList<>();

		public Builder(int id) {
			this.id = id;
		}

		public float getHeight() {
			return height;
		}

		public Builder setHeight(float height) {
			this.height = height;
			return this;
		}

		public float getWidth() {
			return width;
		}

		public Builder setWidth(float width) {
			this.width = width;
			return this;
		}

		public float getX() {
			return x;
		}

		public Builder setX(float x) {
			this.x = x;
			return this;
		}

		public float getY() {
			return y;
		}

		public Builder setY(float y) {
			this.y = y;
			return this;
		}

		public String getName() {
			return name;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public List<String> getAttributes() {
			return attributes;
		}

		public Builder setAttributes(List<String> attributes) {
			this.attributes = attributes;
			return this;
		}

		public int getId() {
			return id;
		}

		public ClassWithDataTypeProperties build() {
			return new ClassWithDataTypeProperties(id, height, width, x, y, name, attributes);
		}

	}

	@Override
	public String toString() {
		return String.format(template, id, height, width, x, y, name, attributes);
	}

}
