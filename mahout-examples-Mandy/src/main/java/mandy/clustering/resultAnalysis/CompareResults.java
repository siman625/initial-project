package mandy.clustering.resultAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

import mandy.clustering.resultAnalysis.PointsIndexResult;

public class CompareResults {

	private TreeMap<String, ArrayList<Integer>> clusterIdtoIndex1;
	private TreeMap<String, ArrayList<Integer>> clusterIdtoIndex2;
	private PointsIndexResult result1;
	private PointsIndexResult result2;
	private final String[] keyArray1 = { "1379:", "1054:", "633:", "4465:" }; // jan25:
																				// "1655:",
																				// "1745:",
																				// "613:",
																				// "553:"
	private final String[] keyArray2 = { "4577:", "3096:", "4048:", "1025:" };
	private int[] oneInTwo = { 0, 0, 0, 0 };
	private int[] oneNotInTwo = { 0, 0, 0, 0 };
	// private int[] twoInOne;
	private int[] twoNotInOne = { 0, 0, 0, 0 };

	public void run() throws IOException {

		String fileName1 = "/home/mansi/Desktop/clusteredPoints-jan27";
		String fileName2 = "/home/mansi/Desktop/clusteredPoints-jan26";
		result1 = new PointsIndexResult(fileName1);
		result2 = new PointsIndexResult(fileName2);
		clusterIdtoIndex1 = result1.getClusterIdtoIndex();
		clusterIdtoIndex2 = result2.getClusterIdtoIndex();

		for (int i = 0; i < keyArray1.length; i++) {
			String key1 = keyArray1[i];
			String key2 = keyArray2[i];
			// /System.out.println(clusterIdtoIndex1);
			// System.out.println(key1);
			// System.out.println(clusterIdtoIndex1.keySet());
			// if (clusterIdtoIndex1.containsKey(key1)) {
			// int a = 0;

			// }
			int k = 0;
			for (int j = 0; j < clusterIdtoIndex1.get(key1).size();) {
				System.out.println("j size:"
						+ clusterIdtoIndex1.get(key1).size());//
				if (k >= clusterIdtoIndex2.get(key2).size()) {
					while (j < clusterIdtoIndex1.get(key1).size()) {
						j++;
						oneNotInTwo[i]++;
					}
					break;
				}

				for (k = 0; k < clusterIdtoIndex2.get(key2).size();)
 {
					// System.out.println("k size:"
					// + clusterIdtoIndex2.get(key2).size());//
					if (j < clusterIdtoIndex1.get(key1).size()) {
						if (clusterIdtoIndex1.get(key1).get(j) < clusterIdtoIndex2
							.get(key2).get(k)) {
						oneNotInTwo[i]++;
						j++;
						} else if (clusterIdtoIndex1.get(key1).get(j) > clusterIdtoIndex2
							.get(key2).get(k)) {
							twoNotInOne[i]++;
						k++;
						} else {
						oneInTwo[i]++;
						j++;
						k++;
						}
					}
 else {
						// oneNotInTwo[i] += (clusterIdtoIndex2.get(key2).size()
						// - k);
						// break;
						while (k < clusterIdtoIndex2.get(key2).size()) {
							twoNotInOne[i]++;
							k++;
						}
					}
				}
			}

		}
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/TestResults.txt", true));


		resultDisplay(out);
		out.close();

	}


	private void resultDisplay(PrintWriter out0) {

		String output = "";
		for (int i = 0; i < oneInTwo.length; i++) {
			output = output + " one in two:" + oneInTwo[i] + " one not in two:"
					+ oneNotInTwo[i] + " two not in one:" + twoNotInOne[i];

			System.out.println(output);
			out0.println(output);
			output = "";
		}

	}

	public static void main(String[] args) throws IOException {
		CompareResults cr = new CompareResults();
		cr.run();

	}



}
