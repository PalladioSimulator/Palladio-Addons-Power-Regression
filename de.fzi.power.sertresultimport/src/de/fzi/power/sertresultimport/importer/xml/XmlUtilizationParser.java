package de.fzi.power.sertresultimport.importer.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.fzi.power.sertresultimport.SertResultConstants;
import de.fzi.power.sertresultimport.data.NumericResultData;
import de.fzi.power.sertresultimport.importer.xml.util.XmlParserUtils;

/**
 * {@inheritDoc}
 * 
 * This parser extracts the CPU utilization information from the benchmark
 * results.
 */
public class XmlUtilizationParser implements IXmlProviderResultParser<NumericResultData> {
	private final SertResultConstants.Type cpuLoadType = SertResultConstants.Type.CPULOAD;
	private final XPath xpath;

	/**
	 * Creates a new {@link XmlUtilizationParser} object.
	 */
	public XmlUtilizationParser() {
		xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<NumericResultData> parse(final Node interval) throws XPathExpressionException {
		final List<NumericResultData> results = new ArrayList<NumericResultData>();
		final Node cpuLoadData = XmlParserUtils.extractProviderResults(interval, cpuLoadType.getName());
		if (cpuLoadData != null) {
			final NodeList accumulated_data = (NodeList) xpath.evaluate("percentage/*", cpuLoadData,
					XPathConstants.NODESET);
			final NumericResultData result = XmlParserUtils.extractProviderData(accumulated_data, cpuLoadType.getName());
			if (result != null) {
				result.setUnit(cpuLoadType.getUnit());
				results.add(result);
			}
		}
		return results;
	}

}
