package de.fzi.power.sertresultimport.importer.xml;

import java.nio.file.Path;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.signedcontent.InvalidContentException;

import de.fzi.power.sertresultimport.data.SuiteResultData;

/**
 * @author Daniel Hassler
 *
 */
public interface IImport {
	
	/**
	 * This method parses the given file and uses the registered specific
	 * results parser to extract measurement information of specific sensors.
	 * 
	 * @param filepath
	 *            the path to the results file
	 * @return an SuiteResultData object that contains the result of a benchmark
	 *         suite run
	 * @throws InvalidContentException
	 *             if the input content is malformed
	 */
	public SuiteResultData parseResults(final Path filepath, IProgressMonitor monitor) throws InvalidContentException;
}
