import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;

public class NetXMLGenerator {

	String basePath = ".";
	DocumentBuilderFactory documentFactory;
	DocumentBuilder documentBuilder;
	Document document;

	public void init() throws ParserConfigurationException {
		documentFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	public void generate(List<Feature> configurations, String netID)
			throws ParserConfigurationException, TransformerException {
		init();

		// root elements
		Element root = document.createElement("pnml");
		document.appendChild(root);

		Element lpo = document.createElement("lpo");
		root.appendChild(lpo);

		Attr id = document.createAttribute("id");
		id.setValue(netID);
		lpo.setAttributeNode(id);

		Attr type = document.createAttribute("type");
		type.setValue("http://www.pnml.org/version-2009/grammar/ptnet");
		lpo.setAttributeNode(type);

		// Define model name

		Element name = createName("valor");
		lpo.appendChild(name);

		// Create Events

		for (Feature feat : configurations) {
			Element event = document.createElement("event");

			Attr eventId = document.createAttribute("id");
			eventId.setValue(feat.getName());
			event.setAttributeNode(eventId);

			Element confName = createName(feat.getName());
			event.appendChild(confName);
			lpo.appendChild(event);
		}

		// Create arcs
		for (int i = 0; i < configurations.size() - 1; i++) {

			Feature sFeat = configurations.get(i);
			Feature dFeat = configurations.get(i + 1);

			Element arc = document.createElement("lpoArc");

			Attr eventId = document.createAttribute("id");
			eventId.setValue(sFeat.getName() + "." + dFeat.getName());
			arc.setAttributeNode(eventId);

			Attr source = document.createAttribute("source");
			source.setValue(sFeat.getName());
			arc.setAttributeNode(source);

			Attr target = document.createAttribute("target");
			target.setValue(dFeat.getName());
			arc.setAttributeNode(target);

			lpo.appendChild(arc);
		}

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new File(basePath + "/" + netID + ".lpo"));

		// If you use
		// StreamResult result = new StreamResult(System.out);
		// the output will be pushed to the standard output ...
		// You can use that for debugging

		transformer.transform(domSource, streamResult);

		System.out.println("Done creating " + netID + " XML-LPO File");

	}

	private Element createName(String valor) {
		Element name = document.createElement("name");
		Element value = document.createElement("value");

		value.appendChild(document.createTextNode(valor));
		name.appendChild(value);

		return name;
	}
}
