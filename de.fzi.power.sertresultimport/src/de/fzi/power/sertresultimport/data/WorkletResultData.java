package de.fzi.power.sertresultimport.data;

import java.util.ArrayList;

import java.util.List;

/**
 * Objects of this class are used to save the results of a single worklet run
 * (e.g. compression algorithm). A worklet consists of several interval runs.
 * 
 * @author Daniel Hassler
 *
 */
public class WorkletResultData extends AbstractResultData {

	private List<IntervalResultData> subResults;

	/**
	 * Creates WorkletResultData objects with a specific name and a list of sub
	 * results.
	 * 
	 * @param name
	 *            the name of the result data object
	 */
	public WorkletResultData(String name) {
		super(name);
		subResults = new ArrayList<IntervalResultData>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IntervalResultData> getSubResults() {
		return subResults;
	}
}
