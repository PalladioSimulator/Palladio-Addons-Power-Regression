package de.fzi.power.sertresultimport.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Objects of this class are used to save the results of a single type of
 * workload (e.g. CPU intense workload). A workload consists of one or more
 * worklets.
 * 
 * @author Daniel Hassler
 *
 */
public class WorkloadResultData extends AbstractResultData {

	private List<WorkletResultData> subResults;

	/**
	 * Creates WorkloadResultData objects with a specific name and a list of sub
	 * results.
	 * 
	 * @param name
	 *            the name of the result data object
	 */
	public WorkloadResultData(String name) {
		super(name);
		subResults = new ArrayList<WorkletResultData>();
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
	public List<WorkletResultData> getSubResults() {
		return subResults;
	}

}
