package de.fzi.power.sertresultimport.importer.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.signedcontent.InvalidContentException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.fzi.power.sertresultimport.data.AbstractResultData;
import de.fzi.power.sertresultimport.data.IntervalResultData;
import de.fzi.power.sertresultimport.data.NumericResultData;
import de.fzi.power.sertresultimport.data.SuiteResultData;
import de.fzi.power.sertresultimport.data.TimingData;
import de.fzi.power.sertresultimport.data.WorkletResultData;
import de.fzi.power.sertresultimport.data.WorkloadResultData;

/**
 * This class parses an given XML file and stores the information into a
 * SuiteResultData object. Specific parsers can be registered that extract
 * sensor information from the results of the measurement phases of the
 * benchmark.
 * 
 * @author Daniel Hassler
 */
public class XmlResultParser implements IImport {

	private final XPath xpath;
	private static final String WORKLET_DESCRIPTION = "worklet";
	private static final String NAME_ATTRIBUTE_DESCRIPTION = "name";
	private static final String SUITE_DESCRIPTION = "suite";
	private static final String SUITE_WORKLOAD_XPATH = "/suite/workload";
	private static final String MEASUREMENT_INTERVAL_XPATH = "phase[@type='measurement']/sequence/interval";
	
	private List<IXmlProviderResultParser<NumericResultData>> specificResultParsers;
	private final XmlTimingParser timingParser;

	private static final int SERT_MAGIC_BAD_SAMPLE_IDENTIFIER = -2;
	private boolean discardBadSamples;
	private boolean filterBadSamples;
	private IProgressMonitor monitor;
	

	/**
	 * Creates a new {@link XmlResultParser} object.
	 */
	public XmlResultParser() {
		specificResultParsers = new ArrayList<IXmlProviderResultParser<NumericResultData>>();
		xpath = XPathFactory.newInstance().newXPath();
		timingParser = new XmlTimingParser();
		discardBadSamples = false;
		filterBadSamples = true;
	}

	/**
	 * Returns the registered results parsers.
	 * 
	 * @return the registered specific results parsers
	 */
	public List<IXmlProviderResultParser<NumericResultData>> getProviderResultParsers() {
		return specificResultParsers;
	}

	/**
	 * The parsers set with this method are used by the parseResults method to
	 * extract specific measures.
	 * 
	 * @param parsers
	 *            sets the specific results parsers
	 */
	public void setProviderResultParsers(final List<IXmlProviderResultParser<NumericResultData>> parsers) {
		this.specificResultParsers = parsers;
	}

	/**
	 * This method parses the given xml file and uses the registered specific
	 * results parser to extract measurement information of specific sensors.
	 * 
	 * @param filepath
	 *            the path to the xml results file
	 * @return an SuiteResultData object that contains the result of a benchmark
	 *         suite run
	 * @throws InvalidContentException
	 *             if the XML content is malformed
	 */
	public SuiteResultData parseResults(final Path filepath) throws InvalidContentException {
		return parseResults(filepath, null);
	}
	/**
	 * This method parses the given xml file and uses the registered specific
	 * results parser to extract measurement information of specific sensors.
	 * 
	 * @param monitor 
	 * 			  an IProgressMonitor to indicate import progress
	 * @param filepath
	 *            the path to the xml results file
	 * @return an SuiteResultData object that contains the result of a benchmark
	 *         suite run
	 * @throws InvalidContentException
	 *             if the XML content is malformed
	 */
	public SuiteResultData parseResults(final Path filepath, IProgressMonitor monitor) throws InvalidContentException {
		final InputSource inputSource = new InputSource(filepath.toAbsolutePath().toString());
		final SuiteResultData suiteResult = new SuiteResultData(SUITE_DESCRIPTION);
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		this.monitor = monitor;
		if (monitor != null) {
			monitor.beginTask("Parsing ", IProgressMonitor.UNKNOWN);
		}
		try {
			final NodeList workloads = (NodeList) xpath.evaluate(SUITE_WORKLOAD_XPATH, inputSource,
					XPathConstants.NODESET);
			final List<Future<AbstractResultData>> futureWorkloadData = new ArrayList<Future<AbstractResultData>>();
			for (int i = 0; i < workloads.getLength(); i++) {
				// Asynchronous method call by executor
				futureWorkloadData.add(executor.submit(new WorkloadParser(workloads.item(i))));
			}
			for (final Future<AbstractResultData> future : futureWorkloadData) {
				// Synchronous call to get results, may block
				suiteResult.getSubResults().add((WorkloadResultData) future.get());
			}

		} catch (XPathExpressionException e) {
			throw new InvalidContentException("Error parsing results XML" + e.getMessage(), e);
		} catch (ExecutionException e) {
			throw new InvalidContentException("Error parsing results XML" + e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Got interrupted while parsing XML" + e.getMessage());
		}
		if (monitor != null) {
			monitor.subTask("Done with XML import");
			monitor.done();
		}
		executor.shutdown();
		return suiteResult;
	}

	/**
	 * Returns the setting for discarding bad samples.
	 * @return true if bad samples are going to be discarded.
	 */
	public boolean isDiscardBadSamples() {
		return discardBadSamples;
	}

	/**
	 * With this flag set the parser discards bad samples from the result.
	 * @param discardBadSamples true if bad samples should be discarded.
	 */
	public void setDiscardBadSamples(boolean discardBadSamples) {
		this.discardBadSamples = discardBadSamples;
	}

	/**
	 * Returns the setting for filtering bad samples.
	 * @return true if bad samples are going to be filtered out.
	 */
	public boolean isFilterBadSamples() {
		return filterBadSamples;
	}
	
	/**
	 * With this flag set the parser filteres bad samples out of the result. 
	 * @param filterBadSamples true if bad samples should be filtered.
	 */
	public void setFilterBadSamples(boolean filterBadSamples) {
		this.filterBadSamples = filterBadSamples;
	}
	
	/**
	 * This class parses workload data.
	 *
	 */
	private class WorkloadParser implements Callable<AbstractResultData> {

		private Node node;
		
		WorkloadParser(Node node) {
			this.node = node;
		}

		public Node getNode() {
			return node;
		}

		@Override
		public AbstractResultData call() throws XPathExpressionException, InterruptedException, ExecutionException {
			final String workloadName = getNode().getAttributes().getNamedItem(NAME_ATTRIBUTE_DESCRIPTION)
					.getNodeValue();
			final WorkloadResultData workloadResult = new WorkloadResultData(workloadName);
			final NodeList childs = getNode().getChildNodes();
			final List<Future<AbstractResultData>> futureWorkletData = new ArrayList<Future<AbstractResultData>>();
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			for (int j = 0; j < childs.getLength(); j++) {
				final Node worklet = childs.item(j);
				if (worklet.getNodeName().equals(WORKLET_DESCRIPTION)) {
					futureWorkletData.add(executor.submit(new WorkletParser(worklet)));
				}
			}
			if (monitor != null) {
				monitor.subTask("Workload " + workloadName);
			}
			for (final Future<AbstractResultData> future : futureWorkletData) {
				workloadResult.getSubResults().add((WorkletResultData) future.get());
			}	
			executor.shutdown();
			return workloadResult;
		}
	}

	/**
	 * This class parses worklet data.
	 *
	 */
	private class WorkletParser extends WorkloadParser {
		
		WorkletParser(Node node) {
			super(node);
		}
		
		@Override
		public WorkletResultData call() throws XPathExpressionException, InterruptedException, ExecutionException {
			// only read measurement results (not from calibration or warmup)
			final String workletName = getNode().getAttributes().getNamedItem(NAME_ATTRIBUTE_DESCRIPTION)
					.getNodeValue();
			final WorkletResultData workletResult = new WorkletResultData(workletName);
			final NodeList intervals = (NodeList) xpath.evaluate(MEASUREMENT_INTERVAL_XPATH, getNode(),
					XPathConstants.NODESET);
			final List<Future<AbstractResultData>> futureIntervalData = new ArrayList<Future<AbstractResultData>>();
			final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for (int k = 0; k < intervals.getLength(); k++) {
				futureIntervalData.add(executor.submit(new IntervalParser(intervals.item(k))));
			}
			if (monitor != null) {
				monitor.subTask("Worklet: " + workletName);
			}
			for (final Future<AbstractResultData> future : futureIntervalData) {
				// Synchronous call to get results, may block
				workletResult.getSubResults().add((IntervalResultData) future.get());	
			}
			if (monitor != null) {
				monitor.worked(1);
			}
			executor.shutdown();
			return workletResult;
		}
	}

	/**
	 * This class parses interval data.
	 *
	 */
	private class IntervalParser extends WorkloadParser {

		IntervalParser(Node node) {
			super(node);
		}

		@Override
		public IntervalResultData call() throws XPathExpressionException {
			final String intervalName = getNode().getAttributes().getNamedItem(NAME_ATTRIBUTE_DESCRIPTION)
					.getNodeValue();
			final IntervalResultData intervalResult = new IntervalResultData(intervalName);
			List<NumericResultData> toAdd = new ArrayList<NumericResultData>();
			for (final IXmlProviderResultParser<NumericResultData> parser : getProviderResultParsers()) {
				List<NumericResultData> data = parser.parse(getNode());
				if (data.isEmpty()) {
					toAdd.clear();
					break;
				} else {
					toAdd.addAll(check_for_invalid_samples(data));
				}
			}
			intervalResult.getSubResults().addAll(toAdd);
			final List<TimingData> timings = timingParser.parse(getNode());
			if (timings != null && !timings.isEmpty()) {
				// each interval only contains one TimingData object
				intervalResult.setTimings(timings.get(0));
			}
			return intervalResult;
		}

		private List<NumericResultData> check_for_invalid_samples(List<NumericResultData> dataSet) {
			List<NumericResultData> newDataSet = new ArrayList<NumericResultData>();
			for (NumericResultData data : dataSet) {
				if (data.getBadSamples() == 0 || (!filterBadSamples && !discardBadSamples)) {
					newDataSet.add(data);
				} else if (!discardBadSamples && filterBadSamples) {
					List<Double> rawValues = data.getRawValues();
					List<Double> newRawValues = new ArrayList<Double>();
					for (double value : rawValues) {
						// this is bad because it assumes that negatives values are invalid
						if (value >= 0 ) {
							newRawValues.add(value);
						}
					}
					data.setRawValues(newRawValues);
					newDataSet.add(data);
				}
			}
			return newDataSet;
		}
	}

}
