package de.fzi.power.sertresultimport.exporter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;

import de.fzi.power.sertresultimport.data.IntervalResultData;
import de.fzi.power.sertresultimport.data.NumericResultData;
import de.fzi.power.sertresultimport.data.SuiteResultData;
import de.fzi.power.sertresultimport.data.WorkletResultData;
import de.fzi.power.sertresultimport.data.WorkloadResultData;
import de.fzi.power.sertresultimport.importer.xml.util.XmlParserUtils;

/**
 * This class exports the results of a benchmark suite run into a CSV file.
 * 
 * @author Daniel Hassler
 *
 */
public class CSVExport implements IExport {

	private Path path;

	/**
	 * Sets the path to the directory where CSV data will be written.
	 * 
	 * @param file
	 *            the path to the target directory
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Returns the path to the CSV directory into which the CSV data will be
	 * written.
	 * 
	 * @return the path to the target directory
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export(SuiteResultData suiteResult) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException("Filename not set correctly");
		}

		for (WorkloadResultData workloadResult : suiteResult.getSubResults()) {
			FileWriter fileWriter = null;
			for (WorkletResultData workletResult : workloadResult.getSubResults()) {
				Path csvPath = path.resolve(workletResult.getName() + ".csv");
				StringBuilder str = new StringBuilder();
				ProviderResultLine resultsLine = null;
				for (IntervalResultData intervalResult : workletResult.getSubResults()) {
					resultsLine = new ProviderResultLine(intervalResult.getSubResults());
					Iterator<String> entryIter = resultsLine.iterator();
					while (entryIter.hasNext()) {
						str.append(entryIter.next());
					}
				}
				fileWriter = new FileWriter(csvPath.toFile());
				if (resultsLine != null) {
					fileWriter.append(resultsLine.getHeader());
				}
				fileWriter.append(str);
				fileWriter.flush();
			}
			fileWriter.close();
		}
	}

	/**
	 * This class returns the given result data in CSV format.
	 */
	private class ProviderResultLine implements Iterable<String> {
		private final Map<Unit<?>, List<Double>> rawValues;
		private final List<NumericResultData> results;
		private final List<Iterator<Double>> iterators;
		private static final char ENTRY_SEPERATOR = ';';
		private static final char ENDLINE = '\n';

		public ProviderResultLine(List<NumericResultData> results) {
			this.results = results;
			this.iterators = new ArrayList<Iterator<Double>>(results.size());
			this.rawValues = new HashMap<Unit<?>, List<Double>>();
			reduce_to_same_size();
		}

		@Override
		public Iterator<String> iterator() {
			reset_iterators();
			return new ProviderResultLineIterator();
		}

		public String getHeader() {
			String str = "";
			Iterator<Unit<?>> headerIt = rawValues.keySet().iterator();
			while (headerIt.hasNext()) {
				str += headerIt.next().toString();
				if (headerIt.hasNext()) {
					str += " " + ENTRY_SEPERATOR + " ";
				}
			}
			str += '\n';
			str = str.replace('%', 'P');
			return str;
		}

		private void reduce_to_same_size() {
			int min = Integer.MAX_VALUE;
			for (NumericResultData providerResult : results) {
				min = Math.min(min, providerResult.getRawValues().size());
			}
			for (NumericResultData providerResult : results) {
				List<Double> values = XmlParserUtils.reduceTo(providerResult.getRawValues(), min);
				Unit<?> unit = providerResult.getUnit();
				if (unit != null) {
					if (rawValues.get(unit) == null) {
						rawValues.put(unit, values);
					} else {
						rawValues.get(unit).addAll(values);
					}
				}
			}
		}

		private void reset_iterators() {
			for (List<Double> vals : rawValues.values()) {
				iterators.add(vals.iterator());
			}
		}
		
		private class ProviderResultLineIterator implements Iterator<String> {
			@Override
			public boolean hasNext() {
				boolean hasNext = !iterators.isEmpty();
				for (Iterator<Double> it : iterators) {
					hasNext = hasNext && it.hasNext();
				}
				return hasNext;
			}

			@Override
			public String next() {
				java.lang.String str = "";
				Iterator<Iterator<Double>> iterIt = iterators.iterator();
				while (iterIt.hasNext()) {
					Iterator<Double> it = iterIt.next();
					if (it.hasNext()) {
						str += it.next();
						if (iterIt.hasNext()) {
							str += " " + ENTRY_SEPERATOR + " ";
						} else {
							str += ENDLINE;
						}
					}
				}
				return str;
			}

			@Override
			public void remove() {
				for (Iterator<Double> it : iterators) {
					it.remove();
				}
			}

		}

	}
}
