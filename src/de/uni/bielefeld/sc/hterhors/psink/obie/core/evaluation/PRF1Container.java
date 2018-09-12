package de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation;

/**
 * Simple container to store precision, recall and f1 score.
 * 
 * @author hterhors
 *
 */
public class PRF1Container {
	final public double p, r, f1;

	public PRF1Container(double p, double r, double f1) {
		this.p = p;
		this.r = r;
		this.f1 = f1;
	}

	@Override
	public String toString() {
		return "p: " + p + "\tr: " + r + "\tf1: " + f1;
	}

}
