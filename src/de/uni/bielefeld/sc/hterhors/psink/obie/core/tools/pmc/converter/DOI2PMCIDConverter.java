package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc.converter;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc.XMLLoaderUtils;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.pmc.exceptions.UnkownOrInvalidDOIException;

/**
 * This class can be used to convert DOI IDs to PMCIDs
 * 
 * @author hterhors
 *
 *         Apr 14, 2016
 */
public class DOI2PMCIDConverter {

	final public static String BASE_URL = "http://www.ncbi.nlm.nih.gov/pmc/utils/idconv/v1.0/?&ids=%s";

	public static void main(String[] args) throws UnsupportedEncodingException, IOException,
			ParserConfigurationException, SAXException, UnkownOrInvalidDOIException {
		args = new String[] { "10.1093/nar/gks1195", "10.1002/cne.22149" };

		Map<String, String> mapping = DOI2PMCIDConverter.convertDOIs(args);
		mapping.entrySet().forEach(System.out::println);

	}

	/**
	 * @param DOIs
	 *            a list that contains DOIs to convert.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws UnkownOrInvalidDOIException
	 */
	public static Map<String, String> convertDOIs(List<String> DOIs) throws UnsupportedEncodingException, IOException,
			ParserConfigurationException, SAXException, UnkownOrInvalidDOIException {
		URL url = buildURL(DOIs);

		String XMLDocument = null;
		try {

			XMLDocument = XMLLoaderUtils.downloadXMLDocument(url);
		} catch (Exception e) {
			throw new UnkownOrInvalidDOIException("One or more of the requested IDs are not valid:" + DOIs);
		}

		return readMappingFromXML(DOIs, XMLDocument);
	}

	/**
	 * @param DOIs
	 *            an array that contains DOIs to convert.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws UnkownOrInvalidDOIException
	 */
	public static Map<String, String> convertDOIs(String[] DOIs) throws UnsupportedEncodingException, IOException,
			ParserConfigurationException, SAXException, UnkownOrInvalidDOIException {
		return convertDOIs(Arrays.asList(DOIs));
	}

	/**
	 * Reads an XML file and extracts and maps all PMCIDs to DOIs.
	 * 
	 * @param args
	 * @param XMLDocument
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws UnkownOrInvalidDOIException
	 */
	private static Map<String, String> readMappingFromXML(List<String> args, String XMLDocument)
			throws ParserConfigurationException, SAXException, IOException, UnkownOrInvalidDOIException {
		Map<String, String> mapping = new HashMap<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new StringBufferInputStream(XMLDocument));

		doc.getDocumentElement().normalize();

		if (!doc.getDocumentElement().getAttribute("status").equals("ok"))
			throw new UnkownOrInvalidDOIException("One or more of the requested IDs are not valid:" + args);

		NodeList nList = doc.getElementsByTagName("record");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				final String DOI = eElement.getAttribute("requested-id");
				final String PMCID = eElement.getAttribute("pmcid");

				mapping.put(DOI, PMCID);
			}
		}
		return mapping;
	}

	/**
	 * This method builds a valid URL given an array of DOIs.
	 * 
	 * @param args
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL buildURL(List<String> args) throws MalformedURLException {

		final String IDList = args.stream().map(id -> id.toString() + ",").reduce("", String::concat);

		return new URL(String.format(BASE_URL, IDList));
	}

}
