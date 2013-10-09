/**
 * 
 */
package ac.technion.iem.ontobuilder.matching.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;

/**
 * @author Tomer Sagi
 * This class provides facilities to edit the algorithm.xml file
 *
 */
public class AlgorithmXMLEditor {

	public static void updateAlgorithmParams(String algorithmName,
			HashMap<String, Double> parameterValues) throws Exception {
		File file = new File(OntoBuilderResources.Config.Matching.ALGORITHMS_XML);
		BufferedReader reader = new BufferedReader(new FileReader(file));
        SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
        builder.setEntityResolver(new NetworkEntityResolver());
        Document doc = builder.build(reader);
		Element root = doc.getRootElement();

		List<Element> algorithmsList = root.getChildren("algorithm");
		
		// find algorithm node
		Element algoNode = null;
		for (Element e : algorithmsList) 
		{
			if (e.getAttribute("name").getValue().equals(algorithmName))
				{
					algoNode = e;
					break;
				}
		}
		if (algoNode == null)
		{
			Exception e = new Exception("Algorithm not found");
			throw e;
		}
		// find parameter node
		Element parametersElement = algoNode.getChild("parameters");
		List<Element> algoParams = parametersElement.getChildren("parameter");
		for (String param : parameterValues.keySet())
		{
			boolean found = false;
			for (Element algoParam : algoParams)
			{
				Element paramName = (Element)algoParam.getContent().get(1);
				Text name = (Text)paramName.getContent().get(0);
				
				if (name.getText().equals(param))
				{
					found = true;
					Element paramValue = (Element)algoParam.getContent().get(3);
					Text value = (Text) paramValue.getContent().get(0);
					value.setText(parameterValues.get(param).toString());
				}
			}
			if (!found)
			{
				Exception e = new Exception("Parameter not found");
				throw e;
			}
			
		}
		
		//update algorithms.xml
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter();
        fmt.output(doc, out);
        out.close();
	}
	
	

}
