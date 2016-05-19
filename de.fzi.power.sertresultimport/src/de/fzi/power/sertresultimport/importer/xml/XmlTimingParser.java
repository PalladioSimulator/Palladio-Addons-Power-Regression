package de.fzi.power.sertresultimport.importer.xml;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.fzi.power.sertresultimport.data.TimingData;

public class XmlTimingParser implements IXmlProviderResultParser<TimingData> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TimingData> parse(final Node interval) throws XPathExpressionException {

		List<TimingData> result = new ArrayList<TimingData>();
		final XPath xpath = XPathFactory.newInstance().newXPath();
		final String eval = "result/metrics/provider/timing/interval/*";
		final NodeList timings = (NodeList) xpath.evaluate(eval, interval, XPathConstants.NODESET);
		if (timings != null && timings.getLength() > 0) {
			TimingData timingData = new TimingData("timing");
			for (int i = 0; i < timings.getLength(); i++) {
				Node timing = timings.item(i);
				try {
					Calendar calendar;
					switch (timing.getNodeName()) {
					case "measurement-starting":
						calendar = DatatypeConverter.parseDateTime(timing.getTextContent());
						timingData.setStart(calendar.getTime());
						break;
					case "measurement-ended":
						calendar = DatatypeConverter.parseDateTime(timing.getTextContent());
						timingData.setEnd(calendar.getTime());
						break;
					}
				} catch (DOMException e) {
					throw new RuntimeException("Could not convert timing information" + e.getMessage());
				}
				result.add(timingData);
			}
		}
		return result;
	}

}
