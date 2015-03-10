
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Calculation {

	public static HashMap<String, Double> predictedvectorbytrajectory (
			ArrayList<String> trajectory, 
			String lastposition, //last position
			double p,
			int LatitudeGridNum,
			int LongitudeGridNum) 
			{
		HashMap<String, Double> result = new HashMap<String, Double>();
		LinkedHashSet<String> adjacent = ComplicatedAdjacent.adjacent_8(trajectory.get(trajectory.size() - 1), LatitudeGridNum, LongitudeGridNum);


		if(lastposition.isEmpty()) {
			for (String positionID : adjacent) {		
				double pr = (double) 1.0 / (adjacent.size());
				result.put(positionID, pr);
			}
		}
		else {
			for (String positionID : adjacent) {
				double pro = 1.0;
			//	double lastdistance = TimePosition.distance(positionID, trajectory.get(0));
				
				double lastdistance = TimePosition.distance(positionID, lastposition);
				double thisdistance = TimePosition.distance(positionID, trajectory.get(0));
				
				if (thisdistance < lastdistance) {
					pro = pro * p;
				}
				else {
					pro = pro * (1.0 - p);
				}
				
				/*for (int i = 1; i < trajectory.size(); i++) {
					double thisdistance = TimePosition.distance(positionID, trajectory.get(i));
					if (thisdistance < lastdistance) {
						pro = pro * p;
					}
					else {
						pro = pro * (1.0 - p);
					}
			//		lastdistance = thisdistance;
				}
				*/
				result.put(positionID, pro);
			}
		}

		return result;
			}



	public static HashMap<String, Double> predictedvector (
			ArrayList<HashMap<String, Double>> knownpr, 
			String lastposition,
			double p,
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
			double thispr = kpower.get(start);
			ArrayList<String> trajectory = new ArrayList<String>();
			for (int i = 0; i < knownpr.size(); i++) {
				trajectory.add(start.substring(i * 6, i * 6 + 6));
			}

			HashMap<String, Double> prvec = Calculation.predictedvectorbytrajectory(trajectory, lastposition, p, LatitudeGridNum, LongitudeGridNum);
			for (String positionID : prvec.keySet()) {
				if (result.containsKey(positionID)) {
					result.put(positionID, result.get(positionID) + prvec.get(positionID) * thispr);
				}
				else {
					result.put(positionID, prvec.get(positionID) * thispr);
				}
			}
		}
		return result;
	}


}
