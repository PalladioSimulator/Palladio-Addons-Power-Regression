package de.fzi.power.sertresultimport.exporter;

import java.io.IOException;

import de.fzi.power.sertresultimport.data.SuiteResultData;

/**
 * Classes that handle the export of suite result data should implement this
 * interface.
 * 
 * @author Daniel Hassler
 *
 */
public interface IExport {
	/**
	 * Exports the given SuiteResultData into a format specified by the
	 * implementing class.
	 * 
	 * @param suiteResult the data to be exported.
	 * @throws IOException if the results cannot be written.
	 */
	public void export(SuiteResultData suiteResult) throws IOException;
}
