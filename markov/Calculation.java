package markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Calculation {

	public static HashMap<String, HashMap<String, Double>> xij (ArrayList<ArrayList<TimePosition>> History, int k) {
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String, Double>>();
		for (int i = 0; i < History.size(); i++) {
			ArrayList<TimePosition> continuedata = History.get(i);
			for (int j = k; j < continuedata.size(); j++) {
				String end = continuedata.get(j).get_positionID();
				String start = "";
				for (int l = j - k; l < j; l++) {
					start = start + continuedata.get(l).get_positionID();
				}
				if (result.containsKey(start)) {
					HashMap<String, Double> tmp = result.get(start);
					if (tmp.containsKey(end)) {
						tmp.put(end, tmp.get(end) + 1.0);
					}
					else {
						tmp.put(end, 1.0);
					}
				}
				else {
					HashMap<String, Double> tmp = new HashMap<String, Double>();
					tmp.put(end, 1.0);
					result.put(start, tmp);
				}
			}
		}
		for (String start : result.keySet()) {
			HashMap<String, Double> tmp = result.get(start);
			double sum = 0.0;
			for (String end : tmp.keySet()) {
				sum = sum + tmp.get(end);
			}
			for (String end : tmp.keySet()) {
				tmp.put(end, tmp.get(end) / sum);
			}			
		}
		return result;
	}
	
	public static HashMap<String, Double> predictedvector (
			HashMap<String, HashMap<String, Double>> xij, 
			ArrayList<HashMap<String, Double>> knownpr,
			int LatitudeGridNum,
			int LongitudeGridNum
			) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		HashMap<String, Double> kpower = new HashMap<String, Double>();
		kpower.put("", 1.0);
		for (int i = 0; i < knownpr.size(); i++) {
			HashMap<String, Double> tmp = new HashMap<String, Double>();
			HashMap<String, Double> thisvec = knownpr.get(i);
			for (String s1 : kpower.keySet()) {
				for (String s2 : thisvec.keySet()) {
					tmp.put(s1 + s2, kpower.get(s1) * thisvec.get(s2));
				}
			}
			kpower = tmp;
		}
		for (String start : kpower.keySet()) {
			double pr1 = kpower.get(start);
			HashMap<String, Double> tmp = new HashMap<String, Double>();
			if (xij.containsKey(start)) {
				tmp = new HashMap<String, Double>(xij.get(start));
			}
			else {
				HashSet<String> adjacent = TimePosition.adjacent(start.substring(start.length() - 6, start.length()), LatitudeGridNum, LongitudeGridNum);
				int count = adjacent.size();
				for (String positionID : adjacent) {
					tmp.put(positionID, 1.0 / ((int) count));
				}
			}
			for (String positionID : tmp.keySet()) {
				if (result.containsKey(positionID)) {
					result.put(positionID, result.get(positionID) + pr1 * tmp.get(positionID));
				}
				else {
					result.put(positionID, pr1 * tmp.get(positionID));
				}
			}
		}
		return result;
	}
	
	public static ArrayList<HashMap<String, Double>> nprvectors (
			HashMap<String, HashMap<String, Double>> xij, 
			ArrayList<HashMap<String, Double>> knownpr, 
			int n,
			int LatitudeGridNum,
			int LongitudeGridNum
			) {
		ArrayList<HashMap<String, Double>> result = new ArrayList<HashMap<String, Double>>();
		for (int i = 0; i < n; i++) {
			HashMap<String, Double> onevec = Calculation.predictedvector(xij, knownpr, LatitudeGridNum, LongitudeGridNum);
			result.add(onevec);
			knownpr.remove(0);
			knownpr.add(onevec);
		}
		return result;
	}
	
}
