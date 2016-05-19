package de.fzi.power.sertresultimport.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Objects of this class are used to save the results of an interval run. One
 * interval run consists of at least one provider result and the timing
 * information of this specific interval run.
 * 
 * @author Daniel Hassler
 *
 */
public final class IntervalResultData extends AbstractResultData {

	private List<NumericResultData> subResults;
	private TimingData timings;

	/**
	 * Creates IntervalResultData objects with a specific name and a list of sub
	 * results.
	 * 
	 * @param name
	 *            the name of the result data object
	 */
	public IntervalResultData(String name) {
		super(name);
		subResults = new ArrayList<NumericResultData>();
	}

	@Override
	public List<NumericResultData> getSubResults() {
		return subResults;
	}

	/**
	 * This returns the timing information of the interval run.
	 * 
	 * @return a TimingData object containing the timing information.
	 */
	public TimingData getTimings() {
		return timings;
	}

	/**
	 * This sets the timing information of the interval run.
	 * 
	 * @param timing
	 *            a TmingData object to be set as timing information for this
	 *            run.
	 */
	public void setTimings(TimingData timing) {
		this.timings = timing;
	}

}
