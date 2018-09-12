package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
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

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc.converter.DOI2PMCIDConverter;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc.exceptions.UnkownOrInvalidDOIException;

/**
 * This class can be used to download and extract full text from pmc full text
 * article given their pmcid.
 * 
 * @author hterhors
 *
 *         Apr 14, 2016
 */
public class FullTextXMLDownloader {

	final public static String DOCUMENT_PREFIX = "OpenAccess_";

	final public static String BASE_URL = "http://www.ncbi.nlm.nih.gov/pmc/oai/oai.cgi?verb=GetRecord&identifier=oai:pubmedcentral.nih.gov:%s&metadataPrefix=pmc";

	/*
	 * Elements of interest in the XML file provided by the ope access database.
	 */
	final public static List<String> ELEMENTS_OF_INTEREST = Arrays.asList("article-title", "surname", "given-names",
			"subject", "p", "title", "label");

	public static void main(String[] args)
			throws UnkownOrInvalidDOIException, ParserConfigurationException, SAXException, IOException {

		List<String> PMCs = new ArrayList<>(DOI2PMCIDConverter
				.convertDOIs(Files.readAllLines(FileSystems.getDefault().getPath("res/DOIS"))).values());

		batchExtractor(PMCs);

	}

	public static void batchExtractor(List<String> args)
			throws FileNotFoundException, MalformedURLException, UnkownOrInvalidDOIException {
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).isEmpty()) {
				continue;
			}

			final String doc = postProcess(extractDocumentForPMCID(args.get(i)));

			if (!doc.trim().isEmpty()) {
				System.out.println("Print document...");

				PrintStream ps = new PrintStream("gen/files/" + DOCUMENT_PREFIX + args.get(i));
				ps.println(doc);
				ps.close();
			} else {
				System.err.println("Could not extract text for document: " + args.get(i));
			}

		}
	}

	private static String postProcess(String extractDocumentForPMCID) {

		return extractDocumentForPMCID;
	}

	public static void batchExtractor(String[] args)
			throws FileNotFoundException, MalformedURLException, UnkownOrInvalidDOIException {
		batchExtractor(Arrays.asList(args));
	}

	private static String extractDocumentForPMCID(String pmcID)
			throws MalformedURLException, UnkownOrInvalidDOIException {

		System.out.println("Extract :" + pmcID);

		pmcID = pmcID.replaceAll("PMC", "");

		URL ncbiFullTextXMLURL = buildURL(pmcID);

		String XMLDocument = null;
		try {
			System.out.println("Download page...");
			XMLDocument = XMLLoaderUtils.downloadXMLDocument(ncbiFullTextXMLURL);
		} catch (Exception e) {
			throw new UnkownOrInvalidDOIException("One or more of the requested IDs are not valid:" + pmcID);
		}
		System.out.println("Extract content...");

		final String document = extractXMLContent(XMLDocument);
		return document;
	}

	private static URL buildURL(String pmcid) throws MalformedURLException {
		return new URL(String.format(BASE_URL, pmcid));
	}

	private static String extractXMLContent(final String xmlDoc) {
		StringBuffer document = new StringBuffer();

		Elements nList = Jsoup.parse(xmlDoc).getAllElements();

		for (int temp = 0; temp < nList.size(); temp++) {

			Element nNode = nList.get(temp);

			final String tag = nNode.tag().getName();
			if (ELEMENTS_OF_INTEREST.contains(tag)) {

				if (nNode.parentNode().nodeName().equals("arff")) {
					continue;
				}

				if (tag.equals("title") || tag.equals("label"))
					document.append("\n");
				document.append(Jsoup.parse(Jsoup.clean(nNode.html(), Whitelist.basic())).text());
				if (tag.equals("title") || tag.equals("label"))
					document.append("\n");

				if (tag.equals("surname") || tag.equals("given-names")) {
					document.append(" ");
				} else if (tag.equals("degrees")) {
					document.append("\n");
				} else {
					document.append("\n");
				}
			}
		}

		return document.toString().replaceAll(" +", " ");
	}

}
