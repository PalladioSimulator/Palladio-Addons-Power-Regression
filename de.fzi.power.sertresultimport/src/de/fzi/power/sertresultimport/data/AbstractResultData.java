package de.fzi.power.sertresultimport.data;

import java.util.List;

/**
 * This class represents an abstract form of a result of a benchmark measurement
 * phase. Actual result data classes should extend this class.
 * 
 * @author Daniel Hassler
 *
 */
public abstract class AbstractResultData {

	private String name;

	public AbstractResultData(String name) {
		setName(name);
	}

	/**
	 * Returns the name of the result data.
	 * 
	 * @return the name as a String object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the result data object.
	 * 
	 * @param name
	 *            the name as a String object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method should return the sub results of the current object.
	 * 
	 * @return a list of results or null if there are no sub results
	 */
	public abstract List<? extends AbstractResultData> getSubResults();

	@Override
	public String toString() {
		String str = name + System.getProperty("line.separator");
		for (Object subResult : getSubResults()) {
			str += " " + subResult.toString() + System.getProperty("line.separator");
		}
		return str;
	}
}
