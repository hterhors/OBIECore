package de.hterhors.obie.core.tools.pmc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import de.hterhors.obie.core.tools.pmc.converter.DOI2PMCIDConverter;
import de.hterhors.obie.core.tools.pmc.exceptions.UnkownOrInvalidDOIException;

/**
 * This class can be used to download the full text html pages where the open
 * access database entry is missing.
 * 
 * @author hterhors
 *
 *         Apr 19, 2016
 */
public class FullTextPageLoader {

	final public static String DOCUMENT_PREFIX = "AutoDownload_";

	final public static String BASE_URL = "http://www.ncbi.nlm.nih.gov/pmc/articles/%s/";

	/*
	 * Elements of interest in the XML file provided by the ope access database.
	 */
	final public static List<String> ELEMENTS_OF_INTEREST = Arrays.asList("h1", "h2", "h3", "h4", "p", "span");

	public static void main(String[] args)
			throws UnkownOrInvalidDOIException, IOException, ParserConfigurationException, SAXException {

		List<String> PMCs = new ArrayList<>(DOI2PMCIDConverter
				.convertDOIs(Files.readAllLines(FileSystems.getDefault().getPath("res/DOIS"))).values());

		extractBatchPMCPages(PMCs);
	}

	private static void extractBatchPMCPages(String[] pmcIDs) throws UnkownOrInvalidDOIException, IOException {
		for (String pmcID : pmcIDs) {
			extractPMCPage(pmcID);
		}
	}

	private static void extractBatchPMCPages(List<String> pmcIDs) throws UnkownOrInvalidDOIException, IOException {

		for (String pmcID : pmcIDs) {
			extractPMCPage(pmcID);
		}

	}

	private static void extractPMCPage(String pmcID) throws UnkownOrInvalidDOIException, IOException {

		if (pmcID.trim().isEmpty()) {
			return;
		}

		System.out.println("Extract :" + pmcID);
		String XMLDocument = null;
		try {
			System.out.println("Download page...");
			XMLDocument = executeDownload(pmcID);
		} catch (Exception e) {
			throw new UnkownOrInvalidDOIException("One or more of the requested IDs are not valid:" + pmcID);
		}

		if (XMLDocument == null) {
			System.err.println("Could not find page for id: " + pmcID);
			return;
		}

		System.out.println("Extract content...");
		final String document = extractXMLContent(XMLDocument);

		System.out.println("Print document...");
		final PrintStream ps = new PrintStream("gen/files/" + DOCUMENT_PREFIX + pmcID);

		ps.println(document);

		ps.close();

	}

	private static String executeDownload(final String pmcid) {
		String targetURL = String.format(BASE_URL, pmcid);
		// System.out.println(targetURL);
		URLConnection connection = null;
		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = url.openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;

			boolean includeLines = false;

			while ((line = rd.readLine()) != null) {

				if (line.contains("<!--main-content-->"))
					includeLines = true;

				if (!includeLines)
					continue;

				response.append(line);
				response.append("\n");
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
		}
	}

	private static String extractXMLContent(final String xmlDoc) {
		StringBuffer document = new StringBuffer();

		Elements nList = Jsoup.parse(xmlDoc).getAllElements();

		for (int temp = 0; temp < nList.size(); temp++) {

			Element nNode = nList.get(temp);

			// boolean inSec = false;
			final String tag = nNode.tag().getName();

			if (ELEMENTS_OF_INTEREST.contains(tag)) {

				if (nNode.nodeName().startsWith("h")) {
					document.append("\n");
				} else {
					document.append("\t");
				}

				document.append(Jsoup.parse(Jsoup.clean(nNode.html(), Whitelist.basic())).text() + "\n");

			}
		}

		return document.toString().replaceAll(" +", " ");
	}
}
