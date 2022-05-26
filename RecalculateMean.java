import java.util.ArrayList;
import java.util.HashMap;

public class RecalculateMean {
	
	public static HashMap<String, Double> meanForSolution(RunConstructiveHeuristic rnCE ,
			ArrayList<Solution> solutions, int numberOfDistributionTimesForRobustSolution) {
		
		HashMap<String, ArrayList<Double>> mapAux = new HashMap<>();
		
        for(int i = 0; i < numberOfDistributionTimesForRobustSolution; i++) {
        	
        	String idString = "";
        	
        	//Recalculo las durationTasks tantas veces como numberOfDistributionTimesForRobustSolution
        	rnCE.getProblemData().refreshDistributions(i == 0 ? true : false);
        	
        	//Calculo la nueva perdida para cada nuevas duration
        	for(Solution sol: solutions) {
        		idString = sol.getId() + "-" + sol.getIdGrasp();
        		sol.recalculateRouteForNewDuration();
        		sol.calculateLoss();
        		//System.out.println("::::::::::::::::::::::");
        		//System.out.println(idString);
        		//System.out.println(sol.getLoss());
        		if(!(mapAux.containsKey(idString))) {
        			mapAux.put(idString, new ArrayList<>());
        		}
        		mapAux.get(idString).add(sol.getLoss());
        	}
        	
        }
        HashMap<String, Double> solMean = calculateMean(mapAux);
        return solMean;
		
	}
	
	public static HashMap<String, Double> calculateMean(HashMap<String, ArrayList<Double>> map){
		HashMap<String, Double> solMean = new HashMap<>();
		for(String solID: map.keySet()) {
			ArrayList<Double> means = map.get(solID);
			int size = means.size();
			double total = 0;
			for(Double mean: means) {
				total += mean;
			}
			double value = total/size;
			solMean.put(solID, value);
			
		}
		return solMean;
	}
	
	public static Solution getBestSolution(HashMap<String, Double> solutions, ArrayList<Solution> allSolutions) {
		Solution solRet = null;
		double aux = Double.MAX_VALUE;
		String keyAux ="";
		for(String key: solutions.keySet()) {
			double mean = solutions.get(key);
			if(mean < aux) {
				aux = mean;
				keyAux = key;
			}
		}
		String[] vec = keyAux.split("-");
		int id = Integer.valueOf(vec[0]);
		int idGrasp = Integer.valueOf(vec[1]);
		for(Solution s: allSolutions) {
			if(s.getId() == id && s.getIdGrasp() == idGrasp) {
				solRet = s;
				break;
			}
			
		}
		
		return solRet;
	}
}
