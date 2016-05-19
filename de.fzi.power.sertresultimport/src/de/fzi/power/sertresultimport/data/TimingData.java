package de.fzi.power.sertresultimport.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TimingData extends AbstractResultData {

	private Date start;
	private Date end;

	/**
	 * Creates TimingData objects with a specific name and timing information.
	 * The timing data does not contain a sub result.
	 * 
	 * @param name
	 *            the name of the result data object
	 */
	public TimingData(String name) {
		super(name);
		start = new Date();
		end = new Date();
	}

	/**
	 * Returns the start time.
	 * 
	 * @return the end time as a Date object.
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Sets the start time.
	 * 
	 * @param start
	 *            the start time as a Date object.
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * Returns the end time.
	 * 
	 * @return the end time as a Date object
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Sets the end time.
	 * 
	 * @param start
	 *            the end time as a Date object.
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Start: " + start.toString() + ", End: " + end.toString();
	}

	/**
	 * This method would return the sub results but in this case there are none.
	 * @return an empty list because timing data does not contain any sub
	 *         results
	 */
	@Override
	public List<? extends AbstractResultData> getSubResults() {
		return Collections.emptyList();
	}
}
