package de.hterhors.obie.core.tools.pdfToText;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFBoxTool {

	/**
	 * This will print the documents text in a certain area.
	 *
	 * @param args
	 *            The command line arguments.
	 *
	 * @throws IOException
	 *             If there is an error parsing the document.
	 */
	public static void main(String[] args) throws IOException {
		// if (args.length != 1) {
		// usage();
		// } else {
		PDDocument document = null;
		try {
			document = PDDocument
					.load(new File("res/knowtator/annotations/brigitte1/pdf/Ates et al. 2007, 17532502.pdf"));
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSortByPosition(true);

			System.out.println("Text in the pdf:");
			System.out.println(stripper.getText(document));
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}
	// }
	//
	// /**
	// * This will print the usage for this document.
	// */
	// private static void usage() {
	// System.err.println("Usage: java " + PDFBoxTool.class.getName() + "
	// <input-pdf>");
	// }
}
