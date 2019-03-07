import java.util.ArrayList;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.OrderingHeuristics.OrderingHeuristic;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.SPLXReader;

public class ModelParser {

	public static void main(String[] args) throws Exception {

		SPLXReader reader = new SPLXReader();
		FAMAFeatureModel fm = (FAMAFeatureModel) reader.parseFile("./configurator/ERP-System/ERP-System.xml");

		NetXMLGenerator writer = new NetXMLGenerator();
		writer.basePath="./resources/gen/lpos";
		
		OrderingHeuristic post = new OrderingHeuristic(OrderingHeuristic.POSTORDER);
		ArrayList<Feature> postorder = post.orderFM(fm);
	//	cleanConfiguration(fm,postorder);
		writer.generate(postorder,"ERP-POSTORDER");

		OrderingHeuristic pre = new OrderingHeuristic(OrderingHeuristic.PREORDER);
		
		writer.generate(pre.orderFM(fm),"ERP-PREORDER");
		
		OrderingHeuristic alph = new OrderingHeuristic(OrderingHeuristic.ALPHABETICAL);
		writer.generate(alph.orderFM(fm),"ERP-ALPHABETICAL");
	
		OrderingHeuristic invalph = new OrderingHeuristic(OrderingHeuristic.INVALPHABETICAL);
		writer.generate(invalph.orderFM(fm),"ERP-INVALPHABETICAL");
	}

//	private static void cleanConfiguration(FAMAFeatureModel fm, ArrayList<Feature> postorder) {
//
//		ChocoReasoner reasoner = new ChocoReasoner();
//		fm.transformTo(reasoner);
//		Model chocoProblem=reasoner.getProblem();
//
//		for(Feature f:postorder) {
//			//Pillo el problema
//			//Aplico la feature actual
//			chocoProblem.addConstraint(Choco.eq(reasoner.getVariables().get(f.getName()), 1));
//			System.out.println(f.getName());
//			//Propago para ver cuales no se pueden seleccionar ya.
//			Solver solver = new CPSolver();
//			solver.read(chocoProblem);
//			try {
//				solver.propagate();
//				//valid = solver.solve();
//				
//				for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
//					IntDomainVar aux = solver.getVar(chocoProblem.getIntVar(i));
//					System.out.println(aux.getName()+" - "+aux.getSup()+" - "+aux.getInf()+" - "+aux.getVal()+" - "+aux.getDomainSize());
//					if (aux.getDomainSize()==1 && aux.getVal()==0) {
//						System.out.println("EUREKA");
//					}
//				}
//				
//			} catch (ContradictionException e) {}		
//			
//			
//			
//		}
//		
//		
//	}

}
