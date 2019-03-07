import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

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

public class TraceXMLGenerator {

	String basePath = ".";
	DocumentBuilderFactory documentFactory;
	DocumentBuilder documentBuilder;
	Document document;

	public void init() throws ParserConfigurationException {
		documentFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	public void generate(Map<String, ArrayList<String>> configurations, String netID)
			throws ParserConfigurationException, TransformerException {
		init();

		// root elements
		Element root = document.createElement("log");
		document.appendChild(root);

		Attr xesversion = document.createAttribute("xes.version");
		xesversion.setValue("1.0");
		root.setAttributeNode(xesversion);

		Attr features = document.createAttribute("xes.features");
		features.setValue("nested-attributes");
		root.setAttributeNode(features);

		Attr oxesversion = document.createAttribute("openxes.version");
		oxesversion.setValue("nested-attributes");
		root.setAttributeNode(oxesversion);

		Attr xmlns = document.createAttribute("xmlns");
		xmlns.setValue("http://www.xes-standard.org/");
		root.setAttributeNode(xmlns);

		// trace
		for (Entry<String, ArrayList<String>> e : configurations.entrySet()) {
			Element trace = document.createElement("trace");
			root.appendChild(trace);
			Element keyval = keyval(e.getKey());
			trace.appendChild(keyval);
			for (String feat : e.getValue()) {
				trace.appendChild(getEvent(feat));
			}

		}

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new File(basePath + "/" + netID + ".xes"));

		// If you use
		// StreamResult result = new StreamResult(System.out);
		// the output will be pushed to the standard output ...
		// You can use that for debugging

		transformer.transform(domSource, streamResult);

		System.out.println("Done creating " + netID + " XML-XES File");

	}
	// <event>
	// <string key="concept:name" value="request_received"/>
	// </event>

	public Element getEvent(String name) {
		Element e = document.createElement("event");
		Element v = keyval(name);
		e.appendChild(v);
		return e;

	}

	public Element keyval(String name) {
		Element v = document.createElement("string");

		Attr key = document.createAttribute("key");
		key.setValue("concept:name");
		v.setAttributeNode(key);

		Attr value = document.createAttribute("value");
		value.setValue(name);
		v.setAttributeNode(value);
		return v;
	}
}
