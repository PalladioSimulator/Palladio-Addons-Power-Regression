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
 * This parser extracts the temperature information from the benchmark results.
 */
public class XmlTemperatureParser implements IXmlProviderResultParser<NumericResultData> {

	private static final String TEMPERATURE_SENSOR_DESCRIPTION = "temperature-sensor";
	private final XPath xpath;
	private final SertResultConstants.Type tempType = SertResultConstants.Type.TEMPERATURE;

	/**
	 * Creates a new Creates a new {@link XmlTemperatureParser} object.
	 */
	public XmlTemperatureParser() {
		xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NumericResultData> parse(final Node interval) throws XPathExpressionException {
		final List<NumericResultData> results = new ArrayList<NumericResultData>();
		NumericResultData result = null;

		final Node temperature_data = XmlParserUtils.extractProviderResults(interval, TEMPERATURE_SENSOR_DESCRIPTION);
		if (temperature_data != null) {
			final NodeList accumulated_data = (NodeList) xpath.evaluate(tempType.getName() + "/*", temperature_data,
					XPathConstants.NODESET);
			result = XmlParserUtils.extractProviderData(accumulated_data, tempType.getName());
			if (result != null) {
				result.setUnit(SertResultConstants.Type.TEMPERATURE.getUnit());
			}
		}
		results.add(result);
		return results;
	}
}
