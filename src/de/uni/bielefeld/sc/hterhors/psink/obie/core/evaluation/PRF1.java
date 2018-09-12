package de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation;

/**
 * Container to store true positive, false psoitive and false negative. Able to
 * compute directly Precision, Recall, F1 Score and Jaccard.
 * 
 * @author hterhors
 *
 */

public class PRF1 {

	public double tp, fp, fn;

	public PRF1() {
		tp = 0;
		fp = 0;
		fn = 0;
	}

	public PRF1(int tp, int fp, int fn) {
		this.tp = tp;
		this.fp = fp;
		this.fn = fn;
	}

	/**
	 * Clone.
	 * 
	 * @param c
	 */
	public PRF1(PRF1 c) {
		this.tp = c.tp;
		this.fp = c.fp;
		this.fn = c.fn;
	}

	public PRF1 add(PRF1 adder) {
		this.tp += adder.tp;
		this.fp += adder.fp;
		this.fn += adder.fn;
		return this;
	}

	public PRF1 set(PRF1 setter) {
		this.tp = setter.tp;
		this.fp = setter.fp;
		this.fn = setter.fn;
		return this;
	}

	public double getF1() {
		final double p = getPrecision();
		final double r = getRecall();
		final double d = (p + r);
		return d == 0 ? 0 : (2 * p * r) / d;
	}

	public double getRecall() {
		if ((tp + fn) == 0)
			return 0;
		return tp / (tp + fn);
	}

	public double getPrecision() {
		if ((tp + fp) == 0)
			return 0;
		return tp / (tp + fp);
	}

	public double getJaccard() {
		if ((tp + fn + fp) == 0)
			return 0;
		return tp / (tp + fn + fp);
	}

	@Override
	public String toString() {
		return "PRF1 [tp=" + tp + ", fp=" + fp + ", fn=" + fn + ", getF1()=" + getF1() + ", getRecall()=" + getRecall()
				+ ", getPrecision()=" + getPrecision() + ", getJaccard()=" + getJaccard() + "]";
	}

}