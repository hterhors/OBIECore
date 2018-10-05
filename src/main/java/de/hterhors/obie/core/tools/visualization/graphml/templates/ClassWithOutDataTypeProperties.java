package de.hterhors.obie.core.tools.visualization.graphml.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ClassWithOutDataTypeProperties implements IGraphMLContent {

	final private int id;
	final private float height;
	final private float width;
	final private float x;
	final private float y;
	final private String name;

	final public static String template = readTemplate();

	private static String readTemplate() {
		try {
			return Files.readAllLines(new File("graphml/templates/class_without_datatype_properties.tmplt").toPath(),
					Charset.defaultCharset()).stream().reduce("", String::concat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ClassWithOutDataTypeProperties(int id, float height, float width, float x, float y, String name) {
		this.id = id;
		this.height = Math.min(60, height);
		this.width = Math.max(width, 10 * name.length());
		this.x = x;
		this.y = y;
		this.name = name;

	}

	public static class Builder {
		final private int id;
		private float height = 100;
		private float width = 100;
		private float x = 0;
		private float y = 0;
		private String name = "UnnamedClass";

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

		public int getId() {
			return id;
		}

		public ClassWithOutDataTypeProperties build() {
			return new ClassWithOutDataTypeProperties(id, height, width, x, y, name);
		}

	}

	@Override
	public String toString() {
		return String.format(template, id, height, width, x, y, name);
	}

}
