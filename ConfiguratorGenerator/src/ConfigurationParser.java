import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.SPLXReader;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.questions.Sat4jValidConfigurationQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jValidProductQuestion;

public class ConfigurationParser {

	static final String configurationsPath = "./configurator/ERP-System/configurations.csv";
	static final String vmfile="./configurator/ERP-System/ERP-System.xml";
	public static void main(String[] args) throws Exception {

		Map<String, ArrayList<String>> allUserConf = new HashMap<String, ArrayList<String>>();

		try {
			Scanner scanner = new Scanner(new File(configurationsPath));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String user = line.substring(0, line.indexOf(";"));
				String feature = line.substring(line.indexOf(";") + 1);

				ArrayList<String> userconfigurations = allUserConf.get(user);
				if (userconfigurations == null) {
					userconfigurations = new ArrayList<String>();
				}
				userconfigurations.add(feature);
				allUserConf.put(user, userconfigurations);

				// System.out.println(user+";"+feature);
			}
			scanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		TraceXMLGenerator generator = new TraceXMLGenerator();
		generator.generate(allUserConf, "ERP");

		generator.generate(onlyValid(allUserConf), "ERP-VALID");
		generator.generate(onlyValidProduct(allUserConf), "ERP-VALIDPRODUCT");
	}

	private static Map<String, ArrayList<String>> onlyValid(Map<String, ArrayList<String>> allUserConf)
			throws Exception {
		SPLXReader reader = new SPLXReader();
		FAMAFeatureModel fm = (FAMAFeatureModel) reader.parseFile(vmfile);
		Sat4jReasoner r = new Sat4jReasoner();
		fm.transformTo(r);
		
		Map<String, ArrayList<String>> res = new HashMap<>();
		for (Entry<String, ArrayList<String>> e : allUserConf.entrySet()) {
			Configuration c = new Configuration();
			for(String featName:e.getValue()) {
				c.addElement(new Feature(featName), 1);
			}
			
			Sat4jValidConfigurationQuestion cvq= new Sat4jValidConfigurationQuestion();
			cvq.setConfiguration(c);
			r.ask(cvq);
			if(cvq.isValid()) {
				res.put(e.getKey(), e.getValue());
			}
		}

		return res;
	}

	private static Map<String, ArrayList<String>> onlyValidProduct(Map<String, ArrayList<String>> allUserConf)
			throws Exception {
		SPLXReader reader = new SPLXReader();
		FAMAFeatureModel fm = (FAMAFeatureModel) reader.parseFile("./configurator/ERP-System/ERP-System.xml");
		Sat4jReasoner r = new Sat4jReasoner();
		fm.transformTo(r);
		
		Map<String, ArrayList<String>> res = new HashMap<>();
		for (Entry<String, ArrayList<String>> e : allUserConf.entrySet()) {
			Configuration c = new Configuration();
			for(String featName:e.getValue()) {
				c.addElement(new Feature(featName), 1);
			}
			
			Sat4jValidProductQuestion cvprod = new Sat4jValidProductQuestion();
			Product p=c.getConfigurationAsProduct();
			cvprod.setProduct(p);
			
			r.ask(cvprod);
			if(cvprod.isValid()) {
				res.put(e.getKey(), e.getValue());
			}
		}

		return res;
	}
}
