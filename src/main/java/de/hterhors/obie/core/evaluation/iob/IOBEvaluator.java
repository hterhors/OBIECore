package de.hterhors.obie.core.evaluation.iob;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.hterhors.obie.core.evaluation.evaluator.StandardPRF1Evaluator;

public class IOBEvaluator {

	public static enum IOBEnum {
		I, O, B;

		public String getAnnotationName() {
			if (this == O)
				return "|" + O.name();
			else
				return "|" + this.name() + "-Entity";
		}
	}

	public static void main(String[] args) throws IOException {

		List<IOBDocument> test = IOBReader.readFile(new File("scio/iob/scio-groupnames-test.iob"));
		List<IOBDocument> predict = IOBReader.readFile(new File("scio/iob/scio-crf-output.iob"));

//		 Set<OnlyTextEvaluationObject> gold = getSetEvaluation(test);

//		 Set<OnlyTextEvaluationObject> result = getSetEvaluation(predict);

		Set<FullEvaluationObject> gold = getFullEvaluation(test);

		Set<FullEvaluationObject> result = getFullEvaluation(predict);

		final double p = StandardPRF1Evaluator.recall(gold, result);
		final double r = StandardPRF1Evaluator.precision(gold, result);
		final double f1 = StandardPRF1Evaluator.f1(gold, result);

		System.out.println("Precisiion = " + p);
		System.out.println("Recall = " + r);
		System.out.println("F1 = " + f1);
	}

	private static Set<FullEvaluationObject> getFullEvaluation(List<IOBDocument> test) {
		return test.stream().flatMap(d -> d.annotations.stream())
				.map(annotation -> new FullEvaluationObject(annotation.textMention, annotation.onset, annotation.offset,
						annotation.docIndex))
				.collect(Collectors.toSet());
	}

	private static Set<OnlyTextEvaluationObject> getSetEvaluation(List<IOBDocument> test) {
		return test.stream().flatMap(d -> d.annotations.stream())
				.map(annotation -> new OnlyTextEvaluationObject(annotation.textMention, annotation.docIndex))
				.collect(Collectors.toSet());
	}

}
