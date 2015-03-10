import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


public class Avgaccuracy {

	public static void test_acc(
			) throws Exception

	{
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/RKotian/Documents/Information Retrieval/Project/shanghai_taxi/shanghai_taxi/positionIDbyCar/02210.txt"));
		String oneline = br.readLine();
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<String> timeList = new ArrayList<String>();
		ArrayList<String> data = new ArrayList<String>();
		while (oneline != null && oneline.length() > 0) {
			String[] s = oneline.split(" ");
			dateList.add(s[0]);
			timeList.add(s[1]);
			data.add(s[2]);
			oneline = br.readLine();
		}

		br.close();

		br = new BufferedReader(new FileReader("C:/Users/RKotian/Documents/Information Retrieval/Project/shanghai_taxi/shanghai_taxi/positionIDbyCar/GridNum.txt"));
		oneline = br.readLine();
		int LatitudeGridNum = Integer.parseInt(oneline.split(" ")[1]);
		oneline = br.readLine();
		int LongitudeGridNum = Integer.parseInt(oneline.split(" ")[1]);
		br.close();
		System.out.println("LatitudeGridNum = " + LatitudeGridNum + "  LongitudeGridNum = " + LongitudeGridNum +"\n");


		double p = 0.9;
		String root = "C:/Users/RKotian/Documents/Information Retrieval/Project/shanghai_taxi/shanghai_taxi/";
		String outputdirectory = "positionIDbyCar/";
		String outdir = "dividedresult/withp/";
		double sumaccuracy = 0.0;
		double avgaccuracy = 0.0;
		int count = 0;
		int c = 0;
		HashMap<String, Double> prvector = new HashMap<String, Double>();
		ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();
		StringBuffer sb = new StringBuffer();
		//StringBuilder sb = new StringBuilder();
		//ArrayList<String> builder = new ArrayList<String>();
		String lastposition = "";
		int j = 1;
		String current = "";
		String next = "";
		int i = 0;
		int n = data.size();

		int knownprcount = 0;

		label1: while(i < data.size() - 1){
			//for(i=0; i < data.size()-1; i++) {
			//		if (data.get(i+1).equals(data.get(i))) continue;
			current = data.get(i);
			next = data.get(i+j);
			/* Jumps to next line of input if the cell number is same as previous*/
			if (!data.get(i+j).equals(data.get(i))){
				LinkedHashSet<String> adjacent = ComplicatedAdjacent.adjacent_8(data.get(i), LatitudeGridNum, LongitudeGridNum);
				//	if(!adjacent.contains(data.get(i+j)))
				/* Checks if the next cell in the input is one of the adjacent cells of current cell*/
				while(!adjacent.contains(data.get(i+j)))
				{	if((i+j) >= n - 1)
				{
					//System.out.println("End of file");
					break label1;
				}
				j = j + 1;
				}

				String thisposition = data.get(i);
				String realDestination = data.get(i+j);
				//		ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();
				knownpr.add(TimePosition.fixedPosition(thisposition));
				knownprcount = knownprcount + 1;

				/* Taking 7 steps at a time*/
				if (knownprcount > 7)
				{
					lastposition = "";
					knownprcount = 1;
				}


				HashMap<String, Double> predictedvector = Calculation.predictedvector(knownpr, lastposition, p, LatitudeGridNum, LongitudeGridNum);

				double thisaccuracy = 0.0;
				if (predictedvector.containsKey(realDestination)) thisaccuracy = predictedvector.get(realDestination);
				//	sumaccuracy = sumaccuracy * thisaccuracy;

				count++;
				sb.append(count + " " + data.get(i) + " " + data.get(i+j) + " " + thisaccuracy + "\r\n");
				//builder.add((count + " " + data.get(i) + " " + data.get(i+1) + " " + thisaccuracy + "\r\n"));

				System.out.println(count + " Current cell : " + data.get(i) + " at time " + timeList.get(i) + " Next cell : " + data.get(i+j) + " accuracy is : " + thisaccuracy);
				//	WriteFile.writefile(root + outputdirectory + "result.txt", sb);
				lastposition = thisposition;

				if(count % 7 == 0){
					count =  0;
					c = c + 1;
				}

				if(count == 0){
					String resultfile =  Integer.toString(c);
					resultfile = resultfile + ".txt";
					WriteFile.writefile(root + outputdirectory + outdir + resultfile, sb);
					sb.delete(0, sb.length());
				}
			}

			i = i + j;
			j = 1;
		}






		/*		for(int k=0;k<builder.size();k++) {
			int maxSubList = builder.subList(k,builder.size()).size();
			if(maxSubList<7) break;  //exit loop if subList size < 7

			List<String> subList = builder.subList(k, (k+7));
		//	System.out.println(subList.toString());

			for(int j=0;j<subList.size();j++) {
				sb.append(subList.get(j));
				String[] s = subList.get(j).split(" ");
				double in = Double.parseDouble (s[3]);
				sumaccuracy = sumaccuracy + in;
			} */

		String resultfile =  Integer.toString(c+1);
		resultfile = resultfile + ".txt";
		WriteFile.writefile(root + outputdirectory + outdir + resultfile, sb);
		//	sb.delete(0, sb.length());
		//	c = c+1;

		//avgaccuracy = avgaccuracy + (sumaccuracy/7.0);


		avgaccuracy = sumaccuracy / (c+1);
		System.out.println(" accuracy is : " + avgaccuracy);
	}
}


