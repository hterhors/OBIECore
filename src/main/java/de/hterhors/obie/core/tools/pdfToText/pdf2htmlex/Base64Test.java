package de.hterhors.obie.core.tools.pdfToText.pdf2htmlex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.xerces.impl.dv.util.Base64;

public class Base64Test {

	public static void main(String[] args) throws IOException {

		List<String> fonts = Files.readAllLines(new File("pdf2htmlex/res/font/font.base64").toPath());

		int counter = 0;
		for (String font : fonts) {

			byte[] b = Base64.decode(font);

			FileOutputStream fos = new FileOutputStream("pdf2htmlex/gen/font" + counter + ".woff");
			fos.write(b);
			fos.close();
			counter++;
		}
	}
}
