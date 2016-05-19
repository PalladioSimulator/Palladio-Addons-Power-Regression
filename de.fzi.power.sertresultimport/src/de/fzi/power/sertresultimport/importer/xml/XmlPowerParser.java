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
 * This parser extracts the power consumption information from the benchmark results.
 */
public class XmlPowerParser implements IXmlProviderResultParser<NumericResultData> {

    private XPath xpath;

    /**
     * Creates a new {@link XmlPowerParser} object.
     */
    public XmlPowerParser() {
        xpath = XPathFactory.newInstance().newXPath();

    }

    /**
	 * {@inheritDoc}
	 */
    @Override
    public List<NumericResultData> parse(final Node interval) throws XPathExpressionException {
        final List<NumericResultData> results = new ArrayList<NumericResultData>();
        final Node power_data = XmlParserUtils.extractProviderResults(interval, "power-analyzer");
        if (power_data != null) {
                for (final SertResultConstants.Type type : SertResultConstants.Type.values()) {
                    final NumericResultData result = extract_subresult(type.getName(), power_data);
                    if (result != null) {
                        result.setUnit(type.getUnit());
                        results.add(result);
                    }
                }
        }
        return results;
    }

    private NumericResultData extract_subresult(final String typeName, final Node data) throws XPathExpressionException {
        final NodeList subResultList = (NodeList) xpath.evaluate(typeName + "/*", data, XPathConstants.NODESET);
        return XmlParserUtils.extractProviderData(subResultList, typeName);
    }
    
    

}
