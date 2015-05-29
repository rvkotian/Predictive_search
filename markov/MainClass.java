package markov;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MainClass {

	public static void test1 () throws Exception {
//		String path = "C:/shanghai_taxi/positionIDbyCar/";
//		String filename = "00105.txt";
		String path = "C:/Users/adfa/Desktop/IR-Project/shanghai_taxi/shanghai_taxi/070202positionIDbyCar";
		String filename = "02949";
		ArrayList<ArrayList<TimePosition>> History = new ArrayList<ArrayList<TimePosition>>(); 
		ArrayList<ArrayList<TimePosition>> noHistory = new ArrayList<ArrayList<TimePosition>>();
		String noHistoryDate = "2007-02-01";
		int noHistoryTimestart = 0;
		int noHistoryTimeend = 120;
		String pathandfile = path + filename;
		double MaxLatitude = 32.0;
		double MinLatitude = 30.0;
		double MaxLongitude = 122.0;
		double MinLongitude = 120.0;
		double GridLength1 = 1.0;
		double GridLength2 = 1.0;
		double LatitudeGrid = 0.0090 * GridLength1;
		double LongitudeGrid = 0.0104 * GridLength2;
		int LatitudeGridNum = (int) ((MaxLatitude - MinLatitude) / LatitudeGrid);
		int LongitudeGridNum = (int) ((MaxLongitude - MinLongitude) / LongitudeGrid);
		
		ReadFile.divide(History, noHistory, noHistoryDate, noHistoryTimestart, noHistoryTimeend, pathandfile);
		
//		System.out.println("History:");
//		for (int i = 0; i < History.size(); i++) {
//			for (int j = 0; j < History.get(i).size(); j++) {
//				History.get(i).get(j).print_out();
//			}
//			System.out.println();
//		}
//		System.out.println("noHistory:");
//		for (int i = 0; i < noHistory.size(); i++) {
//			for (int j = 0; j < noHistory.get(i).size(); j++) {
//				noHistory.get(i).get(j).print_out();
//			}
//			System.out.println();
//		}
		
		int k = 3;
		
		Runtime runtime = Runtime.getRuntime();
		double sumaccuracy = 0.0;
		int count = 0;
		double begintime = System.currentTimeMillis();
		
		
		HashMap<String, HashMap<String, Double>> xij = Calculation.xij(History, k);
		
//		for (String start : xij.keySet()) {
//			for (String end : xij.get(start).keySet()) {
//				System.out.println(start + " " + end + " " + xij.get(start).get(end));
//			}
//		}
		
		
		for (int i = 0; i < noHistory.size(); i++) {
			ArrayList<TimePosition> continuedata = noHistory.get(i);
			if (continuedata.size() > k) {
				for (int j = k; j < continuedata.size(); j++) {
					String realDestination = continuedata.get(j).get_positionID();
					ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();
					for (int l = j - k; l < j; l++) {
						String thisposition = continuedata.get(l).get_positionID();
						knownpr.add(TimePosition.fixedPosition(thisposition));
					}
					HashMap<String, Double> predictedvector = Calculation.predictedvector(xij, knownpr, LatitudeGridNum, LongitudeGridNum);
					double thisaccuracy = 0.0;
					if (predictedvector.containsKey(realDestination)) thisaccuracy = predictedvector.get(realDestination);
					sumaccuracy = sumaccuracy + thisaccuracy;
					count++;
				}
			}
		}
		
		double avgaccuracy = sumaccuracy / count;
		double endtime = System.currentTimeMillis();
		double avgtime = (endtime - begintime) / count;
		double memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("avgaccuracy = " + avgaccuracy);
		System.out.println("avgtime = " + avgtime);
		System.out.println("memory = " + memory);
		System.out.println("count = " + count);
	}
	
	public static void MarkovAlgorithm (
			HashMap<Integer, String> time_position,
			HashMap<Integer, ArrayList<HashMap<String, Double>>> time_nprvectors,
			String path,
			String carID,
			int k,
			int n,
			String noHistoryDate,
			int noHistoryTimestart,
			int noHistoryTimeend
			) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(path + "GridNum.txt"));
		String oneline = br.readLine();
		StringTokenizer st = new StringTokenizer(oneline);
		st.nextToken();
		int LatitudeGridNum = Integer.parseInt(st.nextToken());
		oneline = br.readLine();
		st = new StringTokenizer(oneline);
		st.nextToken();
		int LongitudeGridNum = Integer.parseInt(st.nextToken());
		br.close();
		
		ArrayList<ArrayList<TimePosition>> History = new ArrayList<ArrayList<TimePosition>>(); 
		ArrayList<ArrayList<TimePosition>> noHistory = new ArrayList<ArrayList<TimePosition>>();
		String filename = carID + ".txt";
		String pathandfile = path + filename;
		
		ReadFile.divide(History, noHistory, noHistoryDate, noHistoryTimestart - k, noHistoryTimeend, pathandfile);
//		HashMap<String, HashMap<String, Double>> xij = Calculation.xij(History, k);
		
		for (int i = 0; i < noHistory.size(); i++) {
			ArrayList<TimePosition> continuedata = noHistory.get(i);
			if (continuedata.size() > 2 * k) {
				ArrayList<ArrayList<TimePosition>> history = new ArrayList<ArrayList<TimePosition>>();
				ArrayList<TimePosition> onelist = new ArrayList<TimePosition>();
				for (int j = 0; j < k; j++) {
					onelist.add(continuedata.get(j));
				}
				history.add(onelist);
				HashMap<String, HashMap<String, Double>> xij = Calculation.xij(history, k);
				for (int j = 2 * k; j < continuedata.size(); j++) {
					ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();
					for (int l = j - k; l < j; l++) {
						String thisposition = continuedata.get(l).get_positionID();
						knownpr.add(TimePosition.fixedPosition(thisposition));
					}
					ArrayList<HashMap<String, Double>> nprvectors = Calculation.nprvectors(xij, knownpr, n, LatitudeGridNum, LongitudeGridNum);
					int time = continuedata.get(j - 1).get_time();
					String positionID = continuedata.get(j - 1).get_positionID();
					time_position.put(time, positionID);
					time_nprvectors.put(time, nprvectors);
				}
			}
		}
	}
	
	public static void main (String[] args) throws Exception {
		MainClass.test1();
	}
	
}
