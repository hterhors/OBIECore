package de.hterhors.obie.core.tools.pmc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class XMLLoaderUtils {
	/**
	 * This method uses the API by NCBI to map DOIS to PMCIDs
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String downloadXMLDocument(URL url) throws IOException, UnsupportedEncodingException {
		final StringBuffer document = new StringBuffer();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			for (String line; (line = reader.readLine()) != null;) {
				document.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			throw e;
		}
		return document.toString().trim();
	}
}
