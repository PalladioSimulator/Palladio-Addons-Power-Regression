package de.fzi.power.sertresultimport.importer.xml;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import de.fzi.power.sertresultimport.data.AbstractResultData;

/**
 * This interface should be implemented by specific result providers. A result providers reads the
 * measured data of the "sensors" of the SuT, e.g. the power consumption or CPU utilization.
 * 
 * @author Daniel Hassler
 *
 */
public interface IXmlProviderResultParser<T extends AbstractResultData> {
    /**
     * This method parses the interval node of the results XML file, extracts specific data and
     * returns it as a list.
     * 
     * @param interval
     *            The interval sub node of the results XML file
     * @return the specific result data
     * @throws XPathExpressionException if the XML is malformed.
     */
    public List<T> parse(Node interval) throws XPathExpressionException;
}
