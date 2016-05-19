package de.fzi.power.sertresultimport.data;

import java.util.ArrayList;

import java.util.List;

/**
 * Objects of this class are used to save the results of a complete benchmark
 * suite. A suite consists of at at least one workload.
 * 
 * @author Daniel Hassler
 *
 */
public class SuiteResultData extends AbstractResultData {

	private List<WorkloadResultData> subResults;

	/**
	 * Creates SuiteResultData objects with a specific name and a list of sub
	 * results.
	 * 
	 * @param name
	 *            the name of the result data object
	 */
	public SuiteResultData(String name) {
		super(name);
		subResults = new ArrayList<WorkloadResultData>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WorkloadResultData> getSubResults() {
		return subResults;
	}

}
