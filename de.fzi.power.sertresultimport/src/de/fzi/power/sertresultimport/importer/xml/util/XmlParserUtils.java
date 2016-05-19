package de.fzi.power.sertresultimport.importer.xml.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.fzi.power.sertresultimport.data.NumericResultData;

/**
 * This is a utility class used by several other classes in the project.
 * 
 * @author Daniel Hassler
 *
 */
public class XmlParserUtils {


	/**
	 * This methdo extracts the provider results from the XML of a provider specified by the providerName.
	 * @param node the Node object of the XML document.
	 * @param providerName the name of the provider from which the results should be extracted.
	 * @return the Node which contains the provider data.
	 * @throws XPathExpressionException if the XML document is malformed.
	 */
    public static Node extractProviderResults(Node node, String providerName) throws XPathExpressionException{
        XPath xpath = XPathFactory.newInstance().newXPath();
        String eval = "result/metrics/provider/" + providerName + "/measurement";
        Node providerResult = null;
        try {
        	providerResult = (Node) xpath.evaluate(eval, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
			// if this did not work look in subset
        }
		if (providerResult == null) {
			eval = "result/metrics/subset/provider/" + providerName + "/measurement";
			providerResult = (Node) xpath.evaluate(eval, node, XPathConstants.NODE);
		}
  
        return providerResult;
    }
    
    /**
     * This method extracts the actual provider data from a list of Node objects.
     * @param data the NodeList object which contains the data.
     * @param providerName the name of the specified provider.
     * @return the data in a NumericResultData object.
     */
    public static NumericResultData extractProviderData(NodeList data, String providerName) {
        if (data == null || data.getLength() == 0) {
            return null;
        }
        NumericResultData result = new NumericResultData(providerName);
        for (int i = 0; i < data.getLength(); i++) {
            Node accumulated = data.item(i);
            String content = accumulated.getTextContent();
            switch (accumulated.getNodeName()) {
            case "maximum":
                result.setMaximum(Double.valueOf(content));
                break;
            case "minimum":
                result.setMinimum(Double.valueOf(content));
                break;
            case "average":
                result.setAverage(Double.valueOf(content));
                break;
            case "raw":
                result.setRawValues(extractDoubleList(content));
                break;
            case "bad-samples":
            	result.setBadSamples(Integer.valueOf(content));
            case "default":
                // not supported (yet)
                break;
            }
        }
        return result;
    }

    /**
     * This takes a String which contains double values that are separated by a comma 
     * and creates a list of doubles from that string.
     * @param strList the string containing the values.
     * @return a List<Double> object which contains the values.
     */
    public static List<Double> extractDoubleList(String strList) {
        List<Double> list = new ArrayList<Double>();
        if (strList != null && strList.length() > 2) {
            String tmp = strList.substring(1, strList.length() - 2);
            String[] strRawValues = tmp.split(",");
            for (String strRawValue : strRawValues) {
                list.add(Double.valueOf(strRawValue));
            }
        }
        return list;
    }

    /**
     * This method reduces a list of doubles values with m entries into
     * a list with newSize entries. This is done by averaging the old 
     * values into the new list.
     * @param values the old list.
     * @param newSize the size of the new list.
     * @return the new list.
     */
    public static List<Double> reduceTo(List<Double> values, int newSize) {
        if (newSize >= values.size()) {
            return values;
        } else {
            List<Double> newList = new ArrayList<Double>();
            double n = values.size() / newSize;
            int i = 0;
            double sum = 0;
            for (Double val : values) {
                if (i < n) {
                    sum += val;
                    i++;
                } else {
                    newList.add(sum / n);
                    sum = 0;
                    i = 0;
                }
            }
            if (i > 0) {
                newList.add(sum / (double) i);
            }
            assert(newSize == newList.size());
            return newList;	
        }
    }
}
