package us.es.idea.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XTrace;

import PetriNet.LPO.PetriNetLpo;
import us.es.idea.utils.XesUtils;

public class test {
	
	public static final String LOCALLPOS = "/resources/lpos/four/";
	public static final String LOCALXES =  "/resources/xes/four_test.xes";

	public static void main(String[] args) throws Exception {
		
		List<PetriNetLpo> lpos = new ArrayList<>();
		
		String runPath = test.class.getResource(test.LOCALLPOS).getPath();
		
		for (File f1 : new File(runPath).listFiles()){
            lpos.add(new PetriNetLpo(test.LOCALLPOS + f1.getName()));
        }
		
		System.out.println("********* LPOs **********");
		System.out.println(lpos);
		
		List<XTrace> traces = XesUtils.getXLog(test.LOCALXES);
		
		System.out.println("********* Traces **********");
		System.out.println(traces);

	}
	
}
