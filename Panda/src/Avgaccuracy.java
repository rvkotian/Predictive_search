import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;




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
		String outdir = "dividedresult/";
		double sumaccuracy = 0.0;
		double avgaccuracy = 0.0;
		int count = 0;
		int c = 0;

		ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();

		ArrayList<String> builder = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		//	StringBuilder sb = new StringBuilder();
		String lastposition = "";

		int j = 1;
		String current = "";
		String next = "";
		String last = "";
		int i = 0;
		int n = data.size();

		double begintime = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();

		int knownprcount = 0;

		label1: while(i < data.size() - 1){

			//	for(i=0; i < data.size()-1; i++) {

			current = data.get(i);
			next = data.get(i+j);

			/* Checks if the next cell number is different from the current cell number*/
			if (!data.get(i+j).equals(data.get(i))){




				//	if (data.get(i+1).equals(data.get(i))) continue;
				LinkedHashSet<String> adjacent = ComplicatedAdjacent.adjacent_8(data.get(i), LatitudeGridNum, LongitudeGridNum);
				//if(!adjacent.contains(data.get(i+1))) continue;
				//	builder.add(data.get(i));

				/* Checks if the next cell is one of the adjacent cells of the current cell*/
				while(!adjacent.contains(data.get(i+j)))
				{	if((i+j) >= n - 1)
				{
					//System.out.println("End of file");
					break label1;
				}
				j = j + 1;
				}

					if(!last.equals("")){
					if(last.equals(data.get(i+j)))
					{
						//i = i ;
						j = j + 1;
						continue;
					}
				}
				 
				String thisposition = data.get(i);
				String realDestination = data.get(i+j);
				//		ArrayList<HashMap<String, Double>> knownpr = new ArrayList<HashMap<String, Double>>();
				knownpr.add(TimePosition.fixedPosition(thisposition));

				/* For 7 steps window*/
				if(knownpr.size() > 7){
					knownpr.clear();
					knownpr.add(TimePosition.fixedPosition(thisposition));
					lastposition = "";
				}

				HashMap<String, Double> predictedvector = Calculation.predictedvector(knownpr, lastposition, count, p, LatitudeGridNum, LongitudeGridNum);


				double thisaccuracy = 0.0;
				if (predictedvector.containsKey(realDestination)) thisaccuracy = predictedvector.get(realDestination);

				/* Making sure that it does not return back*/

			/*	Iterator it = adjacent.iterator();
				double minacc = thisaccuracy;
				while(it.hasNext()){
					String temp = (String) it.next();
					if(minacc > predictedvector.get(temp)){
						minacc = predictedvector.get(temp);
					}
					
					if(thisaccuracy > minacc)
					{
						j = j + 1;
						break label1;
					}
				}
			*/		


				

				//sumaccuracy = sumaccuracy + thisaccuracy;

				count++;

				//	builder.add((count + " " + data.get(i) + " " + data.get(i+1) + " " + thisaccuracy + "\r\n"));
				sb.append(count + " " + data.get(i) + " " + data.get(i+j) + " " + thisaccuracy + "\r\n");

				System.out.println(count + " " + "Current cell : " + data.get(i) + " at time " + timeList.get(i) + " Next cell : " + data.get(i+j) + " accuracy is : " + thisaccuracy);


				lastposition = thisposition;	
				last = data.get(i);

				if(count % 7 == 0){
					count =  0;
					c = c + 1;
				}

				if(count == 0){
					sumaccuracy = sumaccuracy + thisaccuracy;
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
			} 
		 */
		String resultfile =  Integer.toString(c+1);
		resultfile = resultfile + ".txt";
		WriteFile.writefile(root + outputdirectory + outdir + resultfile, sb);
		sb.delete(0, sb.length());
		//	c = c+1;

		//	WriteFile.writefile(root + outputdirectory + "result.txt", sb);



		avgaccuracy = avgaccuracy / (c);
		System.out.println(" accuracy is : " + avgaccuracy);
		//	double avgaccuracy = sumaccuracy / count;
		double endtime = System.currentTimeMillis();
		double avgtime = (endtime - begintime);
		//	double avgtime = (endtime - begintime) / count;
		double memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("The average accuracy is " + avgaccuracy);
		System.out.println("The average time is " + avgtime);
		System.out.println("The used memory is " + memory);
		System.out.println("The number of data is " + count);
	}
}

