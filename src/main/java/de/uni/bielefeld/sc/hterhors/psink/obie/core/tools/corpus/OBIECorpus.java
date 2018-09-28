package de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

/**
 * A document with annotations is called a instance or corpus instance.
 * 
 * @author hterhors
 *
 * @param
 */
public class OBIECorpus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	public static class Instance implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1;

		public final String name;
		public final String content;
		public final Map<Class<? extends IOBIEThing>, List<IOBIEThing>> annotations;

		public Instance(String name, String content, Map<Class<? extends IOBIEThing>, List<IOBIEThing>> annotations) {
			this.name = name;
			this.content = content;
			this.annotations = Collections.unmodifiableMap(annotations);
		}

	}

	private final Map<String, Instance> trainingInstances;
	private final Map<String, Instance> developInstances;
	private final Map<String, Instance> testingInstances;

	private final String corpusName;

	private final int version;
	private final Set<Class<? extends IOBIEThing>> rootClassTypes;
	/**
	 * Sorted list of all names of the documents in this corpus.
	 */
	private final List<String> allDocumentNames;

	public OBIECorpus(Map<String, Instance> trainingInstances, Map<String, Instance> developInstances,
			Map<String, Instance> testInstances, String corpusName, final int version) {
		this.trainingInstances = trainingInstances;
		this.developInstances = developInstances;
		this.testingInstances = testInstances;
		this.corpusName = corpusName;
		this.version = version;
		this.rootClassTypes = Stream
				.concat(trainingInstances.values().stream(),
						Stream.concat(developInstances.values().stream(), testInstances.values().stream()))
				.flatMap(a -> a.annotations.keySet().stream()).collect(Collectors.toSet());

		this.allDocumentNames = Stream
				.concat(trainingInstances.keySet().stream(),
						Stream.concat(developInstances.keySet().stream(), testInstances.keySet().stream()))
				.distinct().sorted().collect(Collectors.toList());

	}

	public Map<String, Instance> getTrainingInstances() {
		return trainingInstances;
	}

	public Map<String, Instance> getDevelopInstances() {
		return developInstances;
	}

	public Map<String, Instance> getTestInstances() {
		return testingInstances;
	}

	/**
	 * Returns all instances.
	 * 
	 * @return
	 */
	public Map<String, Instance> getInstances() {
		Map<String, Instance> allInstances = new HashMap<>();

		allInstances.putAll(trainingInstances);
		allInstances.putAll(developInstances);
		allInstances.putAll(testingInstances);

		return allInstances;

	}

	public String getCorpusName() {
		return corpusName;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "RawCorpus [trainingInstances.size()=" + trainingInstances.size() + ", developInstances.size()="
				+ developInstances.size() + ", testingInstances.size()=" + testingInstances.size() + ", corpusName="
				+ corpusName + ", version=" + version + "]";
	}

	/**
	 * Writes a corpus to the file-system.
	 * 
	 * @param filename the name of the corpora.
	 * @param data     the actual data.
	 */
	public void writeRawCorpusData(final File file) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	/**
	 * Reads a binary SCIOCopus.
	 * 
	 * @param filename fileName of the corpus.
	 * @return the SCIOCorpus.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static OBIECorpus readRawCorpusData(final File file) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			final OBIECorpus data = (OBIECorpus) in.readObject();
			return data;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					System.exit(-1);
				}
		}
	}

	public Set<Class<? extends IOBIEThing>> getRootClasses() {
		return rootClassTypes;
	}

	public List<String> getAllInstanceNames() {
		return allDocumentNames;
	}

}
