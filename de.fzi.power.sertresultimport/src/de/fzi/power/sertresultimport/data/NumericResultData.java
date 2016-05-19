package de.fzi.power.sertresultimport.data;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import javax.measure.unit.Unit;

/**
 * Objects of this class are used to save the results of an interval run of one
 * specific provider (e.g. cpu utilization). This class stores the actual data
 * and does not have any sub results.
 * 
 * @author Daniel Hassler
 *
 */
public final class NumericResultData extends AbstractResultData {

	/**
	 * Creates NumericResultData objects with a specific name.
	 * @param name
	 *            the name of the result data object
	 */
	public NumericResultData(String name) {
		super(name);
	}

	private double max;
	private double min;
	private double avg;
	private int badSamples;

	private Unit<?> unit;
	private List<Double> rawValues = new ArrayList<Double>();

	public Unit<?> getUnit() {
		return unit;
	}

	public void setUnit(Unit<?> unit) {
		this.unit = unit;
	}

	public List<Double> getRawValues() {
		return rawValues;
	}

	public void setRawValues(List<Double> rawValues) {
		this.rawValues = rawValues;
	}

	public double getMaximum() {
		return max;
	}

	public void setMaximum(double max) {
		this.max = max;
	}

	public double getMinimum() {
		return min;
	}

	public void setMinimum(double min) {
		this.min = min;
	}

	public double getAverage() {
		return avg;
	}

	public void setAverage(double avg) {
		this.avg = avg;
	}

	public void setBadSamples(int badSamples) {
		this.badSamples = badSamples;
	}

	public int getBadSamples() {
		return this.badSamples;
	}

	@Override
	public String toString() {
		return "Unit: " + unit + ", minimum" + min + ", maximum" + max + ", average" + avg + ", total samples: "
				+ rawValues.size();
	}

	/**
	 * This would return the sub results but in this case it returns an empty
	 * list, because there are not sub results.
	 * @return an empty list.
	 */
	@Override
	public List<? extends AbstractResultData> getSubResults() {
		return Collections.emptyList();
	}

}
