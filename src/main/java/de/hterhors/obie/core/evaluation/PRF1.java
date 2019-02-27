package de.hterhors.obie.core.evaluation;

/**
 * Container to store true positive, false psoitive and false negative. Able to
 * compute directly Precision, Recall, F1 Score and Jaccard.
 * 
 * @author hterhors
 *
 */

public class PRF1 {

	public double tp, fp, fn, tn;

	public PRF1() {
		tp = 0;
		fp = 0;
		fn = 0;
		tn = 0;
	}

	public PRF1(int tp, int fp, int fn) {
		this.tp = tp;
		this.fp = fp;
		this.fn = fn;
		this.tn = 0;
	}

	public PRF1(int tp, int fp, int fn, int tn) {
		this.tp = tp;
		this.fp = fp;
		this.fn = fn;
		this.tn = tn;
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
		this.tn = c.tn;
	}

	public PRF1 add(PRF1 adder) {
		this.tp += adder.tp;
		this.fp += adder.fp;
		this.fn += adder.fn;
		this.tn += adder.tn;
		return this;
	}

	public PRF1 set(PRF1 setter) {
		this.tp = setter.tp;
		this.fp = setter.fp;
		this.fn = setter.fn;
		this.tn = setter.tn;
		return this;
	}

	public double getF1() {
		final double p = getPrecision();
		final double r = getRecall();
		final double d = (p + r);
		return d == 0 ? 0 : (2 * p * r) / d;
	}

	public double getAccuracy() {
		double d = (tp + tn + fp + fn);
		
		if (d == 0)
			return 0;
	
		return (tp + tn) / (tp + tn + fp + fn);
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
		return "PRF1 [tp=" + tp + ", fp=" + fp + ", fn=" + fn + ", tn=" + tn + ", getF1()=" + getF1()
				+ ", getAccuracy()=" + getAccuracy() + ", getRecall()=" + getRecall() + ", getPrecision()="
				+ getPrecision() + ", getJaccard()=" + getJaccard() + "]";
	}

}